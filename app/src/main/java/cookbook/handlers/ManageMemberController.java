package cookbook.handlers;

import cookbook.AlertUtils;
import cookbook.DBUtils;
import cookbook.classes.Admin;
import cookbook.classes.Recipe;
import cookbook.classes.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class ManageMemberController {

    @FXML
    private Button backButton;

    @FXML
    private AnchorPane createAnchor;

    @FXML
    private Button createButton;


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
    private PasswordField passwordCreate;

    @FXML
    private TextField pass_text;

    @FXML
    private CheckBox pass_toggle;
    @FXML
    private TextField usernameCreate;

    @FXML
    private Label tableName;

    @FXML
    private AnchorPane rootAnchor; // Assuming this is the root AnchorPane

    private ObservableList<User> users; // Added line



        @FXML
    void initialize() {
        rootAnchor.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Node clickedNode = event.getPickResult().getIntersectedNode();
            boolean clickedEmptyListCell = false;
    
            // Check if the clicked node is an empty cell of the memberList
            while (clickedNode != null) {
                if (clickedNode instanceof ListCell && ((ListCell<?>) clickedNode).getItem() == null) {
                    clickedEmptyListCell = true;
                    break;
                }
                clickedNode = clickedNode.getParent();
            }
    
            // Clear selection if the click is on an empty list cell
            if (clickedEmptyListCell) {
                memberList.getSelectionModel().clearSelection();
                usernameCreate.clear(); // Clear the usernameCreate field
                passwordCreate.clear();
                isAdminRadioCreate.setSelected(false); 

            }
        });

        users = DBUtils.loadUsers();

    }

    /**
     * A method that takes an ObservableList and sets it as the list for the
     * memberList.
     * 
     * @param list The ObservableList to be set as the list for the memberList.
     */
    public void setMemberList(ObservableList<String> list) {
        memberList.setItems(list);
    }



    /**
     * A method that sets the selected item in the memberList ListView as the text
     * in the TextField modifyUsernameField.
     */
    @FXML
    void setModifyUsernameFromList(MouseEvent event) {
        String selectedItem = memberList.getSelectionModel().getSelectedItem();
        User selectedUser = users.stream().filter(user -> user.getUsername().equalsIgnoreCase(selectedItem)).findFirst().orElse(null);
        if (selectedUser != null) {
            usernameCreate.setText(selectedItem.toLowerCase());
            isAdminRadioCreate.setSelected(selectedUser instanceof Admin);
            // gpt: get the selectedUser password and set in the passwordCreate field 
            passwordCreate.setText(selectedUser.getPassword());
        }
    }

    @FXML
    void togglePasswordVisibility(ActionEvent event) {
        if (pass_toggle.isSelected()) {
            pass_text.setText(passwordCreate.getText());
            pass_text.setVisible(true);
            passwordCreate.setVisible(false);
        } else {
            passwordCreate.setText(pass_text.getText());
            passwordCreate.setVisible(true);
            pass_text.setVisible(false);
        }
    }



    @FXML
    void modifyUser(ActionEvent event) {
        if (memberList.getSelectionModel().isEmpty() || memberList.getSelectionModel().getSelectedItem().isEmpty()) {
            AlertUtils.createAlert(AlertType.ERROR, "Error", "", "No user selected for modification").show();
            return;
        }
    
        String selectedItem = memberList.getSelectionModel().getSelectedItem();
        User selectedUser = users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(selectedItem))
                .findFirst().orElse(null);
    
        if (selectedUser == null) return;
    
        String newUsername = usernameCreate.getText().trim();
        String newPassword = passwordCreate.getText().trim();
        boolean newIsAdmin = isAdminRadioCreate.isSelected();
        int selectedID = selectedUser.getUserId()
    ;
        if (validateTextFields(usernameCreate, passwordCreate) && isAdminRadioCreate != null) {
            selectedUser.setUsername(newUsername);
            System.out.println(newPassword);
            selectedUser.setPassword(newPassword);
    
            if (newIsAdmin && !(selectedUser instanceof Admin)) {
                users.remove(selectedUser);
                selectedUser = new Admin(selectedID, newUsername, newPassword);
                users.add(selectedUser);
            } else if (!newIsAdmin && selectedUser instanceof Admin) {
                users.remove(selectedUser);
                selectedUser = new User(selectedID, newUsername, newPassword);
                users.add(selectedUser);
            }
    
            DBUtils.modifyUser(selectedUser, event);
        } else {
            AlertUtils.createAlert(AlertType.ERROR, "Error", "", "Check the content of the forms!").show();
        }
    }


    /**
     * A method to validate the content of the username and password fields.
     * Checks that none of the fields' content is only spaces, tabs, or completely
     * empty.
     * 
     * @return true if the fields are valid, false otherwise.
     */
    private boolean validateFields() {
        String username = usernameCreate.getText().trim(); // Remove leading and trailing spaces
        String password = passwordCreate.getText().trim(); // Remove leading and trailing spaces

        // Check if username or password is empty or contains only whitespace
        if (username.isEmpty() || password.isEmpty() ||
                username.isBlank() || password.isBlank()) {
            return false;
        }
        return true;
    }

    /**
     * A method to validate an array of text fields.
     * Checks that none of the fields' content is only spaces, tabs, or completely
     * empty.
     * 
     * @param textFields The array of text fields to validate.
     * @return true if all text fields are valid, false otherwise.
     */
    private boolean validateTextFields(TextField... textFields) {
        for (TextField textField : textFields) {
            String text = textField.getText().trim();
            if (text.isEmpty() || text.isBlank()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to handle the creation of a user.
     * Validates the fields and creates a new user if the fields are valid.
     * Displays an error alert if the fields are invalid.
     * 
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    void createUser(ActionEvent event) {
        // Check if there is a selection in the member list view
        if (memberList.getSelectionModel().getSelectedItem() != null) {
            // If an item is already selected, display an alert
            Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "", "Clear the selection before creating a new user.");
            alert.show();
        } else if (validateFields()) {
            // Proceed with the user creation logic if validation passes
            String username = usernameCreate.getText();
            String password = passwordCreate.getText();
            boolean isAdmin = isAdminRadioCreate.isSelected();
            DBUtils.createUser(username, password, isAdmin, event);
        } else {
            // Display an alert if form validation fails
            Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "", "Check the content of the forms!");
            alert.show();
        }
    

    }

    /**
     * Method to handle the creation of a user.
     * Validates the fields and creates a new user if the fields are valid.
     * Displays an error alert if the fields are invalid.
     * 
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    void deleteUser(ActionEvent event) {
        String value = memberList.getSelectionModel().getSelectedItem().toLowerCase(); // lowercase for admin names
        User selectedUser = users.stream()
        .filter(user -> user.getUsername().equalsIgnoreCase(value))
        .findFirst().orElse(null);
        if (value.isEmpty()) {
            Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "", "No user selected for deletion");
            alert.show();
        } else {
            DBUtils.deleteRow(selectedUser);
            DBUtils.changeToManageMemberScreen("xmls/manageMembers.fxml", event); // refresh }
        }
    }

    @FXML
    void backToUserScreen(ActionEvent event) {
        DBUtils.changeToUserHomeScene("xmls/userHomeScreen.fxml", event, DBUtils.getloggedInuser());


    }

    /**
     * A method to clear all fields within the specified anchor pane, set its
     * visibility to false, and then set the visibility of a button to true.
     * 
     * @param anchorPane The anchor pane whose children's fields need to be cleared.
     * @param button     The button whose visibility needs to be set to true after
     *                   hiding the anchor pane.
     */
    private void clearAndHideAnchorPane(AnchorPane anchorPane, Button button) {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof TextField) {
                ((TextField) node).clear(); // Clear text fields
            } else if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false); // Uncheck checkboxes
            } else if (node instanceof RadioButton) {
                ((RadioButton) node).setSelected(false); // Uncheck RadioButtons
                // Add more conditions for other types of fields if needed
            }
        }

        anchorPane.setVisible(false); // Hide the anchor pane after clearing its fields
        button.setVisible(true); // Show the button
    }




}