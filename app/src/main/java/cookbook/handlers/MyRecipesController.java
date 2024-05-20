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
    private CheckBox servingsCheck, shortDEscCheck, tagsCheck, detailedDescCheck, ingredientsCheck;

    @FXML
    private Button backButton, createDiscardButton, createRecipeMain, createToggle, deleteRecipeButton, modifyDiscardButton, modifyRecipeMain, modifyToggle;

    @FXML
    private AnchorPane createAnchor, modifyAnchor, rootAnchor;

    @FXML
    private TextField createDetailedField, createIngredientsField, createShortDescription, createTagsField, modifyRecipeName, modifyDetailedDescription, modifyIngredientsField, modifyShortDescription, modifyTagsField, modifyNumberOfServings, numberOfServingsField, recipeNameField;

    @FXML
    private ListView<String> recipeListView;

    private ObservableList<Recipe> recipes;

    /**
    * Initializes the controller.
    * Sets up the event filter for clearing fields on empty cell clicks.
    */
    @FXML
    void initialize() {
        setupClearSelectionOnEmptyCell();
    }

    /**
    * Sets up the event filter to clear selection on empty cell clicks.
    */
    private void setupClearSelectionOnEmptyCell() {
        rootAnchor.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Node clickedNode = event.getPickResult().getIntersectedNode();
            boolean clickedEmptyListCell = false;

            while (clickedNode != null) {
                if (clickedNode instanceof ListCell && ((ListCell<?>) clickedNode).getItem() == null) {
                    clickedEmptyListCell = true;
                    break;
                }
                clickedNode = clickedNode.getParent();
            }

            if (clickedEmptyListCell) {
                showClearFieldsConfirmation();
            }
        });
    }

    /**
    * Shows a confirmation alert to clear fields and clears the fields if confirmed.
    */
    private void showClearFieldsConfirmation() {
        Alert confirmationAlert = AlertUtils.createConfirmationAlert("Confirm Clear", "You are about to clear the fields", "Do you want to proceed?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                clearCreateFields();
            }
        });
    }

    /**
    * Clears all input fields in the create recipe form.
    */
    private void clearCreateFields() {
        createDetailedField.clear();
        createIngredientsField.clear();
        createTagsField.clear();
        recipeNameField.clear();
        createShortDescription.clear();
        numberOfServingsField.clear();
    }

    /**
    * Navigates back to the user home screen.
    * 
    * @param event The action event triggering the method.
    */
    @FXML
    void backToUserScreen(ActionEvent event) {
        DBUtils.changeToUserHomeScene("xmls/userHomeScreen.fxml", event, DBUtils.getloggedInuser());
    }

    /**
    * Creates a new recipe with the provided input fields if validation passes.
    * Shows appropriate error messages if validation fails or if an exception occurs.
    * 
    * @param event The action event triggering the method.
    */
    @FXML
    void createRecipe(ActionEvent event) {
        if (recipeListView.getSelectionModel().getSelectedItem() != null) {
            AlertUtils.createAlert(AlertType.WARNING, "", "", "Clear the selection before creating a new recipe.").showAndWait();
            return;
        }
        try {
            validateFields();
            String name = recipeNameField.getText().trim();
            String shortDescription = createShortDescription.getText().trim();
            String detailedDescription = createDetailedField.getText().trim();
            int servings = Integer.parseInt(numberOfServingsField.getText().trim());
            String ingredientString = createIngredientsField.getText().trim();
            String tagString = createTagsField.getText().trim();

            DBUtils.createRecipe(name, shortDescription, detailedDescription, servings, ingredientString, tagString, event);
            loadUserRecipes(recipes);

        } catch (NumberFormatException e) {
            AlertUtils.createAlert(AlertType.ERROR, "Error", "Number Format Error", "Error parsing number of servings: " + e.getMessage()).showAndWait();
        } catch (Exception e) {
            AlertUtils.createAlert(AlertType.ERROR, "Error", "Creation Error", "Error creating recipe: " + e.getMessage()).showAndWait();
        }
    }

    /**
    * Deletes the selected recipe from the list and the database.
    * Shows an error message if no recipe is selected or if an exception occurs.
    * 
    * @param event The action event triggering the method.
    */
    @FXML
    void deleteRecipe(ActionEvent event) {
        String selectedItem = recipeListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.isEmpty()) {
            AlertUtils.createAlert(AlertType.WARNING, "No Selection", "No Recipe Selected", "Please select a recipe to delete.").showAndWait();
        } else {
            try {
                int id = Integer.parseInt(selectedItem.split(":")[0].trim());
                Recipe recipeToDelete = recipes.stream().filter(r -> r.getRecipeId() == id).findFirst().orElse(null);
                if (recipeToDelete != null) {
                    DBUtils.deleteRecipe(recipeToDelete, event);
                } else {
                    AlertUtils.createAlert(AlertType.ERROR, "Error", "Recipe Not Found", "The selected recipe could not be found.").showAndWait();
                }
            } catch (NumberFormatException e) {
                AlertUtils.createAlert(AlertType.ERROR, "Error", "Invalid Recipe ID", "Could not parse the recipe ID from the selection.").showAndWait();
            }
        }
    }

    /**
    * Validates the input fields to ensure none are empty.
    * Throws an IllegalArgumentException if any field is empty and shows a warning alert.
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

    /**
    * Modifies the selected recipe with the provided input fields if validation passes.
    * Shows appropriate error messages if validation fails or if an exception occurs.
    * 
    * @param event The action event triggering the method.
    */
    @FXML
    void modifyRecipe(ActionEvent event) {
        String selectedItem = recipeListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertUtils.createAlert(AlertType.WARNING, "", "", "Please select a recipe to modify.").showAndWait();
            return;
        }
        try {
            int recipeId = Integer.parseInt(selectedItem.split(":")[0].trim());
            Recipe recipeToUpdate = recipes.stream().filter(r -> r.getRecipeId() == recipeId).findFirst().orElse(null);

            if (recipeToUpdate != null) {
                applyUpdatesToRecipe(recipeToUpdate);
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

    /**
    * Applies updates to the provided recipe with the input field values.
    * 
    * @param recipe The recipe to update.
    */
    private void applyUpdatesToRecipe(Recipe recipe) {
        recipe.setName(recipeNameField.getText());
        recipe.setDetailedDescription(createDetailedField.getText());
        recipe.setIngredients(createIngredientsField.getText());
        recipe.setShortDescription(createShortDescription.getText());
        recipe.setServings(Integer.parseInt(numberOfServingsField.getText()));
        recipe.setTags(createTagsField.getText());
    }

    /**
    * Loads the user's recipes into the observable list and sets the recipe list view.
    * 
    * @param loggedInUserRecipes The observable list of the logged-in user's recipes.
    */
    public void loadUserRecipes(ObservableList<Recipe> loggedInUserRecipes) {
        recipes = loggedInUserRecipes;
        setRecipeList();
    }

    /**
    * Sets the recipe list view with the loaded recipes.
    */
    public void setRecipeList() {
        ObservableList<String> displayList = recipes.stream()
                .map(recipe -> recipe.getRecipeId() + ": " + recipe.getName())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        recipeListView.setItems(displayList);
    }

    /**
    * Sets the modify fields with the selected recipe's details.
    * 
    * @param event The mouse event triggering the method.
    */
    @FXML
    void setModifyFields(MouseEvent event) {
        String selectedRecipe = recipeListView.getSelectionModel().getSelectedItem();
        if (selectedRecipe != null) {
            int id = Integer.parseInt(selectedRecipe.split(":")[0].trim());
            Recipe recipe = recipes.stream().filter(r -> r.getRecipeId() == id).findFirst().orElse(null);
            if (recipe != null) {
                recipeNameField.setText(recipe.getName());
                createShortDescription.setText(recipe.getShortDescription());
                createDetailedField.setText(recipe.getDetailedDescription());
                createIngredientsField.setText(recipe.getIngredients().stream().map(Ingredient::getName).collect(Collectors.joining(", ")));
                createTagsField.setText(recipe.getTags().stream().map(Tag::getName).collect(Collectors.joining(", ")));
                numberOfServingsField.setText(Integer.toString(recipe.getServings()));
            }
        }
    }
}
