package cookbook.handlers;

import cookbook.AlertUtils;
import cookbook.DBUtils;
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
    private CheckBox modifyAdminCheck;

    @FXML
    private RadioButton modifyAdminRadio;

    @FXML
    private AnchorPane modifyAnchor;

    @FXML
    private Button modifyButton;

    @FXML
    private Button modifyDatabase;

    @FXML
    private Button modifyDiscard;

    @FXML
    private CheckBox modifyPasswordCheck;

    @FXML
    private TextField modifyPasswordField;

    @FXML
    private CheckBox modifyUsernameCheck;

    @FXML
    private TextField modifyUsernameField;

    @FXML
    private TextField passwordCreate;

    @FXML
    private TextField usernameCreate;

    @FXML
    private Label tableName;

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
     * A method that enables or disables associated fields based on the state of
     * checkboxes.
     * For each checkbox, if it is checked, the associated field will be enabled;
     * otherwise, it will be disabled.
     */
    @FXML
    void handleCheckbox(ActionEvent event) {
        // Modify Username Checkbox
        if (modifyUsernameCheck.isSelected()) {
            modifyUsernameField.setDisable(false); // Enable the associated text field
        } else {
            modifyUsernameField.setDisable(true); // Disable the associated text field
        }

        // Modify Password Checkbox
        if (modifyPasswordCheck.isSelected()) {
            modifyPasswordField.setDisable(false); // Enable the associated text field
        } else {
            modifyPasswordField.setDisable(true); // Disable the associated text field
        }

        // Modify Admin Checkbox
        if (modifyAdminCheck.isSelected()) {
            modifyAdminRadio.setDisable(false); // Enable the associated radio button
        } else {
            modifyAdminRadio.setDisable(true); // Disable the associated radio button
        }
    }

    /**
     * Method to show the createAnchor when createButton is clicked.
     * This method sets the visibility of the createButton to false and that of the
     * createAnchor to true.
     * 
     * @param event The ActionEvent triggering the method.
     */

    // gpt: make a showAnchor method with params the anchor to show(e.g AnchorPane
    // modifyAnchor and the button to hide e.g Button modifyButton) and sets the
    // visibily of the anchor to true and that of the button to false
    @FXML
    void showCreateAnchor(ActionEvent event) {
        showAnchor(createAnchor, createButton);
    }

    /**
     * Method to show the specified anchor pane and hide the specified button.
     * 
     * @param anchorPane The anchor pane to be shown.
     * @param button     The button to be hidden.
     */
    private void showAnchor(AnchorPane anchorPane, Button button) {
        anchorPane.setVisible(true);
        button.setVisible(false);
    }

    @FXML
    void showModifyAnchor(ActionEvent event) {
        showAnchor(modifyAnchor, modifyButton);
    }

    /**
     * Method to hide the createAnchor and show the createButton.
     * This method sets the visibility of the createAnchor to false and that of the
     * createButton to true.
     * 
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    void hideCreateAnchor(ActionEvent event) {
        clearAndHideAnchorPane(createAnchor, createButton); // Clear fields and hide the modifyAnchor

    }

    /**
     * A method that sets the selected item in the memberList ListView as the text
     * in the TextField modifyUsernameField.
     */
    @FXML
    void setModifyUsernameFromList(MouseEvent event) {
        String selectedItem = memberList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            modifyUsernameField.setText(selectedItem.toLowerCase());
        }
    }

    /**
     * A method to hide the modifyAnchor, clear its fields, and show the
     * modifyButton.
     * 
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    void hideModifyAnchor(ActionEvent event) {
        clearAndHideAnchorPane(modifyAnchor, modifyButton); // Clear fields and hide the modifyAnchor
    }

    @FXML
    void modifyUserRow(ActionEvent event) {
        String tableNameText = tableName.getText();
        String selectedItem = memberList.getSelectionModel().getSelectedItem();
        String setString = generateUpdateString();

        if (setString.isEmpty()) {
            Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "", "No changes made");
            alert.show();
        } else {
            String rowSelector = "username = '" + selectedItem.toLowerCase() + "'";
            DBUtils.modifyRow(tableNameText, setString, rowSelector, selectedItem.toLowerCase());
            DBUtils.changeToManageMemberScreen("xmls/manageMembers.fxml", event); // refresh
        }
    }

    // gpt: adjust the validateFields to validateTextFields and it should take a
    // TextField[] of text fields to validate and loop through and perform the
    // vlaidation, returing the success of failure boolean
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
        if (validateFields()) {
            String username = usernameCreate.getText();
            String password = passwordCreate.getText();
            String isAdmin = isAdminRadioCreate.isSelected() ? "1" : "0";
            String rowInfo = username + ":" + password + ":" + isAdmin;

            String tableNameText = tableName.getText();
            DBUtils.addRow(tableNameText, rowInfo);
            DBUtils.changeToManageMemberScreen("xmls/manageMembers.fxml", event); // refresh
        } else {
            Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "", "Check the content of the forms!"); // Display
                                                                                                                   // error
                                                                                                                   // alert
            alert.show();

        }

        hideCreateAnchor(event);
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
        String tableNameText = tableName.getText();
        String value = memberList.getSelectionModel().getSelectedItem().toLowerCase(); // lowercase for admin names
        if (value.isEmpty()) {
            Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "", "No user selected for deletion");
            alert.show();
        } else {
            DBUtils.deleteRow(tableNameText, "username", value);
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

    /**
     * Method to dynamically generate SQL update string based on enabled fields.
     * 
     * @return The SQL update string.
     */
    private String generateUpdateString() {
        StringBuilder updateString = new StringBuilder();

        // Modify Username
        if (modifyUsernameCheck.isSelected() && !modifyUsernameField.getText().isEmpty()) {
            updateString.append("username = '").append(modifyUsernameField.getText()).append("', ");
        }

        // Modify Password
        if (modifyPasswordCheck.isSelected() && !modifyPasswordField.getText().isEmpty()) {
            updateString.append("password = '").append(modifyPasswordField.getText()).append("', ");
        }

        // Modify Admin
        if (modifyAdminCheck.isSelected()) {
            updateString.append("is_admin = ").append(modifyAdminRadio.isSelected() ? "1" : "0").append(", ");
        }

        // Remove trailing comma and space if any
        if (updateString.length() > 0) {
            updateString.setLength(updateString.length() - 2);
        }

        return updateString.toString();
    }



}