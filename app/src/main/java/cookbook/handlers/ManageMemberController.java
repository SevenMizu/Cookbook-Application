package cookbook.handlers;

import cookbook.AlertUtils;
import cookbook.DBUtils;
import cookbook.classes.Admin;
import cookbook.classes.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    private Button backButton, createButton, deleteButton, discardButton, modifyButton;
    @FXML
    private RadioButton isAdminRadioCreate;
    @FXML
    private ListView<String> memberList;
    @FXML
    private PasswordField passwordCreate;
    @FXML
    private TextField pass_text, usernameCreate;
    @FXML
    private CheckBox pass_toggle;
    @FXML
    private Label tableName;
    @FXML
    private AnchorPane rootAnchor, createAnchor;

    private ObservableList<User> users;

    /**
     * Initializes the controller.
     * Sets up the event filter for clearing fields on empty cell clicks and loads
     * the users from the database.
     */
    @FXML
    void initialize() {
        setupClearSelectionOnEmptyCell();
        users = DBUtils.loadUsers();
    }

    private void setupClearSelectionOnEmptyCell() {
        rootAnchor.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (isClickedOnEmptyListCell(event)) {
                showClearFieldsConfirmation();
            }
        });
    }

    private boolean isClickedOnEmptyListCell(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        while (clickedNode != null) {
            if (clickedNode instanceof ListCell && ((ListCell<?>) clickedNode).getItem() == null) {
                return true;
            }
            clickedNode = clickedNode.getParent();
        }
        return false;
    }

    private void showClearFieldsConfirmation() {
        Alert confirmationAlert = AlertUtils.createConfirmationAlert(
                "Confirm Clear",
                "You are about to clear the fields",
                "Do you want to proceed?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                clearFields();
            }
        });
    }

    private void clearFields() {
        memberList.getSelectionModel().clearSelection();
        usernameCreate.clear();
        passwordCreate.clear();
        isAdminRadioCreate.setSelected(false);
    }

    /**
     * Sets the items of the member list.
     * 
     * @param list The ObservableList to be set as the items for the memberList.
     */
    public void setMemberList(ObservableList<String> list) {
        memberList.setItems(list);
    }

    /**
     * Populates the username and password fields with the selected user's
     * information when a list item is clicked.
     * 
     * @param event The MouseEvent triggering the method.
     */
    @FXML
    void setModifyUsernameFromList(MouseEvent event) {
        String selectedItem = memberList.getSelectionModel().getSelectedItem();
        User selectedUser = users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(selectedItem))
                .findFirst().orElse(null);

        if (selectedUser != null) {
            populateFieldsWithSelectedUser(selectedUser);
        }
    }

    private void populateFieldsWithSelectedUser(User selectedUser) {
        usernameCreate.setText(selectedUser.getUsername());
        isAdminRadioCreate.setSelected(selectedUser instanceof Admin);
        passwordCreate.setText(selectedUser.getPassword());
    }

    /**
     * Toggles the visibility of the password between plain text and hidden.
     * 
     * @param event The ActionEvent triggering the method.
     */
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

    /**
     * Modifies the selected user's information based on the input fields.
     * 
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    void modifyUser(ActionEvent event) {
        if (isNoUserSelected()) {
            AlertUtils.createAlert(Alert.AlertType.ERROR, "Error", "", "No user selected for modification").show();
            return;
        }

        User selectedUser = getSelectedUser();
        if (selectedUser != null && validateTextFields(usernameCreate, passwordCreate)) {
            updateUser(selectedUser, event);
        } else {
            AlertUtils.createAlert(Alert.AlertType.ERROR, "Error", "", "Check the content of the forms!").show();
        }
    }

    private boolean isNoUserSelected() {
        return memberList.getSelectionModel().isEmpty() || memberList.getSelectionModel().getSelectedItem().isEmpty();
    }

    private User getSelectedUser() {
        String selectedItem = memberList.getSelectionModel().getSelectedItem();
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(selectedItem))
                .findFirst().orElse(null);
    }

    private void updateUser(User selectedUser, ActionEvent event) {
        String newUsername = usernameCreate.getText().trim();
        String newPassword = passwordCreate.getText().trim();
        boolean newIsAdmin = isAdminRadioCreate.isSelected();
        int selectedID = selectedUser.getUserId();

        selectedUser.setUsername(newUsername);
        selectedUser.setPassword(newPassword);

        updateAdminStatus(selectedUser, newIsAdmin, selectedID);

        DBUtils.modifyUser(selectedUser, event);
    }

    private void updateAdminStatus(User selectedUser, boolean newIsAdmin, int selectedID) {
        String favouriteRecipeIds = selectedUser.getFavouriteRecipeIdsAsString();

        if (newIsAdmin && !(selectedUser instanceof Admin)) {
            users.remove(selectedUser);
            selectedUser = new Admin(selectedID, selectedUser.getUsername(), selectedUser.getPassword(),
                    favouriteRecipeIds);
            users.add(selectedUser);
        } else if (!newIsAdmin && selectedUser instanceof Admin) {
            users.remove(selectedUser);
            selectedUser = new User(selectedID, selectedUser.getUsername(), selectedUser.getPassword(),
                    favouriteRecipeIds);
            users.add(selectedUser);
        }
    }

    /**
     * Validates that the username and password fields are not empty or only
     * whitespace.
     * 
     * @return true if the fields are valid, false otherwise.
     */
    private boolean validateFields() {
        return validateTextFields(usernameCreate, passwordCreate);
    }

    /**
     * Validates that the given text fields are not empty or only whitespace.
     * 
     * @param textFields The array of text fields to validate.
     * @return true if all text fields are valid, false otherwise.
     */
    private boolean validateTextFields(TextField... textFields) {
        for (TextField textField : textFields) {
            String text = textField.getText().trim();
            if (text.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new user based on the input fields if validation passes.
     * Displays an error alert if a user is selected or if validation fails.
     * 
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    void createUser(ActionEvent event) {
        if (memberList.getSelectionModel().getSelectedItem() != null) {
            AlertUtils
                    .createAlert(Alert.AlertType.ERROR, "Error", "", "Clear the selection before creating a new user.")
                    .show();
        } else if (validateFields()) {
            if (isAdminRadioCreate.isSelected() && users.stream().filter(user -> user instanceof Admin).count() > 0) {
                AlertUtils.createAlert(Alert.AlertType.ERROR, "Error", "", "An admin user already exists.").show();
            } else {
                DBUtils.createUser(usernameCreate.getText(), passwordCreate.getText(), isAdminRadioCreate.isSelected(),
                        event);
            }
        } else {
            AlertUtils.createAlert(Alert.AlertType.ERROR, "Error", "", "Check the content of the forms!").show();
        }
    }

    /**
     * Deletes the selected user from the list and the database.
     * Displays an error alert if no user is selected for deletion.
     * 
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    void deleteUser(ActionEvent event) {
        String selectedItem = memberList.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.isEmpty()) {
            AlertUtils.createAlert(Alert.AlertType.ERROR, "Error", "", "No user selected for deletion").show();
        } else {
            deleteUser(selectedItem.toLowerCase(), event);
        }
    }

    private void deleteUser(String username, ActionEvent event) {
        User selectedUser = users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst().orElse(null);

        if (selectedUser != null) {
            DBUtils.deleteRow(selectedUser);
            DBUtils.changeToManageMemberScreen("xmls/manageMembers.fxml", event);
        }
    }

    /**
     * Navigates back to the user home screen.
     * 
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    void backToUserScreen(ActionEvent event) {
        DBUtils.changeToUserHomeScene("xmls/userHomeScreen.fxml", event, DBUtils.getloggedInuser());
    }
}
