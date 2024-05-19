package cookbook;

import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;

import java.util.List;


import cookbook.classes.Message;


public class MessageListCell extends ListCell<Message> {


    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        if (empty || message == null) {
            setText(null);
            setStyle("");
        } else {
            // allow wrapping
            setWrapText(true);
            List<String> splitMessage = splitMessage(message.getMessageText());
            setText(splitMessage.get(0)); 
            setTooltip(new Tooltip(splitMessage.get(1))); 
            if (message.isRead()) {
                setStyle("-fx-background-color: lightgray;");
            } else {
                setStyle("-fx-background-color: white;");
            }
        }
    }

    private List<String> splitMessage(String messageText) {
        return List.of(messageText.split(" : ", 2));
    }}
