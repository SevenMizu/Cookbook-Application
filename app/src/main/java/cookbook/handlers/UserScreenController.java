package cookbook.handlers;

import cookbook.DBUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class UserScreenController {

    @FXML
    private Label activeUserLabel;


    @FXML
    private Button logoutButton;

    @FXML
    void logout(ActionEvent event) {
        DBUtils.logout(event);
    }

    public void setActiveUserLabel(String text) {
        activeUserLabel.setText(text);
    }

}