package cookbook;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class AlertUtils {

    // Method to create a custom alert
    public static Alert createAlert(AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }

    // Method to create a confirmation alert with custom parameters
    public static Alert createConfirmationAlert(String title, String headerText, String contentText) {
        Alert confirmationAlert = createAlert(AlertType.CONFIRMATION, title, headerText, contentText);
        confirmationAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
        return confirmationAlert;
    }
}