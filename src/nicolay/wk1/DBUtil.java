package nicolay.wk1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBUtil {
  public static boolean saveQuotesToDatabase(List<Quote> quotes, Connection connection) {
    String query = getInsertQuotesQuery(quotes);

    try (
      Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      ResultSet rs = stmt.executeQuery(query)
    ) {
      return true;
    } catch (SQLException e) {
      System.err.println(e);
      return false;
    }
  }

  private static String getInsertQuotesQuery(List<Quote> quotes) {
    StringBuilder query = new StringBuilder();
    query.append("INSERT INTO quotes(symbol, price, volume, date) VALUES ");

    for (Quote quote : quotes) {
      query.append("(");
      query.append(quote.getSymbol());
      query.append(", ");

      query.append(quote.getPrice());
      query.append(", ");

      query.append(quote.getVolume());
      query.append(", ");

      query.append(quote.getDate());
      if (quotes.indexOf(quote) == quotes.size()-1) {
        query.append(");");
      } else {
        query.append("),");
      }
    }

    return query.toString();
  }
}
