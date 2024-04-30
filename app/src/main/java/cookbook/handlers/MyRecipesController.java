package cookbook.handlers;

import cookbook.DBUtils;
import cookbook.classes.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class MyRecipesController {

    @FXML
    private Button backButton;

    @FXML
    private AnchorPane createAnchor;

    @FXML
    private AnchorPane modifyAnchor;

    @FXML
    private TextField createDetailedField;

    @FXML
    private Button createDiscardButton;

    @FXML
    private TextField createIngredientsField;

    @FXML
    private Button createRecipeMain;

    @FXML
    private TextField createShortDescription;

    @FXML
    private TextField createTagsField;

    @FXML
    private Button createToggle;

    @FXML
    private Button deleteRecipeButton;

    @FXML
    private TextField modifyDetailedDescription;

    @FXML
    private Button modifyDiscardButton;

    @FXML
    private TextField modifyIngredientsField;

    @FXML
    private Button modifyRecipeMain;

    @FXML
    private TextField modifyShortDescription;

    @FXML
    private TextField modifyTagsField;

    @FXML
    private Button modifyToggle;

    @FXML
    private TextField numberOfServingsField;

    @FXML
    private ListView<String> recipeListView;

    @FXML
    private TextField recipeNameField;

    private ObservableList<Recipe> recipes; // Added line

    @FXML
    void backToUserScreen(ActionEvent event) {

    }
    @FXML
    void createRecipe(ActionEvent event) {
        try {
            // Extracting values from text fields and trimming any excess whitespace
            String name = recipeNameField.getText().trim();
            String shortDescription = createShortDescription.getText().trim();
            String detailedDescription = createDetailedField.getText().trim();
            int servings = Integer.parseInt(numberOfServingsField.getText().trim()); // Parsing the string as an integer
            String ingredientString = createIngredientsField.getText().trim();
            String tagString = createTagsField.getText().trim();
    
            // Passing the extracted information to the DBUtils.createRecipe method
            DBUtils.createRecipe(name, shortDescription, detailedDescription, servings, ingredientString, tagString, event);
            
            // Optionally clear fields and refresh the recipe list after creation
            clearAndHideAnchorPane(createAnchor, createToggle);
            loadUserRecipes(recipes); // Assuming 'recipes' gets updated elsewhere after DB operation
    
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number of servings: " + e.getMessage());
            // Handle error (e.g., show an alert to the user)
        } catch (Exception e) {
            System.out.println("Error creating recipe: " + e.getMessage());
            // Handle any other exceptions
        }
    }

    @FXML
    void deleteRecipe(ActionEvent event) {

    }

    @FXML
    void modifyRecipe(ActionEvent event) {

    }

    @FXML
    void toggleCreateAnchor(ActionEvent event) {

    }

    @FXML
    void toggleModifyAnchor(ActionEvent event) {

    }

    @FXML
    void hideCreateAnchor(ActionEvent event) {
        clearAndHideAnchorPane(createAnchor, createToggle); // Clear fields and hide the modifyAnchor

    }

    @FXML
    void hideModifyAnchor(ActionEvent event) {
        clearAndHideAnchorPane(modifyAnchor, modifyToggle); // Clear fields and hide the modifyAnchor
    }

    @FXML
    void showCreateAnchor(ActionEvent event) {
        showAnchor(createAnchor, createToggle);
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
        showAnchor(modifyAnchor, modifyToggle);
    }

    // A method to load recipes using DBUtils' loadRecipes method
    public void loadUserRecipes(ObservableList<Recipe> loggedInUserRecipes) { // Changed method signature
        recipes = loggedInUserRecipes; // Changed line
        setRecipeList();
    }

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
     * A method that takes an ObservableList and sets it as the list for the
     * memberList.
     * 
     * @param list The ObservableList to be set as the list for the memberList.
     */
    public void setRecipeList() {

        // Concatenate ID and name for each recipe and add to the list
        ObservableList<String> displayList = FXCollections.observableArrayList();
        for (Recipe recipe : recipes) {
            String recipeInfo = recipe.getRecipeId() + ": " + recipe.getName();
            displayList.add(recipeInfo);
        }

        recipeListView.setItems(displayList);
    }

}
