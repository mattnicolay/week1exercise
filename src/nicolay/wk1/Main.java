package nicolay.wk1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

public class Main {

  public static void main(String args[]) throws SQLException {
    Connection conn = ConnectionManager.getInstance().getConnection();
    ResultSet rs = null;

    try (
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        Scanner in = new Scanner(System.in);
    ) {
      boolean running = true;

      while(running) {
        System.out.println("What would you like to do?\n"
            + "(p) - Populate database from web service\n"
            + "(a) - Get aggregated data\n"
            + "(q) - Quit");
        String response = in.next();

        switch (response.toLowerCase().trim()) {
          case "p":
            DBUtil.clearTable("quotes");
            List<Quote> quotes = WSUtil.getQuotes("https://bootcamp-training-files.cfapps.io/week1/week1-stocks.json");
            DBUtil.saveQuotesToDatabase(quotes);

            rs = stmt.executeQuery("SELECT * FROM quotes");
            rs.last();
            System.out.println(rs.getRow() + " rows affected.");
            break;

          case "a":
            handleAggregateData(in);
            break;

          case "q":
            running = false;
            break;

          default:
            System.out.println("Command \"" + response + "\" not recognized.");
        }
        System.out.println();
      }
    }catch (NumberFormatException e) {
      System.err.println("Improper date format provided;");
    }  catch (Exception e) {
      System.err.println(e);
    } finally {
      ConnectionManager.getInstance().close();
      if (rs != null) {
        rs.close();
      }
    }
  }

  private static void handleAggregateData(Scanner in) throws SQLException {
    System.out.println("Please provide the symbol for the stock you would like to see: ");
    String symbol = in.next();

    boolean running = true;
    Calendar calendar = new GregorianCalendar();
    int timeSetting = -1;
    String[] dateRaw;
    while (running) {

      System.out.println("Aggregate by which measure of time?\n"
          + "(m) - month\n"
          + "(d) - day");
      String measure = in.next();

      switch(measure) {
        case "m":
          System.out.println("Please provide the month for which you'd like to see aggregate"
              + " data (yyyy-mm): ");
          dateRaw = in.next().trim().split("-");

          calendar = new GregorianCalendar(Integer.parseInt(dateRaw[0]),
              Integer.parseInt(dateRaw[1])-1, 1);
          timeSetting = DBUtil.MONTHLY;
          running = false;
          break;

        case "d":
          System.out.println("Please provide the date for which you'd like to see aggregate"
              + " data (yyyy-mm-dd): ");
          dateRaw = in.next().trim().split("-");

          calendar = new GregorianCalendar(Integer.parseInt(dateRaw[0]),
              Integer.parseInt(dateRaw[1])-1, Integer.parseInt(dateRaw[2]));
          timeSetting = DBUtil.DAILY;
          running = false;
          break;

        default:
          System.out.println("Command \"" + measure + "\" not recognized.");
          break;
      }
    }

    AggregateQuote quote = DBUtil.getAggregateData(symbol, calendar, timeSetting);
    if (quote != null) {
      System.out.println(quote.toString());
    }
  }
}
