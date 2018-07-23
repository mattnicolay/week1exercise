package nicolay.wk1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

public class DBUtil {
  private static final int ONE_DAY_IN_MILLISECONDS = 86400000;
  private static Connection connection = ConnectionManager.getInstance().getConnection();

  public static void saveQuotesToDatabase(List<Quote> quotes) throws SQLException {
    String query = "INSERT INTO quotes(symbol, price, volume, date) VALUES (?, ?, ?, ?)";

    PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

    for (Quote quote : quotes) {
      stmt.setString(1, quote.getSymbol());
      stmt.setDouble(2, quote.getPrice());
      stmt.setInt(3, quote.getVolume());
      stmt.setTimestamp(4, quote.getDate());
      stmt.addBatch();
    }

    stmt.executeBatch();
    stmt.close();
  }

  public static void clearTable(String tableName) {
    String query = "DELETE FROM " + tableName;

    try (
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
    ) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e);
    }
  }

  public static AggregateQuote getDailyAggregateData(String stockSymbol, Timestamp date)
      throws SQLException {
    String query = "SELECT MAX(price) AS maxPrice, MIN(price) AS minPrice, SUM(volume) AS totalVolume\n"
        + "FROM quotes\n"
        + "WHERE symbol = ? AND date >= ? AND date < ?";
    AggregateQuote result = null;
    ResultSet rs = null;

    try (
        PreparedStatement stmt = connection.prepareStatement(query,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    ) {
      result = new AggregateQuote();

      stmt.setString(1, stockSymbol);
      stmt.setTimestamp(2, date);
      // create a timestamp to represent the day after the specified date and pass as parameter
      stmt.setTimestamp(3, new Timestamp(date.getTime() + ONE_DAY_IN_MILLISECONDS));
      rs = stmt.executeQuery();
      rs.last();

      result.setSymbol(stockSymbol);
      result.setDate(date);
      result.setMaxPrice(rs.getDouble("maxPrice"));
      result.setMinPrice(rs.getDouble("minPrice"));
      result.setTotalVolume(rs.getInt("totalVolume"));
    } catch (SQLException e) {
      System.err.println(e);
    } finally {
      if (rs != null) {
        rs.close();
      }
    }

    return result;
  }
}
