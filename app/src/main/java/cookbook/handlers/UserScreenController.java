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
    private Button manageMembersButton;

    @FXML
    void logout(ActionEvent event) {
        DBUtils.logout(event);
    }

    @FXML
    void changeToMemberManagerScreen(ActionEvent event) {
        DBUtils.changeToManageMemberScreen("xmls/manageMembers.fxml",event);
    }

    public void setActiveUserLabel(String text) {
        activeUserLabel.setText(text);
    }

    public void showManageMembersButton() {
        manageMembersButton.setVisible(true);
    }

}