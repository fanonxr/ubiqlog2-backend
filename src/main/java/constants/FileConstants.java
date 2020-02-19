package constants;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FileConstants {
  public static Path baseDir = FileSystems.getDefault().getPath(".").toAbsolutePath();
  public static String jsonPathActivity =
      "/Users/fanonxrogers/Documents/BUCourses/research/java-ubiqlog2/data/json-data/activity.json";
}
