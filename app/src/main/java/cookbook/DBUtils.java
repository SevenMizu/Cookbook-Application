package cookbook;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL; // Import URL from java.net package

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Optional;

import cookbook.classes.User;
import cookbook.classes.UserSingleton;
import cookbook.handlers.UserScreenController;
import cookbook.classes.Admin;

public class DBUtils {

    private static Connection mainConn;
    private static UserSingleton loggedInUser; // Static attribute to hold the logged-in user instance

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
                userScreenController.setActiveUserLabel(user.getUsername().toUpperCase()); // Set label text with
                                                                                           // username in uppercase
            } else {
                userScreenController.setActiveUserLabel(user.getUsername()); // Set label text with username
            }
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
                user = new Admin(userId, storedUsername, storedPassword);
                System.out.println(user.getUsername() + "Admin user created");
            } else {
                user = new User(userId, storedUsername, storedPassword);
                System.out.println(user.getUsername() + "Regular user created");
            }
            if (user.checkPassword(inputPassword)) {

                loggedInUser = UserSingleton.getInstance(user);
                changeToUserHomeScene("xmls/userHomeScreen.fxml", event, loggedInUser.getUser());
                // Show success alert with a welcome message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setContentText("Welcome back, " + user.getUsername());
                successAlert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Invalid credentials.");
                alert.show();
            }
        }
    }
} 