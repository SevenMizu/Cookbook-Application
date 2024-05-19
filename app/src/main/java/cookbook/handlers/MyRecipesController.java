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
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ListCell;



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
    private AnchorPane rootAnchor;

    @FXML
    private TextField recipeNameField;

    private ObservableList<Recipe> recipes; // Added line


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
                Alert confirmationAlert = AlertUtils.createConfirmationAlert("Confirm Clear",
                        "You are about to clear the fields", "Do you want to proceed?");
                confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        createDetailedField.clear(); // Clear the usernameCreate field
                        createIngredientsField.clear();
                        createTagsField.clear();                
                        recipeNameField.clear();
                        createShortDescription.clear();
                        numberOfServingsField.clear();
                    }
                });

            }
        });


        }

    @FXML
    void backToUserScreen(ActionEvent event) {
        DBUtils.changeToUserHomeScene("xmls/userHomeScreen.fxml", event, DBUtils.getloggedInuser());


    }
    @FXML
    void createRecipe(ActionEvent event) {
        if (recipeListView.getSelectionModel().getSelectedItem() != null) {
            AlertUtils.createAlert(AlertType.WARNING, "", "", "Clear the selection before creating a new user.").showAndWait();
            return;
        }
        try {
            // Extracting values from text fields and trimming any excess whitespace
            validateFields(); // Method to validate the input fields
            String name = recipeNameField.getText().trim();
            String shortDescription = createShortDescription.getText().trim();
            String detailedDescription = createDetailedField.getText().trim();
            int servings = Integer.parseInt(numberOfServingsField.getText().trim()); // Parsing the string as an integer
            String ingredientString = createIngredientsField.getText().trim();
            String tagString = createTagsField.getText().trim();
    
            // Passing the extracted information to the DBUtils.createRecipe method
            DBUtils.createRecipe(name, shortDescription, detailedDescription, servings, ingredientString, tagString, event);
            
            // Optionally clear fields and refresh the recipe list after creation
            loadUserRecipes(recipes); // Assuming 'recipes' gets updated elsewhere after DB operation
    
        } catch (NumberFormatException e) {
            AlertUtils.createAlert(AlertType.ERROR, "Error", "Number Format Error", "Error parsing number of servings: " + e.getMessage()).showAndWait();
        } catch (Exception e) {
            AlertUtils.createAlert(AlertType.ERROR, "Error", "Creation Error", "Error creating recipe: " + e.getMessage()).showAndWait();
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
        }   

    }

    /**
 * Method to validate the input fields.
 * Alert the user if any field is empty after trimming.
 */
private void validateFields() {
    TextField[] fieldsToValidate = { recipeNameField, createShortDescription, createDetailedField, numberOfServingsField, createIngredientsField, createTagsField };
    for (TextField field : fieldsToValidate) {
        if (field.getText().trim().isEmpty()) {
            AlertUtils.createAlert(AlertType.WARNING, "Validation Error", "", "Please fill in all the fields.").showAndWait();
            throw new IllegalArgumentException("Empty field: " + field.getId());
        }
    }
}
@FXML
void modifyRecipe(ActionEvent event) {
    String selectedItem = recipeListView.getSelectionModel().getSelectedItem();
    if (selectedItem == null) {
        AlertUtils.createAlert(AlertType.WARNING, "", "", "Please select a recipe to modify.").showAndWait();
        return;
    }
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

        } else {
            AlertUtils.createAlert(AlertType.ERROR, "Error", "Invalid Recipe", "Recipe not found").showAndWait();
        }
    } catch (NumberFormatException e) {
        AlertUtils.createAlert(AlertType.ERROR, "Error", "Parsing Error", "Invalid recipe ID").showAndWait();
    } catch (Exception e) {
        AlertUtils.createAlert(AlertType.ERROR, "Error", "Update Error", e.getMessage()).showAndWait();
    }
}

    

    private void applyUpdatesToRecipe(Recipe recipe) {

            recipe.setName(recipeNameField.getText());
            recipe.setDetailedDescription(createDetailedField.getText());
            recipe.setIngredients(createIngredientsField.getText());
            recipe.setShortDescription(createShortDescription.getText());
            recipe.setServings(Integer.parseInt(numberOfServingsField.getText()));
            recipe.setTags(createTagsField.getText());
        
    }


    // A method to load recipes using DBUtils' loadRecipes method
    public void loadUserRecipes(ObservableList<Recipe> loggedInUserRecipes) { // Changed method signature
        recipes = loggedInUserRecipes; // Changed line
        setRecipeList();
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
            recipeNameField.setText(recipe.getName());
            createShortDescription.setText(recipe.getShortDescription());
            createDetailedField.setText(recipe.getDetailedDescription());
            createIngredientsField.setText(recipe.getIngredients().stream()
                                                 .map(Ingredient::getName)
                                                 .collect(Collectors.joining(", ")));
            createTagsField.setText(recipe.getTags().stream()
                                          .map(Tag::getName)
                                          .collect(Collectors.joining(", ")));
            numberOfServingsField.setText(Integer.toString(recipe.getServings()));
        }
    }
}

}









    
    