package cookbook.handlers;

import java.util.stream.Collectors;

import cookbook.AlertUtils;
import cookbook.DBUtils;
import cookbook.classes.Ingredient;
import cookbook.classes.Recipe;
import cookbook.classes.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.input.MouseEvent;


public class MyRecipesController {

    @FXML
    private CheckBox servingsCheck;

    @FXML
    private CheckBox shortDEscCheck;

    @FXML
    private CheckBox tagsCheck;

    @FXML
    private CheckBox detailedDescCheck;

    @FXML
    private CheckBox ingredientsCheck;

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
    private TextField modifyRecipeName;

    @FXML
    private CheckBox modifyRecipeNameCheck;

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
    private TextField modifyNumberOfServings;

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
        DBUtils.changeToUserHomeScene("xmls/userHomeScreen.fxml", event, DBUtils.getloggedInuser());


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
        String selectedItem = recipeListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.isEmpty()) {
            // Show an alert if no recipe is selected
            Alert alert = AlertUtils.createAlert(AlertType.WARNING, "No Selection", "No Recipe Selected", "Please select a recipe to delete.");
            alert.showAndWait();
        } else {
            // Extract the id from the recipeListView (format is "id: recipe name")
            try {
                int id = Integer.parseInt(selectedItem.split(":")[0].trim());
                // Get the recipe with that id from the recipes observable list
                Recipe recipeToDelete = recipes.stream()
                                               .filter(r -> r.getRecipeId() == id)
                                               .findFirst()
                                               .orElse(null);
                if (recipeToDelete != null) {
                    // Call DBUtils.deleteRecipe method
                    DBUtils.deleteRecipe(recipeToDelete, event);
                } else {
                    // Show an error alert if the recipe is not found
                    Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "Recipe Not Found", "The selected recipe could not be found.");
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                // Handle the case where the id is not properly formatted
                Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "Invalid Recipe ID", "Could not parse the recipe ID from the selection.");
                alert.showAndWait();
            }
        }    }


    @FXML
    void modifyRecipe(ActionEvent event) {
        String selectedItem = recipeListView.getSelectionModel().getSelectedItem();
        int activeCheckBoxes = countSelectedCheckboxes();

        if (activeCheckBoxes < 1) {
            Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "", "No changes made");
            alert.show();
        } else {
            try {
                // Extract the id from the recipeListView (format is "id: recipe name")
                String idString = selectedItem.split(":")[0].trim();
                int recipeId = Integer.parseInt(idString);
    
                // Get the recipe with that id from the recipes attribute
                Recipe recipeToUpdate = recipes.stream()
                                               .filter(r -> r.getRecipeId() == recipeId)
                                               .findFirst()
                                               .orElse(null);
    
                if (recipeToUpdate != null) {
                    // The recipe attributes present in the setString using the recipe's setMethods
                    applyUpdatesToRecipe(recipeToUpdate);
    
                    // DBUtils.modifyRecipe(the updated recipe instance);
                    DBUtils.modifyRecipe(recipeToUpdate, event);

                    clearAndHideAnchorPane(modifyAnchor, modifyDiscardButton);
                } else {
                    Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "Invalid Recipe", "Recipe not found");
                    alert.show();
                }
            } catch (NumberFormatException e) {
                Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "Parsing Error", "Invalid recipe ID");
                alert.show();
            } catch (Exception e) {
                Alert alert = AlertUtils.createAlert(AlertType.ERROR, "Error", "Update Error", e.getMessage());
                alert.show();
            }
        }
    }

    

    private void applyUpdatesToRecipe(Recipe recipe) {
        // You would parse the updateString here and apply changes to the recipe object
        // This example assumes you have setters for each field that might be modified
        if (modifyRecipeNameCheck.isSelected()) {
            recipe.setName(modifyRecipeName.getText());
        }
        if (detailedDescCheck.isSelected()) {
            recipe.setDetailedDescription(modifyDetailedDescription.getText());
        }
        if (ingredientsCheck.isSelected()) {
            recipe.setIngredients(modifyIngredientsField.getText());
        }
        if (shortDEscCheck.isSelected()) {
            recipe.setShortDescription(modifyShortDescription.getText());
        }
        if (servingsCheck.isSelected()) {
            recipe.setServings(Integer.parseInt(modifyNumberOfServings.getText()));
        }
        if (tagsCheck.isSelected()) {
            recipe.setTags(modifyTagsField.getText());
        }
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

    /**
     * Method to count how many checkboxes are selected for modifying recipe attributes.
     * @return The count of selected checkboxes.
     */
    private int countSelectedCheckboxes() {
        int count = 0;
        if (modifyRecipeNameCheck.isSelected()) count++;
        if (detailedDescCheck.isSelected()) count++;
        if (ingredientsCheck.isSelected()) count++;
        if (shortDEscCheck.isSelected()) count++;
        if (servingsCheck.isSelected()) count++;
        if (tagsCheck.isSelected()) count++;
        return count;
    }

        /**
     * Method that enables or disables text fields based on the state of associated checkboxes.
     * For each checkbox, if it is checked, the associated text field will be enabled;
     * otherwise, the text field will be disabled.
     */
    @FXML
    void handleCheckbox(ActionEvent event) {
        // Modify Recipe Name Field
        modifyRecipeName.setDisable(!modifyRecipeNameCheck.isSelected());

        // Modify Detailed Description Field
        modifyDetailedDescription.setDisable(!detailedDescCheck.isSelected());

        // Modify Ingredients Field
        modifyIngredientsField.setDisable(!ingredientsCheck.isSelected());

        // Modify Short Description Field
        modifyShortDescription.setDisable(!shortDEscCheck.isSelected());

        // Modify Tags Field
        modifyTagsField.setDisable(!tagsCheck.isSelected());

        // Number of Servings Field
        modifyNumberOfServings.setDisable(!servingsCheck.isSelected());
    }

    @FXML
    void setModifyFields(MouseEvent event) {
    String selectedRecipe = recipeListView.getSelectionModel().getSelectedItem();
    if (selectedRecipe != null) {
        int id = Integer.parseInt(selectedRecipe.split(":")[0].trim()); // Extracting the recipe ID from the selected item format "id: recipe name"
        Recipe recipe = recipes.stream()
                               .filter(r -> r.getRecipeId() == id)
                               .findFirst()
                               .orElse(null);
        if (recipe != null) {
            // Setting the text fields with the recipe details
            modifyRecipeName.setText(recipe.getName());
            modifyShortDescription.setText(recipe.getShortDescription());
            modifyDetailedDescription.setText(recipe.getDetailedDescription());
            modifyIngredientsField.setText(recipe.getIngredients().stream()
                                                 .map(Ingredient::getName)
                                                 .collect(Collectors.joining(", ")));
            modifyTagsField.setText(recipe.getTags().stream()
                                          .map(Tag::getName)
                                          .collect(Collectors.joining(", ")));
            modifyNumberOfServings.setText(Integer.toString(recipe.getServings()));
        }
    }
}

}

