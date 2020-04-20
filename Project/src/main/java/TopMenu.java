import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.fxml.FXML;

public class TopMenu {

    @FXML
    MenuItem saveButton = new MenuItem();
    @FXML
    Menu loggedInStatus = new Menu();

    @FXML
    public void saveFile(ActionEvent actionEvent) {
        System.out.println("Save");
    }
    
    @FXML
    public void updateLoggedInStatus() {
    	System.out.println("hello");
    }
}
