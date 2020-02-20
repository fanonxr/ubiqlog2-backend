package databases;

import datehelper.DateHelper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.ActivityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLManager {
  private static final Logger logger = LoggerFactory.getLogger("MySQLManager");

  private Connection connection;

  // sql constants
  private static final String ACTIVITY_TABLE = "ActivitySensorData";

  /**
   * Method to connect to the mySQL database.
   *
   * @return a SQL connection object refer to that database.
   */
  public Connection connectToDB() {
    try {
      if (connection == null) {
        // establish the connection to the database
        this.connection = DriverManager
            .getConnection(SQLConstants.DB_URL, SQLConstants.USER, SQLConstants.PASSWORD);
        logger.info("Connected to Database.");
        return connection;
      }
    } catch (Exception e) {
      logger.debug("Error: {}", e.getMessage());
      e.printStackTrace();
    }
    return this.connection;
  }

  /**
   * Helper method to initialize the mysql database with necessary data
   * */
  private void init(Connection connection) {
    logger.info("Init setup => creating tables");
    try {
      Statement statement = connection.createStatement();
      statement.execute(MySQLQueries.createActivityTable);
    } catch (Exception se) {
      logger.error("Error: {}", se.getMessage());
    }
  }

  /**
   * Call to insert given list data into ActivitySensorData table in MySQL
   *
   * @param sensorDataList given data list
   */
  public void insertIntoActivityTable(List<ActivityModel> sensorDataList) {
    logger.debug("Preparing to insert into Activity table");
    connection = connectToDB();
    // create the mysql insert prepared statement
    PreparedStatement preparedStmt;
    try {
      for (ActivityModel sensorData : sensorDataList) {
        preparedStmt = connection.prepareStatement(MySQLQueries.insertIntoActivityTable);
        preparedStmt.setString(1, sensorData.getTimestamp());
        preparedStmt.setString(2, sensorData.getFormatted_date());
        preparedStmt.setString(3, sensorData.getSensorName());
        preparedStmt.setInt(4, sensorData.getSensorData().getStepCounts());
        preparedStmt.setInt(5, sensorData.getSensorData().getStepDelta());
        // execute the prepared statement
        preparedStmt.execute();
      }
    } catch (SQLException e) {
      logger.error("Error: {}", e.getMessage());
      e.printStackTrace();
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        logger.error("Error: {}", e.getMessage());
        e.printStackTrace();
      }
    }
  }

  /**
   * Call to query the mysql database
   * */
  public ArrayList<ActivityModel> getActivitySensorDataForGivenDate(String userDate) {
    // extract dates
    String startingDate = DateHelper.convertUserInputToISOFormat(userDate);
    String endingDate = DateHelper.getDayEndingDate(startingDate);
    String sd = "'" + startingDate + "'";
    String ed = "'" + endingDate + "'";
    connection = connectToDB();
//    String query = new StringBuilder().append("SELECT * FROM ").append(ACTIVITY_TABLE)
//        .append(" WHERE formatted_date LIKE '")
//        .append(DateHelper.convertUserInputToISOFormat(userDate)).append("' ORDER BY step_counts DESC LIMIT 1").toString();
    String query = new StringBuilder("SELECT * FROM ").append(ACTIVITY_TABLE)
        .append(" WHERE formatted_date BETWEEN ").append(sd)
        .append(" AND ").append(ed).toString();


    ArrayList<ActivityModel> resultSet = new ArrayList<>();
    // create the java statement
    Statement st;
    try {
      st = connection.createStatement();
      ResultSet rs = st.executeQuery(query);

      while (rs.next()) {
        ActivityModel data = new ActivityModel();
        data.setTimestamp(rs.getString("time_stamp"));
        data.setTime_stamp(rs.getString("time_stamp"));
        data.setSensorName(rs.getString("sensor_name"));
        ActivityModel.SensorData sensorData = new ActivityModel.SensorData();
        sensorData.setStepCounts(rs.getInt("step_counts"));
        sensorData.setStepDelta(rs.getInt("step_delta"));
        data.setSensorData(sensorData);
        data.setFormattedDate(rs.getString("formatted_date"));
        resultSet.add(data);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    // execute the query, and get a java resultset
    return resultSet;
  }

  /** Helper class to execute queries to the database*/
  private static class MySQLQueries {
    static final String createActivityTable = new StringBuilder().append("CREATE TABLE ")
        .append(ACTIVITY_TABLE).append("(time_stamp VARCHAR(30) , ")
        .append(" sensor_name CHAR(25) , ").append(" formatted_date CHAR(40) , ")
        .append(" step_counts INTEGER, ").append(" step_delta INTEGER)").toString();

    static final String insertIntoActivityTable = new StringBuilder().append(" insert into ")
        .append(ACTIVITY_TABLE)
        .append(" (time_stamp, formatted_date, sensor_name, step_counts, step_delta)")
        .append(" values (?, ?, ?, ?, ?)").toString();
  }

}
