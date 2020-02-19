package models;

import datehelper.DateHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivityModel {

  @SerializedName("sensor_name")
  @Expose
  private String sensorName;
  @SerializedName("timestamp")
  @Expose
  private String timestamp;
  @SerializedName("time_stamp")
  @Expose
  private String time_stamp;
  @SerializedName("formatted_date")
  @Expose
  private String formatted_date;
  @SerializedName("sensor_data")
  @Expose
  private SensorData sensorData;

  public String getSensorName() {
    return sensorName;
  }

  public void setSensorName(String sensorName) {
    this.sensorName = sensorName;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
    this.formatted_date = DateHelper.convertTimestampToISOFormat(timestamp);
  }

  public String getTime_stamp() {
    return time_stamp;
  }

  public String getFormatted_date() {
    return formatted_date;
  }

  public void setFormattedDate(String isoTimestamp) {
    this.formatted_date = isoTimestamp;
  }

  public void setTime_stamp(String time_stamp) {
    this.time_stamp = time_stamp;
    this.formatted_date = DateHelper.convertTimestampToISOFormat(time_stamp);
  }

  public SensorData getSensorData() {
    return sensorData;
  }

  public void setSensorData(SensorData sensorData) {
    this.sensorData = sensorData;
  }

  public static class SensorData {

    @SerializedName("step_counts")
    @Expose
    private Integer stepCounts;
    @SerializedName("step_delta")
    @Expose
    private Integer stepDelta;

    public Integer getStepCounts() {
      return stepCounts;
    }

    public void setStepCounts(Integer stepCounts) {
      this.stepCounts = stepCounts;
    }

    public Integer getStepDelta() {
      return stepDelta;
    }

    public void setStepDelta(Integer stepDelta) {
      this.stepDelta = stepDelta;
    }
  }
}
