package nicolay.wk1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBUtil {
  public static void saveQuotesToDatabase(List<Quote> quotes) throws SQLException {
    String query = "INSERT INTO quotes(symbol, price, volume, date) VALUES (?, ?, ?, ?)";

    Connection connection = ConnectionManager.getInstance().getConnection();
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
    Connection connection = ConnectionManager.getInstance().getConnection();
    try (
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
    ) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e);
    }
  }
}
