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

/**
 * Utility class used to connect to a MySQL database. Allows for inserting, deleting, and gathering
 * data from the database.
 */
public class DBUtil {

  private static final long ONE_DAY_IN_MILLISECONDS = 86400000L;
  private static Connection connection = ConnectionManager.getInstance().getConnection();

  /**
   * Method to save a list of stock quotes to the database
   * @param quotes the list of quotes to be saved
   * @throws SQLException
   */
  public static void saveQuotesToDatabase(List<Quote> quotes) throws SQLException {
    String query = "INSERT INTO quotes(symbol, price, volume, date) VALUES (?, ?, ?, ?)";

    PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

    // the addBatch/executeBatch methods allow for multiple queries to be executed at one time
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
  public static AggregateQuote getAggregateData(String stockSymbol, Calendar calendarDate, TimeSetting timeSetting)
      throws SQLException {
    long offset = getOffset(calendarDate, timeSetting);
    if (offset == 0) {
      // if offset is 0, that means an improper time setting was given.
      return null;
    }

    String query = "SELECT maxPrice, minPrice, totalVolume, closingPrice\n"
        + "FROM \n"
        + "(SELECT MAX(price) AS maxPrice, MIN(price) AS minPrice, SUM(VOLUME) AS totalVolume \n"
        + " FROM quotes\n"
        + "WHERE symbol = ? AND date >= ? AND date < ?) s1\n"
        + "JOIN\n"
        + "(SELECT price AS closingPrice, MAX(date)\n"
        + " FROM quotes\n"
        + " WHERE symbol = ? AND date < ?) s2";

    AggregateQuote result = null;
    ResultSet rs = null;

    try (
        PreparedStatement stmt = connection.prepareStatement(query,
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      ){
      Date date = calendarDate.getTime();
      Timestamp selectedDate = new Timestamp(date.getTime());
      // create a timestamp to represent one day/month after the specified date/month using offset
      Timestamp nextTimePeriod = new Timestamp(date.getTime() + offset);

      stmt.setString(1, stockSymbol);
      stmt.setTimestamp(2, selectedDate);
      stmt.setTimestamp(3, nextTimePeriod);
      stmt.setString(4, stockSymbol);
      stmt.setTimestamp(5, nextTimePeriod);
      rs = stmt.executeQuery();
      rs.last();

      result = new AggregateQuote(stockSymbol, rs.getDouble("maxPrice"),
          rs.getDouble("minPrice"), rs.getDouble("closingPrice"),
          rs.getInt("totalVolume"), selectedDate, timeSetting);
    } catch (SQLException e) {
      System.err.println(e);
    } finally {
      if (rs != null) {
        rs.close();
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
   * @param timeSetting the time period over which to aggregate
   * @return the time offset in milliseconds
   */
  private static long getOffset(Calendar date, TimeSetting timeSetting) {
    if (timeSetting == TimeSetting.DAY) {
      return ONE_DAY_IN_MILLISECONDS;
    } else if (timeSetting == TimeSetting.MONTH) {
      return ONE_DAY_IN_MILLISECONDS * date.getActualMaximum(Calendar.DAY_OF_MONTH);
    } else {
      System.err.println("Improper time setting provided.");
      return 0;
    }
  }
}
