package cookbook.handlers;

import cookbook.DBUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;
import cookbook.classes.Recipe; // Import Recipe class
import javafx.collections.FXCollections;




public class UserScreenController {

    @FXML
    private Label activeUserLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private Button manageMembersButton;

    @FXML
    private TextArea recipe;

    @FXML
    private TextField recipeSearchBar;

    @FXML
    private ListView<String> recipesListView; 

    private ObservableList<Recipe> recipes; // Added line

    @FXML
    void logout(ActionEvent event) {
        DBUtils.logout(event);
    }

    @FXML
    void changeToMemberManagerScreen(ActionEvent event) {
        DBUtils.changeToManageMemberScreen("xmls/manageMembers.fxml",event);
    }

    // A method to load recipes using DBUtils' loadRecipes method
    public void loadRecipes() { // Changed method signature
        recipes = DBUtils.loadRecipes(); // Changed line
        setMemberList();
    }
    public void setActiveUserLabel(String text) {
        activeUserLabel.setText(text);
    }

    public void showManageMembersButton() {
        manageMembersButton.setVisible(true);
    }

    /**
     * A method that takes an ObservableList and sets it as the list for the
     * memberList.
     * 
     * @param list The ObservableList to be set as the list for the memberList.
     */
    public void setMemberList() {

        // Concatenate ID and name for each recipe and add to the list
        ObservableList<String> displayList = FXCollections.observableArrayList();
        for (Recipe recipe : recipes) {
            String recipeInfo = recipe.getRecipeId() + ": " + recipe.getName();
            displayList.add(recipeInfo);
        }

        recipesListView.setItems(displayList);
    }

}