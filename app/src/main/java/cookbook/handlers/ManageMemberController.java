package cookbook.handlers;
import cookbook.DBUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;

public class ManageMemberController {

    @FXML
    private Button backButton;

    @FXML
    private AnchorPane createAnchor;

    @FXML
    private Button createButton;

    @FXML
    private Button databaseCreate;

    @FXML
    private Button deleteButton;

    @FXML
    private RadioButton isAdminRadioCreate;

    @FXML
    private ListView<String> memberList;

    @FXML
    private Button discardButton;

    @FXML
    private Button modifyButton;

    @FXML
    private TextField passwordCreate;

    @FXML
    private TextField usernameCreate;

    @FXML
    private Label tableName;


    /**
     * A method that takes an ObservableList and sets it as the list for the memberList.
     * @param list The ObservableList to be set as the list for the memberList.
     */
    public void setMemberList(ObservableList<String> list) {
        memberList.setItems(list);
    }

 
    /**
     * Method to show the createAnchor when createButton is clicked.
     * This method sets the visibility of the createButton to false and that of the createAnchor to true.
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    void showCreateAnchor(ActionEvent event) {
        createButton.setVisible(false);
        createAnchor.setVisible(true);
    }

        /**
     * Method to hide the createAnchor and show the createButton.
     * This method sets the visibility of the createAnchor to false and that of the createButton to true.
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    void hideCreateAnchor(ActionEvent event) {
        passwordCreate.clear();
        usernameCreate.clear();
        createAnchor.setVisible(false);
        createButton.setVisible(true);
    }


    
    @FXML
    void createUser(ActionEvent event) {
        String username = usernameCreate.getText();
        String password = passwordCreate.getText();
        String isAdmin = isAdminRadioCreate.isSelected() ? "1" : "0";
        String rowInfo = username + ":" + password + ":" + isAdmin;
        
        String tableNameText = tableName.getText();
        DBUtils.addRow(tableNameText, rowInfo);
        DBUtils.changeToManageMemberScreen("xmls/manageMembers.fxml",event); // refresh

        
        hideCreateAnchor(event);
    }


    

}