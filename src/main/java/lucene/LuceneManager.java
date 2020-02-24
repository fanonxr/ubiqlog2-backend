package lucene;

import constants.LuceneConstants;
import datehelper.DateHelper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import models.ActivityModel;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneManager {
  private static final Logger logger = LoggerFactory.getLogger("LuceneManager");
  private static String indexDir = "/Users/fanonxrogers/Documents/BUCourses/research/java-ubiqlog2/data/lucene-data";
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
   * @throws IOException Io exception
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

  /**
   * Called to perform search in Lucene in the given indexDirectory for the given query string and the given Index Value
   *
   * @param queryStr   given query string
   * @param indexValue given index value
   * @return time in milliseconds to perform the query
   */
  private static List<Document> getLuceneQueryTime(String queryStr, String indexValue) {
    List<Document> results = new ArrayList<>();
    try {
      // 0. Specify the analyzer for tokenizing text.
      // The same analyzer should be used for indexing and searching
      StandardAnalyzer analyzer = new StandardAnalyzer();
      // 1. create the index
      Directory index = FSDirectory.open(Paths.get(indexDir));

      Query q = new QueryParser(indexValue, analyzer).parse(queryStr);
      // 3. search
      int hitsPerPage = 100;
      IndexReader reader = DirectoryReader.open(index);
      IndexSearcher searcher = new IndexSearcher(reader);
      TopDocs docs = searcher.search(q, hitsPerPage);

      ScoreDoc[] hits = docs.scoreDocs;

      logger.debug("Docs hit = {}", hits.length);
      for (ScoreDoc hit : hits) {
        int docId = hit.doc;
        Document d = searcher.doc(docId);
        results.add(d);
      }
      // reader can only be closed when there
      // is no need to access the documents any more.
      reader.close();
    } catch (ParseException | IOException e) {
      e.printStackTrace();
    }
    logger.debug("Search time for " + queryStr + " for index " + indexValue + " is... ");
    return results;
  }

  /**
   * Called to query the number of steps user takes for the given day
   *
   * @param userDate given day
   * @return step count for the given day
   */
  public static int queryForTotalStepsInDay(String userDate) {
    String formattedDate = DateHelper.convertUserInputToISOFormat(userDate);
    List<Document> results = getLuceneQueryTime("Activity", LuceneConstants.SENSOR_NAME);
    int maxStepCount = 0;    // Max value of step count for the day
    for (Document doc :
        results) {
      logger.debug("Document Found");
      if (doc.get(LuceneConstants.FORMATTED_DATE) != null
          && doc.get(LuceneConstants.FORMATTED_DATE).equals(formattedDate)
          && doc.get(LuceneConstants.STEP_COUNT) != null
          && Integer.parseInt(doc.get(LuceneConstants.STEP_COUNT)) > maxStepCount) {
        maxStepCount = Integer.parseInt(doc.get(LuceneConstants.STEP_COUNT));
      }
    }
    return maxStepCount;
  }

  public static void hardCodedDateQuery(String userDate) {
    List<Document> results = getLuceneQueryTime("Activity", LuceneConstants.SENSOR_NAME);
    int maxStepCount = 0;    // Max value of step count for the day
    for (Document doc :
        results) {
      logger.debug("Document Found");
      if (doc.get(LuceneConstants.FORMATTED_DATE) != null
          && doc.get(LuceneConstants.FORMATTED_DATE).equals(userDate)
          && doc.get(LuceneConstants.STEP_COUNT) != null
          && Integer.parseInt(doc.get(LuceneConstants.STEP_COUNT)) > maxStepCount) {
        maxStepCount = Integer.parseInt(doc.get(LuceneConstants.STEP_COUNT));
      }
    }
    logger.debug("Max step count = {}", maxStepCount);
  }

}
