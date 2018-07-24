package nicolay.wk1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class used to handle a connection to a MySQL database. Having one database connection
 * across the application is more efficient than opening and closing connections for every
 * query.
 */
public class ConnectionManager
{
  private static ConnectionManager instance = null;

  private final String USERNAME = "dbuser";
  private final String PASSWORD = "dbpassword";
  private final String MYSQL_CONNECTION_STRING =
      "jdbc:mysql://localhost/week1exercise?serverTimezone=America/Chicago";

  private Connection connection = null;

  private ConnectionManager() {
  }

  public static ConnectionManager getInstance() {
    if (instance == null) {
      instance = new ConnectionManager();
    }
    return instance;
  }

  private boolean openConnection()
  {
    try {
        connection = DriverManager.getConnection(MYSQL_CONNECTION_STRING, USERNAME, PASSWORD);
        return true;
    }
    catch (SQLException e) {
      System.err.println(e);
      return false;
    }

  }

  public Connection getConnection()
  {
    if (connection == null) {
      if (openConnection()) {
        System.out.println("Connection opened");
        return connection;
      } else {
        return null;
      }
    }
    return connection;
  }

  public void close() {
    System.out.println("Closing connection");
    try {
      connection.close();
      connection = null;
    } catch (Exception e) {
    }
  }

}