import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXML;

public class EventSelector {
    @FXML
    public ComboBox<Timeline> timelineList;
    @FXML
    public ListView<Event> eventList;

    public void initialize(){
        populateDisplay();
    }

    public void openEvent(ActionEvent actionEvent) {
        eventList.getSelectionModel().getSelectedItem();
    }

    public void close(ActionEvent actionEvent) {
    }

    public void deleteEvent(ActionEvent actionEvent) {
    }

    private void populateDisplay(){
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineOwner = ?");
            stmt.setInt(1, /*GUIManager.loggedInUser.getUserID()*/ 1);
            timelineList.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
        } catch (SQLException e){
            System.err.println("Could not get timelines from database.");
        }

        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM events WHERE TimelineID = ?");
            stmt.setInt(1, timelineList.getSelectionModel().getSelectedItem().getTimelineID());
            eventList.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Event())));
        } catch (SQLException e){
            System.err.println("Could not get timelines from database.");
        }
    }
}
