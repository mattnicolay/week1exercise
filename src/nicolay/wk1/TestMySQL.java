package nicolay.wk1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestMySQL {

  public static void main(String args[]) {
    Connection conn = ConnectionManager.getInstance().getConnection();

    try (
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery("SELECT * FROM quotes")
    ) {

      while (rs.next()) {
        System.out.println(rs.getInt("quoteId") + ": " + rs.getString("symbol"));
      }

      rs.last();
      System.out.println(rs.getRow());
    } catch (SQLException e) {
      System.err.println(e);
    }
  }
}
