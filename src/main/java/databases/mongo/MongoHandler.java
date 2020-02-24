package databases.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import datehelper.DateHelper;
import java.util.ArrayList;
import java.util.List;
import models.ActivityModel;
import models.ActivityModel.SensorData;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoHandler {
  private final Logger logger = LoggerFactory.getLogger(MongoHandler.class);
  private MongoClient mongoClient = new MongoClient("localhost" , 27017);
  private MongoDatabase mongoDatabase = mongoClient.getDatabase("ubiqlog2");
  private MongoCollection<Document> activityCollection = mongoDatabase.getCollection("ActivitySensorData");

  /** Method to convert an activity model to a document */
  public Document convertActivityModelToDocument(ActivityModel model) {
    logger.debug("Converting {} to a mongo document", model.getSensorName());
    String timestamp = model.getTimestamp();
    String formattedDate = model.getFormatted_date();
    Integer stepCount = model.getSensorData().getStepCounts();
    Integer stepDelta = model.getSensorData().getStepDelta();
    return new Document("timestamp", timestamp).append("formatted_date", formattedDate)
        .append("steps", stepCount).append("step_delta", stepDelta);
  }

  /**
   * Call to insert a list of activity models into the database */
  public void insertDocumentsToCollection(List<ActivityModel> models) {
    logger.debug("Inserting list of models into mongo collection");
    for (ActivityModel model : models) {
      Document document = convertActivityModelToDocument(model);
      // insert it into the collection
      activityCollection.insertOne(document);
    }
  }

  public List<ActivityModel> queryBasedOnDate(String dateInput) {
    String startDate = DateHelper.convertUserInputToISOFormat(dateInput);
    String endingDate = DateHelper.getDayEndingDate(startDate);
    BasicDBObject query = new BasicDBObject("formatted_date", new BasicDBObject("$gte", startDate).append("$lte", endingDate));
    MongoCursor<Document> cursor = activityCollection.find(query).cursor();

    List<ActivityModel> models = new ArrayList<>();
    try {
      while(cursor.hasNext()) {
        Document document = cursor.next();
        // get the data from the document
        String formatted_date = document.getString("formatted_date");
        String timestamp = document.getString("timestamp");
        Integer stepCount = document.getInteger("stepCounts");
        Integer stepDelta = document.getInteger("stepDelta");

        ActivityModel model = new ActivityModel();
        SensorData sensorData = new SensorData();

        sensorData.setStepCounts(stepCount);
        sensorData.setStepDelta(stepDelta);
        model.setFormattedDate(formatted_date);
        model.setTime_stamp(timestamp);
        model.setSensorData(sensorData);

        // add the model to the list
        models.add(model);
      }
    } catch (Exception e) {
      logger.error("Error ===> " + e.getMessage());
      e.printStackTrace();
    } finally{
      cursor.close();
    }
    return models;
  }
}
