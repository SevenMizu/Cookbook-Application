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
import javafx.collections.transformation.FilteredList;
import cookbook.classes.Recipe; // Import Recipe class
import javafx.collections.FXCollections;
import javafx.scene.input.MouseEvent;





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

    @FXML
    private TextArea longDescriptionField;

    @FXML
    private TextArea shortDescriptionField;

    @FXML
    private Button showComments;

    @FXML
    private Button showIngredients;

    @FXML
    private Button showTags;

    private ObservableList<Recipe> recipes; // Added line
    private FilteredList<Recipe> filteredData;

        public void initialize() {
        // Assuming initialize method is where you set up the bindings
        if (recipes == null) {
            recipes = FXCollections.observableArrayList();
        }
        filteredData = new FilteredList<>(recipes, p -> true);

        recipeSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(recipe -> {
                // If filter text is empty, display all recipes
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (recipe.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (recipe.asString(recipe.getIngredients()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (recipe.asString(recipe.getTags()).toLowerCase().contains(lowerCaseFilter)){
                     return true;
                 } // else if (recipe.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                //     return true;
                // }

                // Can add more conditions to check other properties

                return false;
            });
            // After applying the filter, you might want to sort the list
            ObservableList<Recipe> sortedList = FXCollections.observableArrayList(filteredData);
            FXCollections.sort(sortedList, (recipe1, recipe2) -> 
                recipe1.getName().compareToIgnoreCase(recipe2.getName())); // Sort by name, for example
            updateListView(sortedList);
        });

        // ... rest of the initialize method ...
    }

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
        initialize();
    }
    public void setActiveUserLabel(String text) {
        activeUserLabel.setText(text);
    }

    public void showManageMembersButton() {
        manageMembersButton.setVisible(true);
    }

    private void updateListView(ObservableList<Recipe> sortedList) {
        ObservableList<String> displayList = FXCollections.observableArrayList();
        for (Recipe recipe : sortedList) {
            displayList.add(recipe.getRecipeId() + ": " + recipe.getName());
        }
        recipesListView.setItems(displayList);
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

    @FXML
    void setRecipeDetails(MouseEvent event) {
        String selectedRecipe = recipesListView.getSelectionModel().getSelectedItem();
        if (selectedRecipe != null) {
            int id = Integer.parseInt(selectedRecipe.substring(0, selectedRecipe.indexOf(':')));
            Recipe recipe = recipes.stream()
                                   .filter(r -> r.getRecipeId() == (id))
                                   .findFirst()
                                   .orElse(null);
            if (recipe != null) {
                shortDescriptionField.setText(recipe.getShortDescription());
                longDescriptionField.setText("Number of Servings: " +  recipe.getServings() + "\n" + recipe.getDetailedDescription());
            }
        }
    }
}