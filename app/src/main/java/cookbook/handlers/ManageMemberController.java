package cookbook.handlers;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class ManageMemberController {

    @FXML
    private Button backButton;

    @FXML
    private Button createButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ListView<String> memberList;

    @FXML
    private Button modifyButton;


    /**
     * A method that takes an ObservableList and sets it as the list for the memberList.
     * @param list The ObservableList to be set as the list for the memberList.
     */
    public void setMemberList(ObservableList<String> list) {
        memberList.setItems(list);
    }}
