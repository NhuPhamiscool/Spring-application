package au.edu.sydney.soft3202.task3;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DatabaseHelper {
  private static final String DB_NAME = "fruitbasket.db";
  private Connection connection;

  private void connect() throws SQLException {
    connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
  }
  private void ensureUsersTable() throws SQLException {
    String sql =
      "CREATE TABLE IF NOT EXISTS users (user TEXT PRIMARY KEY NOT NULL)";
    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }

  private void deleteUsersTable() throws SQLException {
    String sql = "DROP TABLE IF EXISTS users";
    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }

  public void addUser(String name) throws SQLException {
    String sql = "INSERT INTO users (user) VALUES (?)";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, name);
      preparedStatement.executeUpdate();
    }
  }


  public List<String> getUsers() throws SQLException {
    String sql = "SELECT user FROM users";

    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ResultSet resultSet = preparedStatement.executeQuery();
    List<String> users = new ArrayList<String>();

    while (resultSet.next()) {
      String user = resultSet.getString("user");
      users.add(user);
    }
    return users;
  }

  public String getUser(String name) throws SQLException {
    String sql = "SELECT user FROM users WHERE user = ?";

    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, name);
    ResultSet resultSet = preparedStatement.executeQuery();

    while (resultSet.next()) {
      String user = resultSet.getString("user");
      return user;
    }
    return null;
  }

  public DatabaseHelper() throws SQLException {
    connect();
    ensureUsersTable();
  }

}

