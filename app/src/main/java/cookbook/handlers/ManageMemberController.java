package cookbook.handlers;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

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
        createAnchor.setVisible(false);
        createButton.setVisible(true);
    }

    

}
