import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Optional;

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

        timelineList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Timeline item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        eventList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getEventName() == null) {
                    setText(null);
                } else {
                    setText(item.getEventName());
                }
            }
        });


        timelineList.getSelectionModel().selectedIndexProperty().addListener(e -> {
            populateEventList();
        });

        eventList.getSelectionModel().selectedIndexProperty().addListener(e -> {
            viewButton.setDisable(eventList.getSelectionModel().selectedIndexProperty() == null);
            deleteButton.setDisable(eventList.getSelectionModel().selectedIndexProperty() == null);
        });
    }

    public void newEvent(ActionEvent actionEvent) throws IOException {
        EventEditor_GUI editor = GUIManager.swapScene("EventEditor");
        editor.headerText.setText("New Event");
        editor.setEvent(new Event());
    }

    public void openEvent(ActionEvent actionEvent) throws IOException {
        EventEditor_GUI editor = GUIManager.swapScene("EventEditor");
        editor.headerText.setText("Edit Event");
        editor.setEvent(eventList.getSelectionModel().getSelectedItem());
        editor.toggleEditable(false);
    }

    public void close(ActionEvent actionEvent) {
        //go back to somewhere
    }

    public boolean deleteEvent() throws SQLException, IOException {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("Deleting this event will remove it from all other timelines as well.");
        confirmDelete.setContentText("Are you ok with this?");

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;

        try {
            if (eventList.getSelectionModel().getSelectedItem().getEventID() == 0)
                throw new IllegalArgumentException("event not in database");

            DBM.deleteFromDB(eventList.getSelectionModel().getSelectedItem());
            populateEventList();
            return true;
        } catch (SQLException e) {
            return false;
        }
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

                eventList.getSelectionModel().clearSelection();
                viewButton.setDisable(true);
                deleteButton.setDisable(true);
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
