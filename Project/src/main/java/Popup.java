import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.sql.SQLException;


public class Popup {


    @FXML private Text timelineDeletionText;
    @FXML private Text displayTxt;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;
    private ListView<Timeline> list;
    private ListView<Event> events;
    private int mode;

    public void initialize() {

    }

    public void setMode(int mode) {
        this.mode = mode;
    }


    public void deleteConfirm() {
        try {
            switch (mode) {
                case 1:
                    DBM.deleteFromDB(list.getSelectionModel().getSelectedItem());
                    break;
                case 2:
                    DBM.deleteFromDB(events.getSelectionModel().getSelectedItem());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        list.getItems().remove(list.getSelectionModel().getSelectedIndex());

    }

    public void close() {
        btnCancel.getScene().getWindow().hide();
    }

    public void setDisplayTxt(String displayTxt) {
        this.timelineDeletionText.setText(displayTxt);
    }

    public void setList(ListView<Timeline> list) {
        this.list = list;

    }

    public void setEvents(ListView<Event> events) {
        this.events = events;
    }

    public void confirm(ActionEvent actionEvent) {
        switch (mode) {
            case 1:
            case 2:
                deleteConfirm();
                break;
        }

        close();
    }
}
