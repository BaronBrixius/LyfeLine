import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventSelector {
    @FXML
    public ComboBox<Timeline> timelineList;
    @FXML
    public ListView<Event> eventList;

    public void initialize() {
        populateTimelineList();

        timelineList.getSelectionModel().selectedIndexProperty().addListener(e ->
            populateEventList()
        );

    }

    public void openEvent(ActionEvent actionEvent) {
        eventList.getSelectionModel().getSelectedItem();
    }

    public void close(ActionEvent actionEvent) {
    }

    public void deleteEvent(ActionEvent actionEvent) {
    }

    private void populateTimelineList() {
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineOwner = ?");
            stmt.setInt(1, /*GUIManager.loggedInUser.getUserID()*/ 1);      //uncomment this for real version
            timelineList.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
        } catch (SQLException e) {
            System.err.println("Could not get timelines from database.");
        }
    }

    private void populateEventList(){
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM events a " +
                    "INNER JOIN timelineevents b " +
                    "ON a.EventID = b.EventID " +
                    "WHERE b.TimelineID = ? ");

            int timelineID = timelineList.getSelectionModel().getSelectedItem().getTimelineID();
            if (timelineID > 0) {
                stmt.setInt(1, timelineList.getSelectionModel().getSelectedItem().getTimelineID());
                eventList.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Event())));
            }
        } catch (SQLException e) {
            System.err.println("Could not get events from database.");
        }
    }
}
