package cookbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;

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
    public static void closeConnection() {
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

        public static String getColumnsExceptFirst(String tableName) {
        StringBuilder columnsBuilder = new StringBuilder();

        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

            boolean firstColumn = true;
            while (resultSet.next()) {
                if (!firstColumn) {
                    columnsBuilder.append(resultSet.getString("COLUMN_NAME")).append(":");
                } else {
                    firstColumn = false;
                }
            }

            // Remove the last ":" if there are columns
            if (columnsBuilder.length() > 0) {
                columnsBuilder.deleteCharAt(columnsBuilder.length() - 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return columnsBuilder.toString();
    }

    

    /**
     * A method that takes the table name and row information in the string format "column1_info:column2_info:column3_info:...". 
     * It adds the row with its column details into the table, omitting the ID column.
     * Returns a boolean indicating successful row addition or not.
     * 
     * @param table The name of the table where the row will be added.
     * @param rowInfo The information of the row to be added in the format "column1_info:column2_info:column3_info:...".
     * @return true if the row addition is successful, false otherwise.
     */
    public static boolean addRow(String table, String rowInfo) {
        try {
            String[] columns = getColumnsExceptFirst(table).split(":");
            String[] values = rowInfo.split(":");

            
            StringBuilder queryBuilder = new StringBuilder("INSERT INTO ");
            queryBuilder.append(table).append(" (");

            // Building the query and setting values for each column
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    queryBuilder.append(", ");
                }
                queryBuilder.append(columns[i]);
            }
            queryBuilder.append(") VALUES (");

            // Adding placeholders for parameterized query
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    queryBuilder.append(", ");
                }
                queryBuilder.append("?");
            }
            queryBuilder.append(")");

            String query = queryBuilder.toString();
            System.out.println(query);
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Set values for each column
                for (int i = 0; i < values.length; i++) {
                    stmt.setString(i + 1, values[i]);
                }
                stmt.executeUpdate();
                return true; // Row added successfully
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the connection after adding the row
            System.out.println("handle closing connection");
        }
        return false; // Failed to add row
    }

        /**
     * A method that modifies a row in the specified table based on the provided set of values and row selector.
     * @param table The name of the table where the row will be modified.
     * @param setString The set of values to be updated in the format "column1 = 'value1', column2 = 'value2', ...".
     * @param rowSelector The condition to identify the row to be updated in the format "column = 'value'".
     * @return true if the row modification is successful, false otherwise.
     */
    public static boolean modifyRow(String table, String setString, String rowSelector) {
        try {
            StringBuilder queryBuilder = new StringBuilder("UPDATE ");
            queryBuilder.append(table).append(" SET ").append(setString).append(" WHERE ").append(rowSelector);

            String query = queryBuilder.toString();
            System.out.println(query);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Failed to modify row
    }

    /**
     * A method that deletes a row from the specified table based on the provided row selector.
     * 
     * @param table       The name of the table from which the row will be deleted.
     * @param rowSelector The condition to identify the row to be deleted in the format "column = 'value'".
     * @return true if the row deletion is successful, false otherwise.
     */
    public static boolean deleteRow(String table, String column , String value) {
        try {
            String query = "DELETE FROM " + table + " WHERE " + column + " = '" + value + "'" ;
            System.out.println(query); // For debugging

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Failed to delete row
    }
}