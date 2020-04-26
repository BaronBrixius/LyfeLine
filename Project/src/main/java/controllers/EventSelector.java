package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import database.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Optional;

public class EventSelector {
    @FXML public GridPane selector;
    @FXML public ComboBox<Timeline> timelineList;
    @FXML public ListView<EventNode> eventList;
    @FXML public Button viewButton;
    @FXML public ComboBox<String> sortBy;
    @FXML public Button deleteButton;
    @FXML public TextField searchBar;
    public Button newButton;
    private TimelineView parentController;

    public void initialize() {
        populateTimelineList();

        sortBy.getItems().addAll("Alphabetic", "Reverse Alphabetic", "Creation Date", "Reverse Creation Date");

        sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> {
            sortEvents(sortBy.getSelectionModel().getSelectedIndex());
        });

        timelineList.setCellFactory(param -> new ListCell<>() {         //changes how Timelines are displayed (name only)
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

        eventList.setCellFactory(param -> new ListCell<>() {         //changes how Events are displayed (name only)
            @Override
            protected void updateItem(EventNode item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getActiveEvent().getEventName() == null) {
                    setText(null);
                } else {
                    setText(item.getActiveEvent().getEventName());
                }
            }
        });

        timelineList.getSelectionModel().selectedIndexProperty().addListener(e -> {
            populateEventList();
            newButton.setDisable(timelineList.getSelectionModel().selectedIndexProperty() == null);
        });

        eventList.getSelectionModel().selectedIndexProperty().addListener(e -> {
            newButton.setDisable(timelineList.getSelectionModel().selectedIndexProperty() == null);
            viewButton.setDisable(eventList.getSelectionModel().selectedIndexProperty() == null);
            deleteButton.setDisable(eventList.getSelectionModel().selectedIndexProperty() == null);
        });
    }

    public void setParentController(TimelineView parentController) {             //TODO delete this inelegant solution
        this.parentController = parentController;
    }

    public void newEvent() throws IOException {
        parentController.addEvent(new Event());
        eventList.getSelectionModel().getSelectedItem().openEventViewer();
    }

    public void openEvent() throws IOException {
        eventList.getSelectionModel().getSelectedItem().openEventViewer();
    }

    public boolean deleteEvent() {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("Deleting this event will remove it from all other timelines as well.");
        confirmDelete.setContentText("Are you ok with this?");

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;

        try {
            if (eventList.getSelectionModel().getSelectedItem().getActiveEvent().getEventID() == 0)
                throw new IllegalArgumentException("event not in database");

            DBM.deleteFromDB(eventList.getSelectionModel().getSelectedItem().getActiveEvent());
            populateEventList();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    void setTimelineSelected(Timeline timelineToSelect){
        for (Timeline t : timelineList.getItems()) {
            if (timelineToSelect.equals(t)) {
                timelineList.getSelectionModel().select(t);
                break;
            }
        }
    }

    private void populateTimelineList() {
        /*Timeline all = new Timeline();
        all.setTimelineName("All");
        timelineList.getItems().add(all);*/
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineOwner = ?");
            stmt.setInt(1, GUIManager.loggedInUser.getUserID());      //uncomment this for real version
            timelineList.getItems().addAll(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
            timelineList.getSelectionModel().select(1);
        } catch (SQLException e) {
            System.err.println("Could not get timelines from database.");
        }
    }

    @FXML
    void populateEventList() {
        eventList.setItems(FXCollections.observableArrayList(parentController.getEventList()));
        eventList.getSelectionModel().clearSelection();
        newButton.setDisable(true);
        viewButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    public void sortEvents(int selection) {
        switch (selection) {
            case 0:
                eventList.getItems().sort(Comparator.comparing(t -> t.getActiveEvent().getEventName()));
                break;
            case 1:
                eventList.getItems().sort((t1, t2) -> (t2.getActiveEvent().getEventName().compareTo(t1.getActiveEvent().getEventName())));
                break;
            case 2:
                eventList.getItems().sort((t1, t2) -> (t2.getActiveEvent().getCreationDate().compareTo(t1.getActiveEvent().getCreationDate())));
                break;
            case 3:
                eventList.getItems().sort(Comparator.comparing(t -> t.getActiveEvent().getCreationDate()));
                break;
        }
    }

    public void search(ActionEvent actionEvent) {
        //not implemented yet
    }

    public void close(ActionEvent actionEvent) {
        parentController.rightSidebar.getChildren().remove(selector);
    }
}
