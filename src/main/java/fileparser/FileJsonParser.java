package fileparser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import datehelper.DateHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import models.ActivityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileJsonParser {
  private static final Logger logger = LoggerFactory.getLogger("FileJsonParser");
  private static Gson gson = new Gson();

  /** method to parse the activity json file
   * @param jsonFilePath file path to the json file
   * */
  public static List<ActivityModel> parseActivityModels(String jsonFilePath) {
    logger.debug("parseActivityModels | starts");
    List<ActivityModel> activityModels = null;
    try {
      InputStream inputStream = new FileInputStream(new File(jsonFilePath));
      Reader reader = new InputStreamReader(inputStream);
      Type activityListType = new TypeToken<ArrayList<ActivityModel>>(){}.getType();
      activityModels = gson.fromJson(reader, activityListType);
      addFormattedTimeToEachObject(activityModels);
    } catch (Exception e) {
      logger.error("Error : {}", e.getMessage());
      e.printStackTrace();
    }
    logger.debug("parseActivityModels | ends");
    return activityModels;
  }

  /**
   * Helper method to assign the formatted timestamp to all objects within the list */
  private static void addFormattedTimeToEachObject(List<ActivityModel> models) {
    for (ActivityModel model: models) {
      String timestamp = model.getTimestamp();
      // convert the time stamp
      String isoTimeStamp = DateHelper.convertTimestampToISOFormat(timestamp);
      // assign the timestamp to the object
      model.setFormattedDate(isoTimeStamp);
    }
  }

}


