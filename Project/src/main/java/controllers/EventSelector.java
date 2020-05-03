package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EventSelector {
    @FXML
    GridPane selector;
    @FXML
    ComboBox<Timeline> timelineComboBox;
    @FXML
    ListView<Event> eventListView;
    @FXML
    Button viewButton;
    @FXML
    ComboBox<String> sortBy;
    @FXML
    Button deleteButton;
    @FXML
    TextField searchBar;
    @FXML
    Button newButton;
    @FXML
    Button addToTimelineButton;
    private TimelineView parentController;
    private FilteredList<Event> filterableEventList;
    private List<List<Integer>> timelineEventLinks;

    public void initialize() {
        populateTimelineList();

        sortBy.getItems().addAll("Alphabetic", "Reverse Alphabetic", "Creation Date", "Reverse Creation Date");
        sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> sortEvents(sortBy.getSelectionModel().getSelectedIndex()));

        if (!GUIManager.loggedInUser.getAdmin()) {
            newButton.setVisible(false);
            deleteButton.setVisible(false);
            addToTimelineButton.setVisible(false);
        }

        populateEventList();

        //formatting for timeline and event selectors
        timelineComboBox.setCellFactory(param -> new ListCell<>() {       //changes how Timelines are displayed (name only)
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

        eventListView.setCellFactory(param -> new ListCell<>() {         //changes how Events are displayed (name only)
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });


        //listeners for timeline and event selectors
        timelineComboBox.getSelectionModel().selectedIndexProperty().addListener(event -> {
            filterEvents();
            eventListView.getSelectionModel().clearSelection();
            disableEventControlButtons();

            if (timelineComboBox.getSelectionModel().getSelectedIndex() < 0)
                newButton.setDisable(true);
            else
                newButton.setDisable(!GUIManager.loggedInUser.getAdmin()
                        && GUIManager.loggedInUser.getUserID() != timelineComboBox.getSelectionModel().getSelectedItem().getOwnerID());

        });

        eventListView.getSelectionModel().selectedIndexProperty().addListener(e -> {
            viewButton.setDisable(eventListView.getSelectionModel().getSelectedIndex() < 0);

            if (GUIManager.loggedInUser.getAdmin()) {           //if admin, allow editing events
                addToTimelineButton.setDisable(eventListView.getSelectionModel().getSelectedIndex() < 0);

                if (timelineComboBox.getSelectionModel().getSelectedIndex() < 0) {     //no adding/deleting from null timeline
                    newButton.setDisable(true);
                    deleteButton.setDisable(true);
                } else {        //only owner can edit
                    newButton.setDisable(GUIManager.loggedInUser.getUserID() != timelineComboBox.getSelectionModel().getSelectedItem().getOwnerID());
                    deleteButton.setDisable(GUIManager.loggedInUser.getUserID() != timelineComboBox.getSelectionModel().getSelectedItem().getOwnerID());
                }
            }
        });
    }

    private void disableEventControlButtons() {
        newButton.setDisable(true);
        viewButton.setDisable(true);
        addToTimelineButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void filterEvents() {
        if (timelineComboBox.getSelectionModel().getSelectedIndex() < 0)    //if no selection, display everything
            filterableEventList.setPredicate(e -> true);
        else
            filterableEventList.setPredicate(e -> (timelineEventLinks.stream().anyMatch(                        //checks the junction table
                    te -> te.get(0) == timelineComboBox.getSelectionModel().getSelectedItem().getID()   //filters by the selected timeline
                            && e.getID() == te.get(1))));                                                  //and returns whether each event is on that timeline
    }

    void setParentController(TimelineView parentController) {             //TODO delete this inelegant solution
        this.parentController = parentController;
    }

    @FXML
    void newEvent() {
        openEditor(new Event(), true);
    }

    @FXML
    void openEvent() {
        openEditor(eventListView.getSelectionModel().getSelectedItem(), false);
    }

    private void openEditor(Event eventToOpen, boolean editable) {
        parentController.eventEditorController.setEvent(eventToOpen);
        parentController.eventEditorController.toggleEditable(editable);
        parentController.rightSidebar.getChildren().add(parentController.eventEditorController.editor);
    }

    public boolean deleteButton() {
        return deleteEvent(eventListView.getSelectionModel().getSelectedItem());
    }

    boolean deleteEvent(Event eventToDelete) {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("Deleting " + eventToDelete.getName() + " will remove it from all other timelines as well.");
        confirmDelete.setContentText("Are you ok with this?");

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.CANCEL)
            return false;

        try {
            if (eventToDelete.getID() == 0)
                throw new IllegalArgumentException("event not in database");

            DBM.deleteFromDB(eventToDelete);
            populateTimelineList();
            populateEventList();
            parentController.populateDisplay();
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
            Timeline currentSelection = timelineComboBox.getSelectionModel().getSelectedItem();
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
            timelineComboBox.getItems().addAll(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
            setTimelineSelected(currentSelection);
        } catch (SQLException e) {
            System.err.println("Could not get timelines from database.");
        }
    }

    void setTimelineSelected(Timeline timelineToSelect) {
        timelineComboBox.getSelectionModel().select(-1);
        if (timelineToSelect == null)
            return;
        for (Timeline t : timelineComboBox.getItems()) {
            if (timelineToSelect.equals(t)) {
                timelineComboBox.getSelectionModel().select(t);
                break;
            }
        }
    }

    @FXML
    void populateEventList() {
        try {
            filterableEventList = new FilteredList<>(FXCollections.observableArrayList(DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events"), new Event())));
            eventListView.setItems(filterableEventList);
            timelineEventLinks = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM timelineevents"),
                    rs -> Arrays.asList(rs.getInt("TimelineID"), rs.getInt("EventID")));
            eventListView.getSelectionModel().select(-1);
        } catch (SQLException e) {
            System.out.println("Could not access events in database.");
        }
    }

    public void sortEvents(int selection) {
        switch (selection) {
            case 0:
                eventListView.getItems().sort(Comparator.comparing(Event::getName));
                break;
            case 1:
                eventListView.getItems().sort((t1, t2) -> (t2.getName().compareTo(t1.getName())));
                break;
            case 2:
                eventListView.getItems().sort((t1, t2) -> (t2.getCreationDate().compareTo(t1.getCreationDate())));
                break;
            case 3:
                eventListView.getItems().sort(Comparator.comparing(Event::getCreationDate));
                break;
        }
    }

    public void search() {
        //not implemented yet
    }

    public void close() {
        parentController.rightSidebar.getChildren().remove(selector);
    }

    public void addToTimeline() {
        try {
            if (eventListView.getSelectionModel().getSelectedItem().addToTimeline(parentController.activeTimeline.getID())) {
                parentController.activeTimeline.getEventList().add(eventListView.getSelectionModel().getSelectedItem());
                parentController.populateDisplay();
                System.out.println("Event added to " + parentController.activeTimeline + " timeline."); // remove this later once more user feedback is implemented
            } else
                System.out.println("Event is already on " + parentController.activeTimeline + " timeline.");
        } catch (SQLException e) {
            System.out.println("Timeline not found.");
        }
    }

    public void clearSelectedTimeline() {
        timelineComboBox.getSelectionModel().select(-1);
    }
}
