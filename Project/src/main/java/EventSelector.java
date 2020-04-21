import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;

public class EventSelector {
    @FXML
    public ComboBox<Timeline> timelineList;
    @FXML
    public ListView<Event> eventList;
    @FXML
    public Button viewButton;
    @FXML
    public ComboBox<String> sortBy;
    @FXML
    public Button deleteButton;
    @FXML
    public TextField searchBar;

    public void initialize() {
        populateTimelineList();

        sortBy.getItems().addAll("Alphabetic", "Reverse Alphabetic", "Creation Date", "Reverse Creation Date");

        sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> {
            switch (sortBy.getSelectionModel().getSelectedIndex()) {
                case 0:
                    eventList.getItems().sort(Comparator.comparing(Event::getEventName));
                    break;
                case 1:
                    eventList.getItems().sort((t1, t2) -> (t2.getEventName().compareTo(t1.getEventName())));
                    break;
                case 2:
                    eventList.getItems().sort((t1, t2) -> (t2.getCreationDate().compareTo(t1.getCreationDate())));
                    break;
                case 3:
                    eventList.getItems().sort(Comparator.comparing(Event::getCreationDate));
                    break;
            }
        });


        timelineList.getSelectionModel().selectedIndexProperty().addListener(e -> {
            populateEventList();
            viewButton.setDisable(true);
            deleteButton.setDisable(true);
        });

        eventList.getSelectionModel().selectedIndexProperty().addListener(e -> {
            viewButton.setDisable(eventList.getSelectionModel().selectedIndexProperty() == null);
            deleteButton.setDisable(eventList.getSelectionModel().selectedIndexProperty() == null);
        });
    }

    public void newEvent(ActionEvent actionEvent) throws IOException {
        GUIManager.swapScene("EventEditor");
    }

    public void openEvent(ActionEvent actionEvent) throws IOException {
        EventEditor_GUI editor = GUIManager.swapScene("EventEditor");
        editor.setEvent(eventList.getSelectionModel().getSelectedItem());
        editor.toggleEditMode();
    }

    public void close(ActionEvent actionEvent) {
        //go back to somewhere
    }

    public void deleteEvent(ActionEvent actionEvent) throws SQLException {
        //probably want a popup
        DBM.deleteFromDB(eventList.getSelectionModel().getSelectedItems());
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

    private void populateEventList() {
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

    public void sortEvents(ActionEvent actionEvent) {

    }

    public void search(ActionEvent actionEvent) {
    }
}
