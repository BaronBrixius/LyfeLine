package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    private final ObservableList<Event> eventList = FXCollections.observableArrayList();
    private final FilteredList<Event> filterableEventList = new FilteredList<>(eventList);
    private final SortedList<Event> sortableEventList = new SortedList<>(filterableEventList);
    @FXML
    GridPane selector;
    @FXML
    ComboBox<Timeline> timelineComboBox;
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
    @FXML
    ListView<Event> eventListView;
    private TimelineView parentController;
    private List<List<Integer>> timelineEventLinks;

    public void initialize() {
        if (!GUIManager.loggedInUser.getAdmin()) {
            newButton.setVisible(false);
            deleteButton.setVisible(false);
            addToTimelineButton.setVisible(false);
        }

        eventListView.setItems(sortableEventList);

        populateDisplay();


        sortBy.getItems().setAll("Alphabetic", "Reverse Alphabetic", "Creation Date", "Reverse Creation Date", "Priority");
        sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> sortEvents(sortBy.getSelectionModel().getSelectedIndex()));

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


        timelineComboBox.getSelectionModel().selectedIndexProperty().addListener(event -> {     //on selecting a different timeline, clear the event selection and disable event controls
            filterEvents();
            eventListView.getSelectionModel().clearSelection();
            disableEventControlButtons(true);

            if (timelineComboBox.getSelectionModel().getSelectedIndex() < 0)
                newButton.setDisable(true);                                     //if no timeline selected, disable New Event button
            else                                                                //otherwise, allow based on ownership
                newButton.setDisable(GUIManager.loggedInUser.getUserID() != timelineComboBox.getSelectionModel().getSelectedItem().getOwnerID());

        });

        eventListView.getSelectionModel().selectedIndexProperty().addListener(e -> {
            viewButton.setDisable(eventListView.getSelectionModel().getSelectedIndex() < 0);    //if no event selected, disable view button

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

    private void disableEventControlButtons(boolean disable) {
        newButton.setDisable(disable);
        viewButton.setDisable(disable);
        addToTimelineButton.setDisable(disable);
        deleteButton.setDisable(disable);
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

    @FXML
    boolean deleteButton() {
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
            populateDisplay();
            parentController.populateDisplay();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    void populateDisplay() {
        Timeline currentSelection = timelineComboBox.getSelectionModel().getSelectedItem();
        populateTimelineList();
        populateEventList();
        setTimelineSelected(currentSelection);
    }

    void populateTimelineList() {
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
            timelineComboBox.getItems().setAll(DBM.getFromDB(stmt, new Timeline()));
        } catch (SQLException e) {
            System.err.println("Could not access timelines database.");
        }
    }

    void populateEventList() {
        try {
            eventList.setAll(DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events"), new Event()));
            timelineEventLinks = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM timelineevents"),
                    rs -> Arrays.asList(rs.getInt("TimelineID"), rs.getInt("EventID")));
        } catch (SQLException e) {
            System.out.println("Could not access events database.");
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

    void filterEvents() {
        if (timelineComboBox.getSelectionModel().getSelectedIndex() < 0)    //if no selection, display everything
            filterableEventList.setPredicate(e -> true);
        else
            filterableEventList.setPredicate(e -> (timelineEventLinks.stream().anyMatch(                //checks the junction table
                    te -> te.get(0) == timelineComboBox.getSelectionModel().getSelectedItem().getID()   //filters by the selected timeline
                            && e.getID() == te.get(1))));                                               //and returns whether each event is on that timeline
    }

    void sortEvents(int selection) {
        switch (selection) {
            case 0:
                sortableEventList.setComparator((e1, e2) -> (e1.getName().compareToIgnoreCase(e2.getName())));
                break;
            case 1:
                sortableEventList.setComparator((e1, e2) -> (e2.getName().compareToIgnoreCase(e1.getName())));
                break;
            case 2:
                sortableEventList.setComparator(Comparator.comparing(Event::getCreationDate).reversed());
                break;
            case 3:
                sortableEventList.setComparator(Comparator.comparing(Event::getCreationDate));
                break;
            case 4:
                sortableEventList.setComparator((e1, e2) -> (Integer.compare(e2.getEventPriority(), e1.getEventPriority())));
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
                populateEventList();
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
