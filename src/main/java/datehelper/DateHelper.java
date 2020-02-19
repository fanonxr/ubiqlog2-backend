package datehelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class DateHelper {

  private static final Logger logger = LoggerFactory.getLogger("datehelper");

  /** Helper method to extract the date from user if they type in date in natural Language format
   * example: march 4 2020
   * @param userInput a string of the user input
   * */
  public static String extractDateNLP(String userInput) {
    // Using Regex to extract date from input
    Pattern p = Pattern.compile("-?\\d+");
    Matcher m = p.matcher(userInput);
    StringBuilder dateBuilder = new StringBuilder();
    while (m.find()) {
      dateBuilder.append(m.group());
    }
    return dateBuilder.toString();
  }

  /** Helper method to convert the format the input date to ISO format
   * @param dateInput the input that will be converted
   * */
  public static String convertTimestampToISOFormat(String dateInput) {
    DateTimeFormatter inputTimeFormat = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss zzz yyyy");
    DateTimeFormatter ISOFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime convertedTime = LocalDateTime.parse(dateInput, inputTimeFormat);
    return convertedTime.format(ISOFormat);
  }

  /** Helper method to convert the user input to ISO Format
   * @param userInput the date the user requests */
  public static String convertUserInputToISOFormat(String userInput) {
    // userInput = reverseDateForISOFormat(userInput);
    userInput = extractDateNLP(userInput);
    DateTimeFormatter inputTimeFormat = DateTimeFormatter.ofPattern("M-d-uuuu");
    DateTimeFormatter ISOFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime convertedTime = LocalDate.parse(userInput, inputTimeFormat).atStartOfDay();
    return convertedTime.format(ISOFormat);
  }
}
