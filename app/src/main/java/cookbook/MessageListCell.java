package cookbook;

import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.css.PseudoClass;

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
            
            setText(message.getMessageText());
            setTooltip(new Tooltip(message.getMessageText()));
            if (message.isRead()) {
                setStyle("-fx-background-color: lightgray;");
            } else {
                setStyle("-fx-background-color: white;");
            }
        }
    }
}
