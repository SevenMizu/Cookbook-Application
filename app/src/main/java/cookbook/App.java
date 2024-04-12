package cookbook;
import cookbook.handlers.StartController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("xmlPanes/start.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
        // Trigger loginClicked event to connect to the database
        StartController controller = loader.getController();
        //controller.dbConnect.fire(); // Fire the event associated with the loginButton
    }

    public static void main(String[] args) {
        launch(args);
    }
}
