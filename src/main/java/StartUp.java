import databases.MySQLManager;
import datehelper.DateHelper;
import java.util.List;
import models.ActivityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartUp {
  private static final Logger logger = LoggerFactory.getLogger("StartUp");
  public static void main(String[] args) {
    logger.debug("Testing run");
    // List<ActivityModel> models = FileJsonParser.parseActivityModels(FileConstants.jsonPathActivity);
    // logger.debug("timestamp time = {}", models.get(0).getTimestamp());

    // Testing for my SQL
    MySQLManager sqlManager = new MySQLManager();
    // sqlManager.insertIntoActivityTable(models);
    // getting a single piece of data
    List<ActivityModel> activityModels = sqlManager.getActivitySensorDataForGivenDate("3-6-2017");
    logger.debug(DateHelper.convertUserInputToISOFormat("3-6-2017"));
    logger.debug("activity models size = " + activityModels.size());
  }
}
