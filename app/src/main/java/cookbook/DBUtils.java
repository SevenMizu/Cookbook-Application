package cookbook;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Optional;

import cookbook.classes.User;
import cookbook.classes.UserSingleton;
import cookbook.handlers.ManageMemberController;
import cookbook.handlers.MyRecipesController;
import cookbook.handlers.UserScreenController;
import cookbook.classes.Admin;
import cookbook.classes.Comment;
import cookbook.classes.Message;
import cookbook.classes.Recipe;

public class DBUtils {

    private static Connection mainConn;
    private static UserSingleton loggedInUser; // Static attribute to hold the logged-in user instance

    public enum FavouriteAction {
        ADD,
        REMOVE
    }

    // Method to connect to the database
    public static void connectToDatabase(Stage stage) {
        // Add your database connection code here
    }

    private static boolean connectToDatabase() {
        try {
            // Load connection details from text file
            Properties properties = loadProperties("database.properties");
            // Get connection details
            String hostname = properties.getProperty("hostname");
            int port = Integer.parseInt(properties.getProperty("port"));
            String databaseName = properties.getProperty("databaseName");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            // Construct the connection URL using concatenation
            String connectionUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + databaseName + "?user=" + username
                    + "&password=" + password + "&useSSL=false";

            // Establish the connection
            Connection conn = DriverManager.getConnection(connectionUrl);
            mainConn = conn;
            return true; // Connection successful
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false; // Connection failed
        }

    }
    public static User getloggedInuser() {
        return loggedInUser.getUser();
    }
    private static Properties loadProperties(String filename) throws IOException {
        Properties properties = new Properties();
        File file = new File(filename);
        System.out.println("Looking for database.properties at: " + file.getAbsolutePath());

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            properties.load(reader);
        }
        return properties;
    }

    // Method to connect to the database and welcome user
    public static void connectAndWelcome(ActionEvent event) {
        boolean connected = connectToDatabase();
        if (connected) {
            changeScene("xmls/loginScreen.fxml", event);
        } else {
            System.out.println("Could not connect to database");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Could not establish connection to the database.");
            alert.show();
        }
    }

    // Method to change scene
    public static void changeScene(String fxml, ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {

            URL directory = DBUtils.class.getResource("/");
            System.out.println("Looking for resources in directory: " + directory);

            URL resourceUrl = DBUtils.class.getResource(fxml);
            System.out.println("Looking for resource at: " + resourceUrl);
            Parent root = FXMLLoader.load(DBUtils.class.getResource(fxml));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Recipe> loadRecipes() {
        return Querier.loadRecipes();
    }

    public static ObservableList<User> loadUsers() {
        return Querier.loadUsers();
    }

    public static ObservableList<Message> loadMessages() {
        return Querier.loadMessages(loggedInUser.getUser());
    }

    public static boolean sendMessage(int recipeId, int recipientId, String messageText) {
        int senderId = loggedInUser.getUser().getUserId();
        boolean messageSent = Querier.sendMessage(recipeId, senderId, recipientId, "Message from @" + loggedInUser.getUser().getUsername() + " : " + messageText);
        if (messageSent) {
            AlertUtils.createAlert(Alert.AlertType.INFORMATION, "Success", null, "Message sent successfully.").show();
        } else {
            AlertUtils.createAlert(Alert.AlertType.ERROR, "Error", null, "Failed to send message.").show();
        }
        return messageSent;
    }
    /**
     * Method to change scene to My Recipes screen, load user-specific recipes, and pass them to the controller.
     * @param fxml The path to the FXML file.
     * @param event The ActionEvent triggering the scene change.
     */
    public static void changeToMyRecipeScreen(String fxml, ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            URL directory = DBUtils.class.getResource("/");
            System.out.println("Looking for resources in directory: " + directory);

            URL resourceUrl = DBUtils.class.getResource(fxml);
            System.out.println("Looking for resource at: " + resourceUrl);
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxml));
            Parent root = loader.load();
            MyRecipesController myRecipesController = loader.getController();
            ObservableList<Recipe> allRecipes = loadRecipes();
            ObservableList<Recipe> userRecipes = FXCollections.observableArrayList();

            // Select only the recipes created by the logged-in user
            for (Recipe recipe : allRecipes) {
                System.out.println(recipe.getName() + recipe.getRecipeCreatorId() + loggedInUser.getUser().getUserId());
                if (recipe.getRecipeCreatorId() == loggedInUser.getUser().getUserId()) {
                    System.out.println(recipe.getName());
                    userRecipes.add(recipe);
                }
            }

            myRecipesController.loadUserRecipes(userRecipes);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean handleFavourite(int recipeId, FavouriteAction action, ActionEvent event) {
        User currentloggedInUser = getloggedInuser();
        if (currentloggedInUser == null) {
            AlertUtils.createAlert(Alert.AlertType.ERROR, "Error", null, "No user is currently logged in.").show();;
        }
    
        boolean success = false;
        switch (action) {
            case ADD:
                success = Querier.addFavourite(recipeId, currentloggedInUser.getUserId());
                break;
            case REMOVE:
                success = Querier.removeFavourite(recipeId, currentloggedInUser.getUserId());
                break;
            default:
            AlertUtils.createAlert(Alert.AlertType.ERROR, "Error", null, "Invalid favourite action specified.").show();;
                return false;
        }
    
        if (success) {
            changeToUserHomeScene("xmls/userHomeScreen.fxml", event, loggedInUser.getUser());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to update the favourite status.");
            alert.show();
        }

        return success;
    }

    public static void createUser(String username, String password, boolean isAdmin, ActionEvent event) {
        // Creating the user instance
        User newUser = isAdmin ? new Admin(0, username, password, "") : new User(0, username, password, "");
    
        boolean userCreated = Querier.createUserInDatabase(newUser);
        String alertMessage = userCreated ? "Successfully created the user: " + username : "Failed to create the user: " + username;
        AlertType alertType = userCreated ? AlertType.INFORMATION : AlertType.ERROR;
        Alert alert = AlertUtils.createAlert(alertType, "User Creation", null, alertMessage);
        alert.show();
        if (userCreated) {
            changeToManageMemberScreen("xmls/manageMembers.fxml", event);
        }
    }

    /**
     * Method to create a new recipe and insert it into the database, with alerts based on the success of the operation.
     * @param name The name of the recipe.
     * @param shortDescription A short description of the recipe.
     * @param detailedDescription A detailed description of the recipe.
     * @param servings The number of servings the recipe makes.
     * @param ingredientString Ingredients used in the recipe, comma-separated.
     * @param tagString Tags associated with the recipe, comma-separated.
     * @param event The ActionEvent from the UI interaction.
     */
    public static void createRecipe(String name, String shortDescription, String detailedDescription, int servings, String ingredientString, String tagString, ActionEvent event) {
        // Creating the recipe instance
        Recipe newRecipe = new Recipe(0, name, shortDescription, detailedDescription, servings, loggedInUser.getUser().getUserId(), ingredientString, tagString, "");

        boolean recipeCreated = Querier.createRecipeInDatabase(newRecipe);
        String alertMessage = recipeCreated ? "Successfully created the recipe: " + name : "Failed to create the recipe: " + name;
        AlertType alertType = recipeCreated ? AlertType.INFORMATION : AlertType.ERROR;
        Alert alert = AlertUtils.createAlert(alertType, "Recipe Creation", null, alertMessage);
        alert.show();
        if (recipeCreated) {
            changeToMyRecipeScreen("xmls/myRecipesScreen.fxml", event);
        }
    }

    /**
     * Method to modify a recipe in the database and handle the response with an alert.
     * @param recipe The recipe object to be updated.
     * @param event The ActionEvent triggering this method call.
     */
    public static void modifyRecipe(Recipe recipe, ActionEvent event) {
        boolean recipeUpdated = Querier.updateRecipeInDatabase(recipe);
        String alertMessage = recipeUpdated ? "Successfully updated the recipe: " + recipe.getName()
                                            : "Failed to update the recipe: " + recipe.getName();
        AlertType alertType = recipeUpdated ? AlertType.INFORMATION : AlertType.ERROR;
        Alert alert = AlertUtils.createAlert(alertType, "Recipe Update", null, alertMessage);
        alert.show();
        if (recipeUpdated) {
            changeToMyRecipeScreen("xmls/myRecipesScreen.fxml", event);
        }
    }

    /**
     * Method to modify a user in the database and handle the response with an alert.
     * @param user The user object to be updated.
     * @param event The ActionEvent triggering this method call.
     */
    public static void modifyUser(User user, ActionEvent event) {
        boolean userUpdated = Querier.modifyUser(user);
        String alertMessage = userUpdated ? "Successfully updated the user: " + user.getUsername()
                                            : "Failed to update the user: " + user.getUsername();
        AlertType alertType = userUpdated ? AlertType.INFORMATION : AlertType.ERROR;
        Alert alert = AlertUtils.createAlert(alertType, "User Update", null, alertMessage);
        alert.show();
        if (userUpdated) {
            changeToManageMemberScreen("xmls/manageMembers.fxml", event);
        }
    }
        /**
     * Method to delete a recipe from the database and handle the response with an alert.
     * @param recipe The recipe object to be deleted.
     * @param event The ActionEvent triggering this method call.
     */
    public static void deleteRecipe(Recipe recipe, ActionEvent event) {
        boolean recipeDeleted = Querier.deleteRecipe(recipe);
        String alertMessage = recipeDeleted ? "Successfully deleted the recipe: " + recipe.getName()
                                            : "Failed to delete the recipe: " + recipe.getName();
        AlertType alertType = recipeDeleted ? AlertType.INFORMATION : AlertType.ERROR;
        Alert alert = AlertUtils.createAlert(alertType, "Recipe Deletion", null, alertMessage);
        alert.show();

        if (recipeDeleted) {
            // Optionally, refresh the current screen or navigate away
            changeToMyRecipeScreen("xmls/myRecipesScreen.fxml", event);
        }
    }

    public static void addComment(int recipeId, String comment, ActionEvent event) {
        Comment newComment = new Comment(recipeId, comment, loggedInUser.getUser().getUserId());
        boolean commentAdded = Querier.addComment(recipeId, newComment);
        String alertMessage = commentAdded ? "Comment added successfully" : "Failed to add comment";
        AlertType alertType = commentAdded ? AlertType.INFORMATION : AlertType.ERROR;
        Alert alert = AlertUtils.createAlert(alertType, "Add Comment", null, alertMessage);
        alert.show();
        // Optionally, perform additional actions based on whether the comment was added successfully.
        if (commentAdded) {
            // Optionally, refresh the current screen or navigate away
            changeToUserHomeScene("xmls/userHomeScreen.fxml", event, loggedInUser.getUser());
        }  
      }
    // Method to change scene
    public static void changeToUserHomeScene(String fxml, ActionEvent event, User user) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {

            URL directory = DBUtils.class.getResource("/");
            System.out.println("Looking for resources in directory: " + directory);

            URL resourceUrl = DBUtils.class.getResource(fxml);
            System.out.println("Looking for resource at: " + resourceUrl);
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxml));
            Parent root = loader.load();
            UserScreenController userScreenController = loader.getController();

            if (user instanceof Admin) { // Check if the user is an Admin instance
                String shieldSymbol = " \u2694"; //  swords symbol

                userScreenController.setActiveUserLabel(user.getUsername().toUpperCase() + shieldSymbol );
                userScreenController.showManageMembersButton(); // Set label text with
                                                                                           // username in uppercase
            } else {
                userScreenController.setActiveUserLabel(user.getUsername()); // Set label text with username
            }
            userScreenController.loadRecipes();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method called changeToManageMember that takes (String fxml, ActionEvent event, ObservableList<String> users),
     * loads and uses the ManageMemberController.setMemberList(users), users gotten from the querier's getAllRowsAsObservableList("User") method.
     * @param fxml The path to the FXML file.
     * @param event The ActionEvent.
     * @param users The ObservableList of users to set as the member list.
     */
    public static void changeToManageMemberScreen(String fxml, ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            URL directory = DBUtils.class.getResource("/");
            System.out.println("Looking for resources in directory: " + directory);

            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxml));
            Parent root = loader.load();
            ManageMemberController manageMemberController = loader.getController();
            ObservableList<String> users = Querier.getAllRowsAsObservableList("User");
            manageMemberController.setMemberList(users);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Method to logout user
    public static void logout(ActionEvent event) {
        Alert confirmationAlert = AlertUtils.createConfirmationAlert("Confirmation", "Logout",
                "Are you sure you want to logout?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            loggedInUser = null; // Set the loggedInUser to null
            changeScene("xmls/loginScreen.fxml", event); // Change scene to login screen
        }
    }



    // Method to add a row to the specified table
    public static void modifyRow(String table, String setString, String rowSelector, String tobeModified) {
        boolean rowmodified = Querier.modifyRow(table, setString, rowSelector);
        String alertMessage = rowmodified ? "Successfully modified  " + tobeModified : "Something went wrong modifying " + tobeModified;
        AlertType alertType = rowmodified ? AlertType.INFORMATION : AlertType.ERROR;
        Alert alert = AlertUtils.createAlert(alertType, "User Modification", null, alertMessage);
        alert.show();
        }

    // Method to add a row to the specified table
    public static void deleteRow(User userToBeDeleted) {
        Alert confirmationAlert = AlertUtils.createConfirmationAlert("Confirmation", "Delete",
        "Are you sure you want to delete " + userToBeDeleted.getUsername() + "?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean rowmodified = Querier.deleteUser(userToBeDeleted);
            String alertMessage = rowmodified ? "Successfully deleted  " + userToBeDeleted.getUsername() : "Something went wrong modifying " + userToBeDeleted.getUsername();
            AlertType alertType = rowmodified ? AlertType.INFORMATION : AlertType.ERROR;
            Alert alert = AlertUtils.createAlert(alertType, "User Deleting", null, alertMessage);
            alert.show();
        }
        }

    // Method to authenticate user
    public static void authenticate(String inputUsername, String inputPassword, ActionEvent event) {
        Querier que = new Querier(mainConn);
        String userRow = que.checkForUser(inputUsername);
        if (userRow == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("User doesnt exist.");
            alert.show();
        } else {
            System.out.println("User exists");
            String[] userData = userRow.split(":");
            int userId = Integer.parseInt(userData[0]);
            String storedUsername = userData[1];
            String storedPassword = userData[2];
            int isAdmin = Integer.parseInt(userData[3]);

            User user = null; // Declare user variable outside if-else block
            if (isAdmin == 1) {
                user = new Admin(userId, storedUsername, storedPassword, "");
                System.out.println(user.getUsername() + "Admin user created");
            } else {
                user = new User(userId, storedUsername, storedPassword, "");
                System.out.println(user.getUsername() + "Regular user created");
            }
            if (user.checkPassword(inputPassword)) {

                loggedInUser = UserSingleton.getInstance(user);
                changeToUserHomeScene("xmls/userHomeScreen.fxml", event, loggedInUser.getUser());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Invalid credentials.");
                alert.show();
            }
        }
    }

}
