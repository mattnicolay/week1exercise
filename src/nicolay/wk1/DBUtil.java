package nicolay.wk1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DBUtil {

  private static final long ONE_DAY_IN_MILLISECONDS = 86400000L;
  private static Connection connection = ConnectionManager.getInstance().getConnection();

  public static final int DAILY = 0;
  public static final int MONTHLY = 1;

  /**
   * Method to save a list of stock quotes to the database
   * @param quotes the list of quotes to be saved
   * @throws SQLException
   */
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

  /**
   * Clears the contents of the table provided.
   * @param tableName the table to be cleared
   */
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

  /**
   * Gets an aggregated view of stock data for a given symbol on a given day or for a given month,
   * based on timeSetting.
   * @param stockSymbol the symbol of the stock for which to get aggregated data
   * @param calendarDate the date on which to display aggregate data (for monthly data, this is the
   * first of a given month
   * @param timeSetting the time period over which to aggregate (either DAILY (0) or MONTHLY (1))
   * @return an AggregateQuote object representing the aggregate view of the data
   * @throws SQLException
   */
  public static AggregateQuote getAggregateData(String stockSymbol, Calendar calendarDate, int timeSetting)
      throws SQLException {
    long offset = getOffset(calendarDate, timeSetting);
    if (offset == 0) {
      return null;
    }

    String firstQuery =
        "SELECT MAX(price) AS maxPrice, MIN(price) AS minPrice, SUM(volume) AS totalVolume\n"
        + "FROM quotes\n"
        + "WHERE symbol = ? AND date >= ? AND date < ?";

    String secondQuery = "SELECT price AS closingPrice, MAX(date)\n"
        + "FROM quotes\n"
        + "WHERE symbol = ? AND date < ?";

    AggregateQuote result = null;
    ResultSet rs = null;
    PreparedStatement stmt = null;

    try {
      stmt = connection.prepareStatement(firstQuery,
          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      result = new AggregateQuote();

      Date date = calendarDate.getTime();

      stmt.setString(1, stockSymbol);
      stmt.setTimestamp(2, new Timestamp(date.getTime()));
      // create a timestamp to represent the day/month after the specified date/month using offset
      stmt.setTimestamp(3, new Timestamp(date.getTime() + offset));
      rs = stmt.executeQuery();
      rs.last();

      result.setSymbol(stockSymbol);
      result.setDate(new Timestamp(date.getTime()));
      result.setMaxPrice(rs.getDouble("maxPrice"));
      result.setMinPrice(rs.getDouble("minPrice"));
      result.setTotalVolume(rs.getInt("totalVolume"));

      stmt = connection.prepareStatement(secondQuery,
          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      stmt.setString(1, stockSymbol);
      stmt.setTimestamp(2, new Timestamp(date.getTime() + offset));
      rs = stmt.executeQuery();
      rs.last();

      result.setClosingPrice(rs.getDouble("closingPrice"));
    } catch (SQLException e) {
      System.err.println(e);
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (stmt != null) {
        stmt.close();
      }
    }

    return result;
  }

  /**
   * Returns the time offset in milliseconds related to the period of time over which to show an
   * aggregate view of data. For daily data, just returns one day in milliseconds. For monthly,
   * uses Calendar parameter to calculate the number of days in the month and returns milliseconds
   * equal to that many days.
   * @param date a Calendar object of the date/month over which to aggregate data
   * @param timeSetting the time period over which to aggregate (either DAILY (0) or MONTHLY (1))
   * @return the time offset in milliseconds
   */
  private static long getOffset(Calendar date, int timeSetting) {
    if (timeSetting == DAILY) {
      return ONE_DAY_IN_MILLISECONDS;
    } else if (timeSetting == MONTHLY) {
      return ONE_DAY_IN_MILLISECONDS * date.getActualMaximum(Calendar.DAY_OF_MONTH);
    } else {
      System.err.println("Improper time setting provided.");
      return 0;
    }
  }
}
