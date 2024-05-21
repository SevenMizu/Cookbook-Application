package cookbook.handlers;

import cookbook.DBUtils;
import cookbook.MessageListCell;
import cookbook.classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.controlsfx.control.textfield.TextFields;

import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;


public class UserScreenController {

    // UI Components
    @FXML private Button addCommentButton;
    @FXML private TextField addCommentField;
    @FXML private TextArea commentTextArea;
    @FXML private Label activeUserLabel;
    @FXML private Button logoutButton;
    @FXML private CheckBox favouriteCheck;
    @FXML private Button manageMembersButton;
    @FXML private Button myRecipesButton;
    @FXML private TextArea recipe;
    @FXML private TextField recipeSearchBar;
    @FXML private ListView<String> recipesListView;
    @FXML private TextArea longDescriptionField;
    @FXML private TextArea shortDescriptionField;
    @FXML private Button showComments;
    @FXML private Button showIngredients;
    @FXML private Button showTags;
    @FXML private ContextMenu sendContextMenu;
    @FXML private MenuItem sendMenuButton;
    @FXML private TextField sendUsernameField;
    @FXML private ListView<Message> inboxList;
    @FXML private Button helpButton;

    // Data Models
    private ObservableList<Message> messages;
    private ObservableList<Recipe> recipes;
    private FilteredList<Recipe> filteredData;
    private ObservableList<User> users;


    @FXML
    public void initialize() {
        loadUsers();
        loadMessages();
        setupInboxList();
        setupAutoComplete();
        setupRecipeSearch();
        setupSendMessageSubMenu();
    }

    private void validateFields(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                showErrorMessage("Please fill in all the fields.");
                throw new IllegalArgumentException("Empty field: " + field.getId());
            }
        }
    }
    private void loadUsers() {
        users = DBUtils.loadUsers();
    }

    private void loadMessages() {
        messages = DBUtils.loadMessages();
        inboxList.setItems(messages);
    }

    private void setupInboxList() {
        inboxList.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> listView) {
                return new MessageListCell();
            }
        });
        inboxList.setOnMouseClicked(this::handleInboxSelection);
    }

    private void setupAutoComplete() {
        ObservableList<String> usernames = users.stream()
                .map(User::getUsername)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        TextFields.bindAutoCompletion(sendUsernameField, usernames);
    }

    private void setupRecipeSearch() {
        if (recipes == null) {
            recipes = FXCollections.observableArrayList();
        }
        filteredData = new FilteredList<>(recipes, p -> true);
        recipeSearchBar.textProperty().addListener((observable, oldValue, newValue) -> filterRecipes(newValue));
    }

    private void filterRecipes(String filterText) {
        filteredData.setPredicate(recipe -> {
            if (filterText == null || filterText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = filterText.toLowerCase();
            return recipe.getName().toLowerCase().contains(lowerCaseFilter) ||
                    recipe.asString(recipe.getIngredients()).toLowerCase().contains(lowerCaseFilter) ||
                    recipe.asString(recipe.getTags()).toLowerCase().contains(lowerCaseFilter);
        });
        ObservableList<Recipe> sortedList = FXCollections.observableArrayList(filteredData);
        FXCollections.sort(sortedList, (recipe1, recipe2) -> recipe1.getName().compareToIgnoreCase(recipe2.getName()));
        updateListView(sortedList);
    }

    private void updateListView(ObservableList<Recipe> sortedList) {
        ObservableList<String> displayList = sortedList.stream()
                .map(recipe -> recipe.getRecipeId() + ": " + recipe.getName())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        recipesListView.setItems(displayList);
    }

    @FXML
    private void handleInboxSelection(MouseEvent event) {
        Message selectedMessage = inboxList.getSelectionModel().getSelectedItem();
        if (selectedMessage != null) {
            int recipeId = selectedMessage.getRecipeId();
            Recipe recipe = recipes.stream()
                    .filter(r -> r.getRecipeId() == recipeId)
                    .findFirst()
                    .orElse(null);
            if (recipe != null) {
                recipeSearchBar.setText(recipe.getName());
            }
        } else {
            recipeSearchBar.clear();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        DBUtils.logout(event);
    }

    @FXML
    private void changeToMemberManagerScreen(ActionEvent event) {
        DBUtils.changeToManageMemberScreen("xmls/manageMembers.fxml", event);
    }

    public void loadRecipes() {
        recipes = DBUtils.loadRecipes();
        setRecipeList();
        initialize();
    }

    public void setActiveUserLabel(String text) {
        activeUserLabel.setText(text);
    }

    public void showManageMembersButton() {
        manageMembersButton.setVisible(true);
    }

    public void setRecipeList() {
        ObservableList<String> displayList = recipes.stream()
                .map(recipe -> recipe.getRecipeId() + ": " + recipe.getName())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        recipesListView.setItems(displayList);
    }


    @FXML
    void showWheel(ActionEvent event) {

    }

    @FXML
    private void setRecipeDetails(MouseEvent event) {
        String selectedRecipe = recipesListView.getSelectionModel().getSelectedItem();
        if (selectedRecipe != null) {
            int id = Integer.parseInt(selectedRecipe.split(":")[0]);
            Recipe recipe = recipes.stream()
                    .filter(r -> r.getRecipeId() == id)
                    .findFirst()
                    .orElse(null);
            if (recipe != null) {
                updateRecipeDetails(recipe);
            }
        }
    }

    private void updateRecipeDetails(Recipe recipe) {
        shortDescriptionField.setText(recipe.getShortDescription());
        longDescriptionField.setText(
                "Number of Servings: " + recipe.getServings() + "\n" + recipe.getDetailedDescription());

        StringBuilder commentsDisplay = new StringBuilder();
        for (Comment comment : recipe.getComments()) {
            User commentUser = users.stream()
                    .filter(u -> u.getUserId() == comment.getAuthorID())
                    .findFirst()
                    .orElse(null);
            if (commentUser != null) {
                commentsDisplay.append("- @")
                        .append(commentUser.getUsername())
                        .append(": ")
                        .append(comment.getText())
                        .append("\n\n");
            }
        }
        commentTextArea.setText(commentsDisplay.toString());

        User loggedInUser = DBUtils.getloggedInuser();
        boolean isFavourite = loggedInUser != null &&
                loggedInUser.getFavouriteRecipeIds().contains(recipe.getRecipeId());
        favouriteCheck.setSelected(isFavourite);
    }

    @FXML
    private void switchtoMyRecipes(ActionEvent event) {
        DBUtils.changeToMyRecipeScreen("xmls/myRecipesScreen.fxml", event);
    }

    @FXML
    private void addCommentToDatabase(ActionEvent event) {
        String selectedRecipe = recipesListView.getSelectionModel().getSelectedItem();
        if (selectedRecipe != null) {
            int recipeId = Integer.parseInt(selectedRecipe.split(":")[0]);
            String comment = addCommentField.getText();

            if (comment.contains(":")) {
                showErrorMessage("Comment cannot contain \":\"");
                return;
            }

            DBUtils.addComment(recipeId, comment, event);
        } else {
            showErrorMessage("No recipe selected.");
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }

    private void createContextualMenu(ContextMenuEvent event, boolean isIngredients) {
        String selectedRecipe = recipesListView.getSelectionModel().getSelectedItem();
        if (selectedRecipe != null) {
            int recipeId = Integer.parseInt(selectedRecipe.split(":")[0]);
            Recipe selectedRecipeObj = recipes.stream()
                    .filter(r -> r.getRecipeId() == recipeId)
                    .findFirst()
                    .orElse(null);
            if (selectedRecipeObj != null) {
                ContextMenu contextMenu = new ContextMenu();
                List<? extends Object> listToAdd = isIngredients ? selectedRecipeObj.getIngredients() : selectedRecipeObj.getTags();
                if (listToAdd.isEmpty()) {
                    contextMenu.getItems().add(new MenuItem("None"));
                } else {
                    listToAdd.forEach(item -> contextMenu.getItems().add(new MenuItem(item.toString())));
                }
                contextMenu.show(isIngredients ? showIngredients : showTags, event.getScreenX(), event.getScreenY());
            }
        }
    }

    @FXML
    private void handleFavouriteCheck(ActionEvent event) {
        String selectedRecipe = recipesListView.getSelectionModel().getSelectedItem();
        if (selectedRecipe == null) {
            showErrorMessage("No recipe selected.");
            return;
        }

        int recipeId = Integer.parseInt(selectedRecipe.split(":")[0]);
        User loggedInUser = DBUtils.getloggedInuser();
        boolean isFavourite = loggedInUser != null && loggedInUser.getFavouriteRecipeIds().contains(recipeId);

        DBUtils.FavouriteAction action = isFavourite ? DBUtils.FavouriteAction.REMOVE : DBUtils.FavouriteAction.ADD;
        DBUtils.handleFavourite(recipeId, action, event);
    }

    @FXML
    private void contextShowIngredients(ContextMenuEvent event) {
        createContextualMenu(event, true);
    }

    @FXML
    private void contextShowTags(ContextMenuEvent event) {
        createContextualMenu(event, false);
    }

    private void setupSendMessageSubMenu() {
    sendMenuButton.setOnAction(event -> showSendMessageDialog());
}

@FXML
    void showHelpScreen(ActionEvent event) {
        DBUtils.changeScene("xmls/helpScreen.fxml", event);
    }

private void showSendMessageDialog() {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Send Message");
    dialog.setHeaderText("Send Message");

    // Set the button types.
    ButtonType sendButtonType = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);

    // Create the content of the dialog.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField sendMessageField = new TextField();
    sendMessageField.setPromptText("Enter message");

    grid.add(new Label("Send Message"), 0, 0);
    grid.add(sendMessageField, 1, 0);

    dialog.getDialogPane().setContent(grid);

    // Request focus on the sendMessageField by default.
    Platform.runLater(() -> sendMessageField.requestFocus());

    // Convert the result to a sendMessageButton click.
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == sendButtonType) {
            String selectedRecipe = recipesListView.getSelectionModel().getSelectedItem();
            if (selectedRecipe != null) {
                validateFields(sendUsernameField, sendMessageField);
                int recipeId = Integer.parseInt(selectedRecipe.split(":")[0]);
                String sendUsername = sendUsernameField.getText();
                String recipeMessage = sendMessageField.getText();

                // Get recipientId associated with the username
                User recipient = users.stream()
                                      .filter(user -> user.getUsername().equals(sendUsername))
                                      .findFirst()
                                      .orElse(null);

                if (recipient != null) {
                    int recipientId = recipient.getUserId();
                    DBUtils.sendMessage(recipeId, recipientId, recipeMessage);
                } else {
                    showErrorMessage("Recipient not found.");
                }
            } else {
                showErrorMessage("No recipe selected.");
            }
        }
        return null;
    });

    dialog.showAndWait();
}

}
