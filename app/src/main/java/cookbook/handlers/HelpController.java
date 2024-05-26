package cookbook.handlers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import cookbook.DBUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class HelpController {
    @FXML
    private TextField searchField;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextFlow textFlow1;
    @FXML
    private TextFlow textFlow2;
    @FXML
    private TextFlow textFlow3;
    @FXML
    private TextFlow textFlow4;
    @FXML
    private TextFlow textFlow5;
    @FXML
    private TextFlow textFlow6;
    @FXML
    private Button backToHome;

    @FXML
    public void initialize() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    private void handleSearch(String query) {
        if (query.isEmpty()) {
            resetHighlighting();
            return;
        }

        highlightTextFlow(textFlow1, query);
        highlightTextFlow(textFlow2, query);
        highlightTextFlow(textFlow3, query);
        highlightTextFlow(textFlow4, query);
        highlightTextFlow(textFlow5, query);
        highlightTextFlow(textFlow6, query);
    }

    private void highlightTextFlow(TextFlow textFlow, String query) {
        String textContent = "";
        for (var node : textFlow.getChildren()) {
            if (node instanceof Text) {
                textContent += ((Text) node).getText();
            }
        }

        textFlow.getChildren().clear();
        int index = textContent.toLowerCase().indexOf(query.toLowerCase());
        if (index >= 0) {
            Text before = new Text(textContent.substring(0, index));
            Text highlighted = new Text(textContent.substring(index, index + query.length()));
            highlighted.setStyle("-fx-fill: green; -fx-font-weight: bold;");
            Text after = new Text(textContent.substring(index + query.length()));
            textFlow.getChildren().addAll(before, highlighted, after);

            scrollPane.setVvalue(textFlow.getLayoutY() / anchorPane.getHeight());
        } else {
            textFlow.getChildren().add(new Text(textContent));
        }
    }

    private void resetHighlighting() {
        resetTextFlow(textFlow1);
        resetTextFlow(textFlow2);
        resetTextFlow(textFlow3);
        resetTextFlow(textFlow4);
        resetTextFlow(textFlow5);
        resetTextFlow(textFlow6);
    }

    private void resetTextFlow(TextFlow textFlow) {
        String textContent = "";
        for (var node : textFlow.getChildren()) {
            if (node instanceof Text) {
                textContent += ((Text) node).getText();
            }
        }
        textFlow.getChildren().clear();
        textFlow.getChildren().add(new Text(textContent));
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
}
