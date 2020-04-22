import java.sql.SQLException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;


public class Popup {

    @FXML private Text displayTxt;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;
    private ListView<Timeline> list;

    public void initialize() {
    }


    public void deleteConfirm(ActionEvent actionEvent) {
    	try {
			DBM.deleteFromDB(list.getSelectionModel().getSelectedItem());
		} catch (SQLException e) {
			e.printStackTrace();
		}
        list.getItems().remove(list.getSelectionModel().getSelectedIndex());
		((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

    public void deleteCancel(ActionEvent actionEvent) {
		((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

    public void setDisplayTxt(String displayTxt) {
        this.displayTxt.setText(displayTxt);
    }

    public void setList(ListView<Timeline> list) {
        this.list = list;
    }
}
