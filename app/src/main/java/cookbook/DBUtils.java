package cookbook;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

public class DBUtils {

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
            changeScene("xmls/loginPane.fxml", event);
        } else {
                        System.out.println("Could not connect to database");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Could not establish connection to the database.");
            alert.show();
        }
        // gpt: add an else block, that prints "could not connect to database", add a alert of type error, context text(unable to reach database) and show it. i also want to close the app after this
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
}
