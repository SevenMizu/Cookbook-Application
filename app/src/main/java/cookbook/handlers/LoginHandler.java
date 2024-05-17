package cookbook.handlers;

import cookbook.DBUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginHandler {


    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    void authenticate(ActionEvent event) {
        System.out.println("login button pressed");
        String username = usernameField.getText();
        String password = passwordField.getText();
        DBUtils.authenticate(username, password, event);    }

}