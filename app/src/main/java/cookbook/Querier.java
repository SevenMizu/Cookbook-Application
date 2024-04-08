package cookbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    /**
     * A method that queries the database and returns all rows in the param table as a String[], where columns are separated by ":".
     * Takes care of exceptions and close the connection after properly.
     */
    public static String[] getAllRows(String table) {
        String query = "SELECT * FROM " + table;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            List<String> rows = new ArrayList<>();
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    row.append(rs.getString(i));
                    if (i < columnCount) {
                        row.append(":");
                    }
                }
                rows.add(row.toString());
            }
            rs.close();
            return rows.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method that takes the String[] from getAllRows and returns it as an ObservableList<String>,
     * where each item in the list is the username (text before the first ":") and if they are an admin (text after last ":") with string "(ADMIN)" next to the username.
     */
    public static ObservableList<String> getAllRowsAsObservableList(String table) {
        String[] rows = getAllRows(table);
        ObservableList<String> observableList = FXCollections.observableArrayList();
        if (rows != null) {
            for (String row : rows) {
                String[] parts = row.split(":");
                String username = parts[parts.length - 1].equals("1") ? parts[1].toUpperCase() : parts[1];
                observableList.add(username);
            }
        }
        return observableList;
    }

}