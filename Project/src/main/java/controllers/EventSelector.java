package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Optional;

public class EventSelector {
    @FXML
    public GridPane selector;
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
    public Button newButton;
    public Button addToTimelineButton;
    private TimelineView parentController;

    public void initialize() {
        populateTimelineList();

        sortBy.getItems().addAll("Alphabetic", "Reverse Alphabetic", "Creation Date", "Reverse Creation Date");
        sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> sortEvents(sortBy.getSelectionModel().getSelectedIndex()));

        timelineList.setCellFactory(param -> new ListCell<>() {       //changes how Timelines are displayed (name only)
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
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getEventName() == null) {
                    setText(null);
                } else {
                    setText(item.getEventName());
                }
            }
        });

        if (!GUIManager.loggedInUser.getAdmin()) {
            newButton.setVisible(false);
            deleteButton.setVisible(false);
            addToTimelineButton.setVisible(false);
        }

        timelineList.getSelectionModel().selectedIndexProperty().addListener(e -> {

                populateEventList();
            if (GUIManager.loggedInUser.getAdmin())
                newButton.setDisable(timelineList.getSelectionModel().selectedIndexProperty() == null);
        });

        eventList.getSelectionModel().selectedIndexProperty().addListener(e -> {
            viewButton.setDisable(eventList.getSelectionModel().selectedIndexProperty() == null);

            if (GUIManager.loggedInUser.getAdmin()) {
                addToTimelineButton.setDisable(eventList.getSelectionModel().selectedIndexProperty() == null);
                if (GUIManager.loggedInUser.getUserID() == timelineList.getSelectionModel().getSelectedItem().getTimelineOwnerID()) {
                    newButton.setDisable(timelineList.getSelectionModel().selectedIndexProperty() == null);     //only owner can edit
                    deleteButton.setDisable(timelineList.getSelectionModel().selectedIndexProperty() == null);
                }
            }
        });
    }

    void setParentController(TimelineView parentController) {             //TODO delete this inelegant solution
        this.parentController = parentController;
    }

    @FXML
    void newEvent() {
        parentController.openEventEditor(new Event(), true);
    }

    @FXML
    void openEvent() {
        parentController.openEventEditor(eventList.getSelectionModel().getSelectedItem(), false);
    }

    public boolean deleteButton() {
        return deleteEvent(eventList.getSelectionModel().getSelectedItem());
    }

    boolean deleteEvent(Event eventToDelete) {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("Deleting " + eventToDelete.getEventName() + " will remove it from all other timelines as well.");
        confirmDelete.setContentText("Are you ok with this?");

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.CANCEL)
            return false;

        try {
            if (eventToDelete.getEventID() == 0)
                throw new IllegalArgumentException("event not in database");

            DBM.deleteFromDB(eventToDelete);
            populateEventList();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    void populateTimelineList() {
        /*Timeline all = new Timeline();
        all.setTimelineName("All");
        timelineList.getItems().add(all);*/
        try {
            Timeline currentSelected = timelineList.getSelectionModel().getSelectedItem();
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
            timelineList.getItems().setAll(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
            setTimelineSelected(currentSelected);
        } catch (SQLException e) {
            System.err.println("Could not get timelines from database.");
        }
    }

    void setTimelineSelected(Timeline timelineToSelect) {
        if (timelineToSelect == null) {
            timelineList.getSelectionModel().select(-1);
            return;
        }
        for (Timeline t : timelineList.getItems()) {
            if (timelineToSelect.equals(t)) {
                timelineList.getSelectionModel().select(t);
                break;
            }
        }
    }

    @FXML
    void populateEventList() {
        if (timelineList.getSelectionModel().getSelectedItem() != null)
            eventList.setItems(FXCollections.observableArrayList(timelineList.getSelectionModel().getSelectedItem().getEventList()));
        eventList.getSelectionModel().clearSelection();
        newButton.setDisable(true);
        viewButton.setDisable(true);
        addToTimelineButton.setDisable(true);
        deleteButton.setDisable(true);
        parentController.populateDisplay();
    }

    public void sortEvents(int selection) {
        switch (selection) {
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
    }

    public void search() {
        //not implemented yet
    }

    public void close() {
        parentController.rightSidebar.getChildren().remove(selector);
    }

    public void setActiveTimeline() {
        parentController.setActiveTimeline(timelineList.getSelectionModel().getSelectedItem());
    }

    public void addToTimeline() {
        try {
            if (eventList.getSelectionModel().getSelectedItem().addToTimeline(parentController.activeTimeline.getTimelineID())) {
                parentController.activeTimeline.getEventList().add(eventList.getSelectionModel().getSelectedItem());
                parentController.populateDisplay();
                System.out.println("Event added to " + parentController.activeTimeline + " timeline."); // remove this later once more user feedback is implemented
            } else
                System.out.println("Event is already on " + parentController.activeTimeline + " timeline.");
        } catch (SQLException e) {
            System.out.println("Timeline not found.");
        }
    }
}
