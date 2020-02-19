package lucene;

import constants.LuceneConstants;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import models.ActivityModel;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneManager {
  private static final Logger logger = LoggerFactory.getLogger("LuceneManager");
  private static String indexDir;
  private static IndexWriter indexWriter;

  /**
   * Call to get instance of IndexWriter
   *
   * @return index writer
   */
  private static IndexWriter getIndexWriter() {
    logger.debug("Getting Index Writer");
    try {
      // 0. Specify the analyzer for tokenizing text.
      // 1. create the index
      Directory dir = FSDirectory.open(Paths.get(indexDir));

      // The same analyzer should be used for indexing and searching
      StandardAnalyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig config = new IndexWriterConfig(analyzer);
      indexWriter = new IndexWriter(dir, config);
      return indexWriter;
    } catch (IOException e) {
      logger.error("Error : {}", e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Call to close the index writer
   *
   * */
  private static void closeWriter() {
    logger.debug("Closing Index Writer");
    if (indexWriter != null) {
      try {
        indexWriter.close();
      } catch (IOException e) {
        logger.error("Error : {}", e.getMessage());
        e.printStackTrace();
      }
    }
  }

  /**
   * Call to store given list of activity sensor data
   *
   * @param models given list of activity models to add to lucene
   */
  public static void storeActivitySensorData(List<ActivityModel> models) {
    logger.debug("Storing activity model in lucene");
    try {
      IndexWriter indexWriter = getIndexWriter();
      if (indexWriter != null) {
        for (ActivityModel model : models) {
          addActivityDoc(indexWriter, model);
        }
      }
      closeWriter();
    } catch (IOException e) {
      logger.error("Error : {}", e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Helper to insert given ActivitySensorData using the given IndexWriter in a document
   *
   * @param w          given IndexWriter
   * @param model given activitiy model object
   * @throws IOException
   */
  private static void addActivityDoc(IndexWriter w, ActivityModel model) throws IOException {
    Document doc = new Document();
    doc.add(new IntPoint(LuceneConstants.STEP_COUNT, model.getSensorData().getStepCounts()));
    doc.add(new IntPoint(LuceneConstants.STEP_DELTA, model.getSensorData().getStepDelta()));
    doc.add(new StringField(LuceneConstants.SENSOR_NAME, model.getSensorName(), Field.Store.YES));
    doc.add(new StringField(LuceneConstants.FORMATTED_DATE, model.getFormatted_date(), Field.Store.YES));
    // use a string field for timestamp because we don't want it tokenized
    doc.add(new StringField(LuceneConstants.TIMESTAMP, model.getTimestamp(), Field.Store.YES));
    w.addDocument(doc);
  }

}
