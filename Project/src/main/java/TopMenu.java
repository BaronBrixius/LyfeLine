import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.fxml.FXML;

public class TopMenu extends HBox {

    @FXML
    MenuItem saveButton = new MenuItem();

    @FXML
    public void saveFile(ActionEvent actionEvent) {
        System.out.println("Save");
    }
}
