package nicolay.wk1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Main {

  public static void main(String args[]) {
    Connection conn = ConnectionManager.getInstance().getConnection();
    ResultSet rs = null;

    try (
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    ) {
      DBUtil.clearTable("quotes");
      List<Quote> quotes = WSUtil.getQuotes("https://bootcamp-training-files.cfapps.io/week1/week1-stocks.json");
      DBUtil.saveQuotesToDatabase(quotes);

      rs = stmt.executeQuery("SELECT * FROM quotes");

      while (rs.next()) {
        System.out.println(rs.getInt("quoteId") + ": " +
            rs.getString("symbol") + ", " +
            rs.getDouble("price") + ", " +
            rs.getInt("volume") + ", " +
            rs.getTimestamp("date"));
      }
      rs.close();


      Calendar calendar = new GregorianCalendar(2018, Calendar.JUNE, 22);
      Date date = calendar.getTime();
      long date_as_milliseconds = date.getTime();

      AggregateQuote quote = DBUtil.getDailyAggregateData("AMZN", new Timestamp(date_as_milliseconds));
      System.out.println(quote.toString());

      ConnectionManager.getInstance().close();
    } catch (SQLException e) {
      System.err.println(e);
    }
  }
}
