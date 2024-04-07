package cookbook.handlers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserScreenController {

    @FXML
    private Label activeUserLabel;

    public void setActiveUserLabel(String text) {
        activeUserLabel.setText(text);
    }

}