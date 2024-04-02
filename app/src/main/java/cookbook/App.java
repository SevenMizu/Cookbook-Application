package cookbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import cookbook.*;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

             // Load connection details from text file
        Properties properties = loadProperties("database.properties");

        // Get connection details
        String hostname = properties.getProperty("hostname");
        int port = Integer.parseInt(properties.getProperty("port"));
        String databaseName = properties.getProperty("databaseName");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");



        VBox root = new VBox();
        root.setPadding(new Insets(5));
        Label title = new Label("CBook");
        StackPane databasePane;

        try {
            // Construct the connection URL using concatenation
            String connectionUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + databaseName + "?user=" + username + "&password=" + password + "&useSSL=false";

            // Establish the connection
            Connection conn = DriverManager.getConnection(connectionUrl);
            // Call the createDatabasePane method from the PaneManager class
            PaneManager paneManager = new PaneManager();
            databasePane = paneManager.createDatabaseXMLPane("Driver found and connected");

        } catch (SQLException e) {
            // Call the createDatabasePane method from the PaneManager class
            PaneManager paneManager = new PaneManager();
            databasePane = paneManager.createDatabaseXMLPane("An error has occurred: \" + e.getMessage()");

            
        }

        root.getChildren().addAll(title, databasePane);

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setTitle("CBook");
        primaryStage.show();
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

    public static void main(String[] args) {
        launch(args);
    }
}
