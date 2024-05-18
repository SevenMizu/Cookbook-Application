package cookbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cookbook.classes.Tag;
import cookbook.classes.Comment;
import cookbook.classes.User;
import cookbook.classes.Admin;
import cookbook.classes.Ingredient;
import cookbook.classes.Recipe;
import cookbook.classes.Message;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;


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

    public static boolean addComment(int recipeID, Comment commentToBeAdded) {
        String sqlQuery = "INSERT INTO Comment (RecipeID, Text, UserID) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {
            stmt.setInt(1, recipeID);
            stmt.setString(2, commentToBeAdded.getText());
            stmt.setInt(3, commentToBeAdded.getAuthorID());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if comment added successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
        try (Connection conn = DriverManager
                .getConnection("jdbc:mysql://localhost/StarWars?user=tobias&password=abcd1234")) {
            Querier querier = new Querier(conn);
            String result = querier.checkForUser("exampleUser");
            System.out.println("Result: " + result);
            querier.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that queries the database and returns all rows in the param table as
     * a String[], where columns are separated by ":".
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
     * A method that takes the String[] from getAllRows and returns it as an
     * ObservableList<String>,
     * where each item in the list is the username (text before the first ":") and
     * if they are an admin (text after last ":") with string "(ADMIN)" next to the
     * username.
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
     * A method that takes the table name and row information in the string format
     * "column1_info:column2_info:column3_info:...".
     * It adds the row with its column details into the table, omitting the ID
     * column.
     * Returns a boolean indicating successful row addition or not.
     * 
     * @param table   The name of the table where the row will be added.
     * @param rowInfo The information of the row to be added in the format
     *                "column1_info:column2_info:column3_info:...".
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
     * A method that modifies a row in the specified table based on the provided set
     * of values and row selector.
     * 
     * @param table       The name of the table where the row will be modified.
     * @param setString   The set of values to be updated in the format "column1 =
     *                    'value1', column2 = 'value2', ...".
     * @param rowSelector The condition to identify the row to be updated in the
     *                    format "column = 'value'".
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

    public static boolean deleteUser(User user) {

        // gpt: adjust this method to first delete the comments like it does, and then loop through
        String[] deleteQueries = {
            "DELETE FROM Comment WHERE UserID = ?", // fix comment deleting 
            "DELETE FROM Recipe WHERE UserID = ?",
            "DELETE FROM User WHERE user_id = ?"
        };
    
        try {
            for (String query : deleteQueries) {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, user.getUserId());
                stmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads all messages where the specified user is the recipient.
     * 
     * @param userWithMessages The user whose messages are to be loaded.
     * @return ObservableList<Message> containing all messages for the specified user.
     */
    public static ObservableList<Message> loadMessages(User userWithMessages) {
        ObservableList<Message> messages = FXCollections.observableArrayList();
        String sql = "SELECT MessageID, SenderID, RecipientID, RecipeID, MessageText, SentTime, `Read` FROM Messages WHERE RecipientID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userWithMessages.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int messageId = rs.getInt("MessageID");
                int senderId = rs.getInt("SenderID");
                int recipientId = rs.getInt("RecipientID");
                int recipeId = rs.getInt("RecipeID");
                String messageText = rs.getString("MessageText");
                LocalDateTime sentTime = rs.getTimestamp("SentTime").toLocalDateTime();
                boolean isRead = rs.getBoolean("Read");

                Message message = new Message(messageId, senderId, recipientId, recipeId, messageText, sentTime, isRead);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }
    /**
     * Loads all users from the database and returns them as an ObservableList of
     * User objects.
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
                String favouriteRecipes = getFavouriteRecipeIdsForUser(userId);

                User user;
                if (isAdmin == 1) {
                    user = new Admin(userId, username, password, favouriteRecipes); // Assuming Admin extends User or is otherwise
                                                                  // suitable
                } else {
                    user = new User(userId, username, password, favouriteRecipes);
                }
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static boolean addFavourite(int recipeId, int userId) {
        String sql = "INSERT INTO UserRecipeStar (UserID, RecipeID) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, recipeId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeFavourite(int recipeId, int userId) {
        String sql = "DELETE FROM UserRecipeStar WHERE UserID = ? AND RecipeID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, recipeId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static String getFavouriteRecipeIdsForUser(int userId) {
        String sql = "SELECT RecipeID FROM UserRecipeStar WHERE UserID = ?";
        List<Integer> recipeIds = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recipeIds.add(rs.getInt("RecipeID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        StringBuilder idsStringBuilder = new StringBuilder();
        for (int i = 0; i < recipeIds.size(); i++) {
            idsStringBuilder.append(recipeIds.get(i));
            if (i < recipeIds.size() - 1) {  // This check prevents adding a comma after the last ID
                idsStringBuilder.append(", ");
            }
        }
        System.out.println(idsStringBuilder.toString());
        return idsStringBuilder.toString();
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
                    (SELECT GROUP_CONCAT(CONCAT(c.CommentID, ':', c.Text, ':', c.UserID) ORDER BY c.CommentID SEPARATOR ', ')
                     FROM Comment c
                     WHERE c.RecipeID = r.RecipeID
                     ) AS comments
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
                Recipe recipe = new Recipe(id, name, shortDescription, detailedDescription, servings, userId,
                        ingredients, tags, comments);
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    public static boolean createUserInDatabase(User user) {
        String sqlUser = "INSERT INTO User (username, password, is_admin) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setInt(3, (user instanceof Admin) ? 1 : 0); // If user is an instance of Admin, set is_admin to 1,
                                                              // otherwise 0
            pstmt.executeUpdate();

            ResultSet rsKeys = pstmt.getGeneratedKeys();
            if (rsKeys.next()) {
                int userId = rsKeys.getInt(1);
                user.setUserId(userId); // Set the generated ID back to the user
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean modifyUser(User user) {
        String sqlQuery = "UPDATE User SET username = ?, password = ?, is_admin = ? WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, (user instanceof Admin) ? 1 : 0); // If user is an instance of Admin, set is_admin to 1,
                                                             // otherwise 0
            stmt.setInt(4, user.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if user modified successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createRecipeInDatabase(Recipe recipe) {
        String sqlRecipe = "INSERT INTO Recipe (Name, ShortDescription, DetailedDescription, Servings, UserID) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlRecipe, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getShortDescription());
            pstmt.setString(3, recipe.getDetailedDescription());
            pstmt.setInt(4, recipe.getServings());
            pstmt.setInt(5, recipe.getRecipeCreatorId());
            pstmt.executeUpdate();

            ResultSet rsKeys = pstmt.getGeneratedKeys();
            if (rsKeys.next()) {
                int recipeId = rsKeys.getInt(1);
                recipe.setRecipeId(recipeId); // Set the generated ID back to the recipe
            }

            // Handle tags
            for (Tag tag : recipe.getTags()) {
                insertOrUpdateTag(conn, tag, recipe.getRecipeId());
            }

            // Similarly, handle ingredients
            for (Ingredient ingredient : recipe.getIngredients()) {
                insertOrUpdateIngredient(conn, ingredient, recipe.getRecipeId());
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
                try (PreparedStatement pstmtTagInsert = conn.prepareStatement(sqlTagInsert,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
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

    private static void insertOrUpdateIngredient(Connection conn, Ingredient ingredient, int recipeId)
            throws SQLException {
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
                try (PreparedStatement pstmtIngredientInsert = conn.prepareStatement(sqlIngredientInsert,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
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
            conn = Querier.conn; // Assuming conn is a static member managed elsewhere
            conn.setAutoCommit(false); // Start transaction

            // Combine deletion of Recipe, RecipeIngredient, and RecipeTag into a single
            // method
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
                if (pstmt != null)
                    pstmt.close();
                // Do not close the conn here as it is managed elsewhere
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
