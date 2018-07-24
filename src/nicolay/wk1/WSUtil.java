package nicolay.wk1;

import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URL;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class used to gather data from a web service.
 */
public class WSUtil {

  /**
   * Method to connect to a given url, process JSON data, and  return that data as a List of Quote
   * objects
   * @param urlString the url from which to gather data
   * @return a list of Quote objects representing the JSON data at urlString
   */
  public static List<Quote> getQuotes(String urlString) {
    List<Quote> quotes = null;
    try {
      URL url = new URL(urlString);
      ObjectMapper jsonMapper = new ObjectMapper();
      // read JSON array in and save as a List<Quote>
      quotes = jsonMapper.readValue(url, new TypeReference<List<Quote>>(){});
    } catch (Exception e) {
      System.err.println(e);
    }
    return quotes;
  }
}
