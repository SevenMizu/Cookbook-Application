package cookbook.handlers;

import cookbook.DBUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartController {

    @FXML
    private Button dbConnect;

    @FXML
    void connectToDb(ActionEvent event) {
        DBUtils.connectAndWelcome(event);


    }

}
