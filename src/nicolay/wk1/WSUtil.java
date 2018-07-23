package nicolay.wk1;

import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URL;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WSUtil {
  public static List<Quote> getQuotes(String urlString) {
    List<Quote> quotes = null;
    try {
      URL url = new URL(urlString);
      ObjectMapper jsonMapper = new ObjectMapper();
      quotes = jsonMapper.readValue(url, new TypeReference<List<Quote>>(){});
    } catch (Exception e) {
      System.err.println(e);
    }
  return quotes;
  }
}
