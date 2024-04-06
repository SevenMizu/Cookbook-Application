package cookbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Querier {
    private static Connection conn;

    public Querier(Connection newConn) {
        conn = newConn;
    }

    private static String getRow(String table, String column, String value, String delimiter) {
        String query = "SELECT * FROM " + table + " WHERE " + column + "=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                StringBuilder result = new StringBuilder();
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    result.append(rs.getString(i));
                    if (i < columnCount) {
                        result.append(delimiter);
                    }
                }
                rs.close();
                return result.toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String checkForUser(String username) {
        return getRow("User", "username", username, ":");
    }

    // Close connection
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example usage
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/StarWars?user=tobias&password=abcd1234")) {
            Querier querier = new Querier(conn);
            String result = querier.checkForUser("exampleUser");
            System.out.println("Result: " + result);
            querier.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}