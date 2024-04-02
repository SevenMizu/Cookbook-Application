package cookbook;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.net.URL;

import javafx.scene.control.Label;

public class PaneManager {
    
    public StackPane createDatabasePane(String labelText) {
        Label label = new Label(labelText);
        Label labell = new Label("yessss");

        // Create a StackPane and add all labels at once
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(label, labell);

        return stackPane;    
    }

    public StackPane createDatabaseXMLPane(String labelText) throws IOException {
        System.out.println("Default location for FXML files: " + getClass().getResource(""));

        // Get the base directory URL
        // Load StackPane layout from FXML file
        StackPane stackPane = FXMLLoader.load(getClass().getResource("xmlPanes/loginPane.fxml"));

        // Set the text property of the Label in the FXML file
        // (Assuming there is a Label with fx:id="label" in the FXML file)
        Label label = (Label) stackPane.lookup("#databaseStatus");
        label.setText(labelText);

        // Print the stackPane object
        System.out.println("StackPane content: " + stackPane.getChildren());

        return stackPane;
    }
    }
    
    

