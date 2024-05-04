package cookbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cookbook.classes.Tag;
import cookbook.classes.User;
import cookbook.classes.Admin;
import cookbook.classes.Ingredient;
import cookbook.classes.Recipe;




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

    /**
     * Loads all users from the database and returns them as an ObservableList of User objects.
     * Each User object contains userId, username, password, and isAdmin status.
     * 
     * @return ObservableList<User> containing all users.
     */
    public static ObservableList<User> loadUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT user_id, username, password, is_admin FROM User";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                int isAdmin = rs.getInt("is_admin"); // Assuming isAdmin is stored as an integer (0 or 1)

                User user;
                if (isAdmin == 1) {
                    user = new Admin(userId, username, password); // Assuming Admin extends User or is otherwise suitable
                } else {
                    user = new User(userId, username, password);
                }
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static ObservableList<Recipe> loadRecipes() {
        ObservableList<Recipe> recipes = FXCollections.observableArrayList();
        String sql_query = """
            SELECT
            r.RecipeID AS id,
            r.Name AS name,
            r.ShortDescription AS short_description,
            r.DetailedDescription AS detailed_description,
            r.Servings AS servings,
            r.UserID AS userid,
            GROUP_CONCAT(DISTINCT t.Name ORDER BY t.Name SEPARATOR ', ') AS tags,
            GROUP_CONCAT(DISTINCT i.Name ORDER BY i.Name SEPARATOR ', ') AS ingredients,
            GROUP_CONCAT(CONCAT(c.CommentID, ':', c.Text, ':', c.UserID) ORDER BY c.CommentID SEPARATOR ', ') AS comments
        FROM
            Recipe r
        LEFT JOIN
            RecipeTag rt ON r.RecipeID = rt.RecipeID
        LEFT JOIN
            Tag t ON rt.TagID = t.TagID
        LEFT JOIN
            RecipeIngredient ri ON r.RecipeID = ri.RecipeID
        LEFT JOIN
            Ingredient i ON ri.IngredientID = i.IngredientID
        LEFT JOIN
            Comment c ON r.RecipeID = c.RecipeID
        GROUP BY
            r.RecipeID;
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql_query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String shortDescription = rs.getString("short_description");
                String detailedDescription = rs.getString("detailed_description");
                int servings = rs.getInt("servings");
                int userId = rs.getInt("userid");
                String tags = (rs.getString("tags") != null) ? rs.getString("tags") : "";
                String ingredients = (rs.getString("ingredients") != null) ? rs.getString("ingredients") : "";
                String comments = (rs.getString("comments") != null) ? rs.getString("comments") : "";

                System.out.println(name);
                System.out.println(comments);

                
                
                // Create a Recipe instance and add to the recipes list
                Recipe recipe = new Recipe(id, name, shortDescription, detailedDescription, servings, userId, ingredients, tags, comments);
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    public static boolean createRecipeInDatabase(Recipe recipe) {
        Connection Recipeconn = null;
        PreparedStatement pstmt = null;
        ResultSet rsKeys = null;
        try {
            Recipeconn = conn; // Assuming mainConn is your static connection instance managed elsewhere
            Recipeconn.setAutoCommit(false); // Start transaction
    
            // Insert the recipe
            String sqlRecipe = "INSERT INTO Recipe (Name, ShortDescription, DetailedDescription, Servings, UserID) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sqlRecipe, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getShortDescription());
            pstmt.setString(3, recipe.getDetailedDescription());
            pstmt.setInt(4, recipe.getServings());
            pstmt.setInt(5, recipe.getRecipeCreatorId());
            pstmt.executeUpdate();
    
            rsKeys = pstmt.getGeneratedKeys();
            if (rsKeys.next()) {
                int recipeId = rsKeys.getInt(1);
                recipe.setRecipeId(recipeId); // Set the generated ID back to the recipe
            }
    
            // Handle tags
            for (Tag tag : recipe.getTags()) {
                insertOrUpdateTag(Recipeconn, tag, recipe.getRecipeId());
            }
    
            // Similarly, handle ingredients
            for (Ingredient ingredient : recipe.getIngredients()) {
                insertOrUpdateIngredient(Recipeconn, ingredient, recipe.getRecipeId());
            }
    
            Recipeconn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (Recipeconn != null) {
                try {
                    Recipeconn.rollback(); // Rollback transaction
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            // Clean up resources but do not close the connection
            if (rsKeys != null) {
                try {
                    rsKeys.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // Do not close the conn here as it is managed elsewhere
        }
    }

    private static void insertOrUpdateTag(Connection conn, Tag tag, int recipeId) throws SQLException {
        String sqlTagCheck = "SELECT TagID FROM Tag WHERE Name = ?";
        String sqlTagInsert = "INSERT INTO Tag (Name) VALUES (?)";
        String sqlRecipeTag = "INSERT INTO RecipeTag (RecipeID, TagID) VALUES (?, ?)";
        int tagId = 0;

        try (PreparedStatement pstmtTagCheck = conn.prepareStatement(sqlTagCheck)) {
            pstmtTagCheck.setString(1, tag.getName());
            ResultSet rsTag = pstmtTagCheck.executeQuery();
            if (rsTag.next()) {
                tagId = rsTag.getInt(1);
            } else {
                try (PreparedStatement pstmtTagInsert = conn.prepareStatement(sqlTagInsert, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    pstmtTagInsert.setString(1, tag.getName());
                    pstmtTagInsert.executeUpdate();
                    ResultSet rsTagId = pstmtTagInsert.getGeneratedKeys();
                    if (rsTagId.next()) {
                        tagId = rsTagId.getInt(1);
                    }
                }
            }
        }

        try (PreparedStatement pstmtRecipeTag = conn.prepareStatement(sqlRecipeTag)) {
            pstmtRecipeTag.setInt(1, recipeId);
            pstmtRecipeTag.setInt(2, tagId);
            pstmtRecipeTag.executeUpdate();
        }
    }

    private static void insertOrUpdateIngredient(Connection conn, Ingredient ingredient, int recipeId) throws SQLException {
        String sqlIngredientCheck = "SELECT IngredientID FROM Ingredient WHERE Name = ?";
        String sqlIngredientInsert = "INSERT INTO Ingredient (Name) VALUES (?)";
        String sqlRecipeIngredient = "INSERT INTO RecipeIngredient (RecipeID, IngredientID) VALUES (?, ?)";
        int ingredientId = 0;

        try (PreparedStatement pstmtIngredientCheck = conn.prepareStatement(sqlIngredientCheck)) {
            pstmtIngredientCheck.setString(1, ingredient.getName());
            ResultSet rsIngredient = pstmtIngredientCheck.executeQuery();
            if (rsIngredient.next()) {
                ingredientId = rsIngredient.getInt(1);
            } else {
                try (PreparedStatement pstmtIngredientInsert = conn.prepareStatement(sqlIngredientInsert, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    pstmtIngredientInsert.setString(1, ingredient.getName());
                    pstmtIngredientInsert.executeUpdate();
                    ResultSet rsIngredientId = pstmtIngredientInsert.getGeneratedKeys();
                    if (rsIngredientId.next()) {
                        ingredientId = rsIngredientId.getInt(1);
                    }
                }
            }
        }

        try (PreparedStatement pstmtRecipeIngredient = conn.prepareStatement(sqlRecipeIngredient)) {
            pstmtRecipeIngredient.setInt(1, recipeId);
            pstmtRecipeIngredient.setInt(2, ingredientId);
            pstmtRecipeIngredient.executeUpdate();
        }
    }

    public static boolean updateRecipeInDatabase(Recipe recipe) {
        Connection updateConn = null;
        PreparedStatement pstmt = null;
        try {
            updateConn = conn; // Assuming mainConn is your static connection instance managed elsewhere
            updateConn.setAutoCommit(false); // Start transaction
    
            // Update the recipe
            String sqlUpdateRecipe = "UPDATE Recipe SET Name = ?, ShortDescription = ?, DetailedDescription = ?, Servings = ?, UserID = ? WHERE RecipeID = ?";
            pstmt = updateConn.prepareStatement(sqlUpdateRecipe);
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getShortDescription());
            pstmt.setString(3, recipe.getDetailedDescription());
            pstmt.setInt(4, recipe.getServings());
            pstmt.setInt(5, recipe.getRecipeCreatorId());
            pstmt.setInt(6, recipe.getRecipeId());
            pstmt.executeUpdate();
    
            // Update tags
            updateRecipeTags(updateConn, recipe);
    
            // Update ingredients
            updateRecipeIngredients(updateConn, recipe);
    
            updateConn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (updateConn != null) {
                try {
                    updateConn.rollback(); // Rollback transaction
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            // Clean up resources but do not close the connection
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // Do not close the conn here as it is managed elsewhere
        }
    }
    
    private static void updateRecipeTags(Connection conn, Recipe recipe) throws SQLException {
        // First, clear existing tags for the recipe
        String sqlDeleteTags = "DELETE FROM RecipeTag WHERE RecipeID = ?";
        try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDeleteTags)) {
            pstmtDelete.setInt(1, recipe.getRecipeId());
            pstmtDelete.executeUpdate();
        }
    
        // Re-insert/update tags
        for (Tag tag : recipe.getTags()) {
            insertOrUpdateTag(conn, tag, recipe.getRecipeId());
        }
    }
    
    private static void updateRecipeIngredients(Connection conn, Recipe recipe) throws SQLException {
        // First, clear existing ingredients for the recipe
        String sqlDeleteIngredients = "DELETE FROM RecipeIngredient WHERE RecipeID = ?";
        try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDeleteIngredients)) {
            pstmtDelete.setInt(1, recipe.getRecipeId());
            pstmtDelete.executeUpdate();
        }
    
        // Re-insert/update ingredients
        for (Ingredient ingredient : recipe.getIngredients()) {
            insertOrUpdateIngredient(conn, ingredient, recipe.getRecipeId());
        }
    }

    /**
 * Deletes a recipe and its associated ingredients and tags from the database.
 * 
 * @param recipe The recipe to be deleted.
 * @return true if the deletion is successful, false otherwise.
 */
public static boolean deleteRecipe(Recipe recipe) {
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
        conn = Querier.conn;  // Assuming conn is a static member managed elsewhere
        conn.setAutoCommit(false); // Start transaction

        // Combine deletion of Recipe, RecipeIngredient, and RecipeTag into a single method
        String[] deleteSQL = {
            "DELETE FROM RecipeIngredient WHERE RecipeID = ?",
            "DELETE FROM RecipeTag WHERE RecipeID = ?",
            "DELETE FROM Recipe WHERE RecipeID = ?"
        };

        for (String sql : deleteSQL) {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, recipe.getRecipeId());
            pstmt.executeUpdate();
            pstmt.close(); // Close the statement to reuse the variable
        }

        conn.commit(); // Commit transaction if all deletions were successful
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        if (conn != null) {
            try {
                conn.rollback(); // Rollback transaction
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    } finally {
        try {
            if (pstmt != null) pstmt.close();
            // Do not close the conn here as it is managed elsewhere
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    
}