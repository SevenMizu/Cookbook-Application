package cookbook;

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
import java.net.URL; // Import URL from java.net package

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Optional;

import cookbook.classes.User;
import cookbook.classes.UserSingleton;
import cookbook.handlers.ManageMemberController;
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
                userScreenController.setActiveUserLabel(user.getUsername().toUpperCase());
                userScreenController.showManageMembersButton(); // Set label text with
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
    public static void addRow(String table, String rowInfo) {
        boolean rowAdded = Querier.addRow(table, rowInfo);
        String alertMessage = rowAdded ? "Successfully added a " + table : "Something went wrong adding a " + table;
        AlertType alertType = rowAdded ? AlertType.INFORMATION : AlertType.ERROR;
        Alert alert = AlertUtils.createAlert(alertType, "User Addition", null, alertMessage);
        alert.show();
    }

    // Method to add a row to the specified table
    public static void modifyRow(String table, String setString, String rowSelector, String tobeModified) {
        boolean rowmodified = Querier.modifyRow(table, setString, rowSelector);
        String alertMessage = rowmodified ? "Successfully modified  " + tobeModified : "Something went wrong modifying " + tobeModified;
        AlertType alertType = rowmodified ? AlertType.INFORMATION : AlertType.ERROR;
        Alert alert = AlertUtils.createAlert(alertType, "User Modification", null, alertMessage);
        alert.show();
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

    // an addrow method(params string table and string rowInfo with format "column1_info:column2_info:column3_info:..."), that calls the querier's addRow and passes the same params and if the method return true show alert "succcesfully added a {table param}", else "something wnet wrong adding a {table param}". use the AlertUtils for creating the alerts 
} 