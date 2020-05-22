package controllers;

import database.DBM;
import database.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

public class Dashboard {
    final List<Spinner<Integer>> startInputs = new ArrayList<>();
    final List<Spinner<Integer>> endInputs = new ArrayList<>();
    public Timeline timeline;
    public Label KeywordLabel;
    public Label RatingLabel;
    public StackPane stack;
    public ScrollPane listScrollPane;
    @FXML
    protected Button timelineViewButton;
    @FXML
    protected Button adminGUI;
    @FXML
    protected Button btnDelete;
    @FXML
    protected Button btnEdit;
    @FXML
    protected Button btnCreate;
    @FXML
    protected Button searchButton;
    @FXML
    protected ListView<Timeline> list;
    @FXML
    protected TextField searchInput;
    @FXML
    protected TextField searchTimelineName;
    @FXML
    protected TextField searchCreator;
    @FXML
    protected TextField searchKeywords;
    @FXML
    protected ComboBox<Integer> searchRating;
    @FXML
    protected Button clearButton;
    @FXML
    protected CheckBox cbOnlyViewPersonalLines;
    @FXML
    protected ComboBox<String> sortBy;
    @FXML
    protected GridPane gridButtons;
    @FXML
    protected GridPane advancedSearchView;
    @FXML
    protected GridPane startDates;
    @FXML
    protected GridPane endDates;
    @FXML
    protected Hyperlink AdvancedSearch;
    protected Timeline activeTimeline;
    protected FilteredList<Timeline> filteredTimelines;
    protected SortedList<Timeline> sortedTimelines;

    public void initialize() {
        //Set Up the Spinners for Start/End Inputs, would have bloated the .fxml and variable list a ton if these were in fxml
        setupTimeInputStartAndEnd("Year", -1000000000, 999999999, 0, 0, 0);
        setupTimeInputStartAndEnd("Month", 0, 12, 1, 0, 1);
        setupTimeInputStartAndEnd("Day", 0, 31, 2, 0, 2);
        setupTimeInputStartAndEnd("Hour", -1, 23, 3, 0, 3);
        setupTimeInputStartAndEnd("Minute", -1, 59, 0, 2, 4);
        setupTimeInputStartAndEnd("Second", -1, 59, 1, 2, 5);
        setupTimeInputStartAndEnd("Millisecond", -1, 999, 2, 2, 6);

        initializeButtons();

        // Initialised sorting
        sortBy.getSelectionModel().select(0);

        // Fill ListView with the timelines
        populateTimelineList();
        list.setCellFactory((ListView<Timeline> ls) -> new TimelineCellListCell());

        // Add sorting options
        sortBy.getItems().setAll("Alphabetically", "Reverse-Alphabetically", "Most Recent", "Oldest");
        // Sort order selection events
        sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> sortTimelines());
        sortBy.getSelectionModel().select(0);

        // Search field
        cbOnlyViewPersonalLines.selectedProperty().addListener(this::simpleSearch);
        searchInput.textProperty().addListener(this::simpleSearch);

        list.getSelectionModel().selectedIndexProperty().addListener(e -> {
            activeTimeline = list.getSelectionModel().getSelectedItem();
            updateDisplays();
        });

        //Ratings combobox in advanced search
        searchRating.getItems().setAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        searchRating.setButtonCell(new RatingsListCell());
        searchRating.setCellFactory(param -> new RatingsListCell());


        list.setPrefHeight((list.getItems().size() * 87) +  340);
        Platform.runLater(()->list.getSelectionModel().select(0));//TODO make the scroll pane focused more immediately
        GUIManager.mainStage.setTitle("Dashboard");
    }

    private void populateTimelineList() {
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
            var foo = FXCollections.observableList(DBM.getFromDB(stmt, new Timeline()));
            filteredTimelines = new FilteredList<>(foo);
            sortedTimelines = new SortedList<>(filteredTimelines);
            list.setItems(sortedTimelines);
        } catch (SQLException e) {
            System.err.println("Could not get timelines from database.");
        }
    }

    private void initializeButtons() {
        stack.getChildren().remove(advancedSearchView);
        btnCreate.setVisible(GUIManager.loggedInUser.getAdmin());
        btnCreate.setDisable(!GUIManager.loggedInUser.getAdmin());
        btnEdit.setVisible(GUIManager.loggedInUser.getAdmin());
        btnEdit.setDisable(list.getSelectionModel().isEmpty() || list.getSelectionModel().getSelectedItem().getOwnerID() != GUIManager.loggedInUser.getUserID());
        btnDelete.setVisible(GUIManager.loggedInUser.getAdmin());
        btnDelete.setDisable(list.getSelectionModel().isEmpty() || list.getSelectionModel().getSelectedItem().getOwnerID() != GUIManager.loggedInUser.getUserID());
        adminGUI.setVisible(GUIManager.loggedInUser.getAdmin());
        adminGUI.setDisable(!GUIManager.loggedInUser.getAdmin());
        timelineViewButton.setDisable(list.getSelectionModel().getSelectedItem() == null);
    }

    private void simpleSearch(Observable obs) {
        String searchText = searchInput.getText();
        if (searchText == null || searchText.isEmpty())
            filteredTimelines.setPredicate(timeline -> true);
        else
            filteredTimelines.setPredicate(timeline -> timeline.getName().toLowerCase().contains(searchText.toLowerCase())
                    || timeline.getKeywords().stream().anyMatch(k -> k.toLowerCase().contains(searchText.toLowerCase())));

        Predicate<Timeline> onlyPersonal = timeline -> timeline.getOwnerID() == GUIManager.loggedInUser.getUserID();
        if (cbOnlyViewPersonalLines.isSelected())
            filteredTimelines.setPredicate(onlyPersonal.and(filteredTimelines.getPredicate()));
        list.refresh();
        if (!list.getItems().isEmpty())
            list.getSelectionModel().select(0);
        list.setPrefHeight((list.getItems().size() * 87) +  340);
    }

    @FXML
    void searchAdvanced() {         //get list of IDs that satisfy search conditions, and apply as predicate to filteredlist
        ResultSet data = advancedResultSet();
        try {
            List<Integer> listOfIDs = parseResultsForAdvancedSearch(data);
            filteredTimelines.setPredicate(timeline -> listOfIDs.contains(timeline.getID()));
            list.getSelectionModel().select(0);
            list.setPrefHeight((list.getItems().size() * 87) +  340);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ResultSet advancedResultSet() {     //pull the relevant data from the database and pass back to search
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT t.*, u.UserName, COALESCE(AVG(r.Rating), 0) as Rating FROM timelines t " +
                    "INNER JOIN users u ON t.TimelineOwner = u.UserID " +
                    "LEFT JOIN ratings r ON t.TimelineID = r.TimeLineID " +
                    "GROUP BY t.TimelineID");
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    List<Integer> parseResultsForAdvancedSearch(ResultSet data) throws SQLException {
        List<Integer> out = new ArrayList<>();
        boolean addToList = true;

        while (data.next()) {
            //if the search box is filled, but doesn't match DB contents, don't add to list. check for each box
            //Timeline Name
            if (!searchTimelineName.getText().isEmpty() && !data.getString("TimelineName").toLowerCase().contains(searchTimelineName.getText().toLowerCase())) {
                addToList = false;
            }
            //Timeline Owner
            if (!searchCreator.getText().isEmpty() && !data.getString("UserName").toLowerCase().contains(searchCreator.getText().toLowerCase())) {
                addToList = false;
            }

            //Keywords
            Predicate<String> keywordMatches = k -> {
                try {
                    return data.getString("Keywords").toLowerCase().contains(k.toLowerCase());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            };
            if (!searchKeywords.getText().isEmpty() && !Arrays.stream(searchKeywords.getText().toLowerCase().split(" ")).allMatch(keywordMatches)) {
                addToList = false;
            }

            if (dateSearchedBy(startInputs)) {
                //Start LocalDateTime
                LocalDateTime startDateSpinner = readTimeInputs(startInputs);
                LocalDateTime startDateInDB = LocalDateTime.of(data.getInt("StartYear"), data.getInt("StartMonth"), data.getInt("StartDay"),
                        data.getInt("StartHour"), data.getInt("StartMinute"), data.getInt("StartSecond"), data.getInt("StartMillisecond"));

                if (startDateInDB.compareTo(startDateSpinner) < 0) {
                    addToList = false;
                }
            }

            if (dateSearchedBy(endInputs)) {
                //End LocalDateTime
                LocalDateTime endDateSpinner = readTimeInputs(endInputs);
                LocalDateTime endDateInDB = LocalDateTime.of(data.getInt("EndYear"), data.getInt("EndMonth"), data.getInt("EndDay"),
                        data.getInt("EndHour"), data.getInt("EndMinute"), data.getInt("EndSecond"), data.getInt("EndMillisecond"));

                if (endDateInDB.compareTo(endDateSpinner) > 0) {
                    addToList = false;
                }
            }

            //Rating
            if (searchRating.getSelectionModel().getSelectedIndex() > 0 && Math.ceil(data.getDouble("Rating")) < searchRating.getSelectionModel().getSelectedIndex())      //TODO implement after ratings
                addToList = false;

            if (addToList)
                out.add(data.getInt("TimelineID"));
            addToList = true;           //reset for next line of ResultSet
        }

        return out;
    }

    private boolean dateSearchedBy(List<Spinner<Integer>> inputs) {     //returns whether or not ANY inputs of either start or end dates are being used
        for (Spinner<Integer> s : inputs) {
            if (s.getValue() != ((SpinnerValueFactory.IntegerSpinnerValueFactory) s.getValueFactory()).getMin())
                return true;
        }
        return false;
    }

    private LocalDateTime readTimeInputs(List<Spinner<Integer>> inputs) {
        return LocalDateTime.of(
                (inputs.get(0).getValue() == -1000000000) ? 0 : inputs.get(0).getValue(),
                Math.max(1, inputs.get(1).getValue()),
                Math.max(1, inputs.get(2).getValue()),
                Math.max(0, inputs.get(3).getValue()),
                Math.max(0, inputs.get(4).getValue()),
                Math.max(0, inputs.get(5).getValue()),
                Math.max(0, inputs.get(6).getValue()));
    }

    @FXML
    public void toggleAdvancedSearch() {
        if (stack.getChildren().size() > 0) {
            stack.getChildren().remove(advancedSearchView);
            searchInput.setDisable(false);
            cbOnlyViewPersonalLines.setDisable(false);
        } else {
            clearAdvancedSearch();
            stack.getChildren().add(advancedSearchView);
            searchInput.setDisable(true);
            cbOnlyViewPersonalLines.setDisable(true);
        }
    }

    @FXML
    public void clearAdvancedSearch() {
        Timeline currentlySelectedTimeline = list.getSelectionModel().getSelectedItem();
        searchTimelineName.clear();
        searchCreator.clear();
        searchKeywords.clear();
        searchInput.clear();
        cbOnlyViewPersonalLines.setSelected(false);
        filteredTimelines.setPredicate(t -> true);
        list.getSelectionModel().select(currentlySelectedTimeline);
    }

    public void sortTimelines() {
        switch (sortBy.getSelectionModel().getSelectedIndex()) {
            case 0:
                sortedTimelines.setComparator((t1, t2) -> (t1.getName().compareToIgnoreCase(t2.getName())));
                break;
            case 1:
                sortedTimelines.setComparator((t1, t2) -> (t2.getName().compareToIgnoreCase(t1.getName())));
                break;
            case 2:
                sortedTimelines.setComparator(Comparator.comparing(Timeline::getCreationDate).reversed());
                break;
            case 3:
                sortedTimelines.setComparator(Comparator.comparing(Timeline::getCreationDate));
                break;
        }
    }

    @FXML
    public void adminScreen() throws IOException {
        GUIManager.swapScene("AdminRoleManager");
    }

    @FXML
    public TimelineView createTimeline() {
        Timeline t = new Timeline();
        t.setOwnerID(GUIManager.loggedInUser.getUserID());
        return openTimelineView(t, true);
    }

    @FXML
    public TimelineView editTimeline() {
        return openTimelineView(this.activeTimeline, true);
    }

    @FXML
    public TimelineView openTimeline() {
        return openTimelineView(list.getSelectionModel().getSelectedItem(), false);
    }

    private TimelineView openTimelineView(Timeline newActiveTimeline, boolean editable) {
        try {
            TimelineView timelineView = GUIManager.swapScene("TimelineView");
            timelineView.setActiveTimeline(newActiveTimeline);
            timelineView.timelineEditorController.toggleEditable(editable);
            return timelineView;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void updateDisplays() {
        list.setPrefHeight((list.getItems().size() * 86) +  322);
        if (list.getSelectionModel().getSelectedItem() != null) {   //If a timeline is selected

            //Scroll to the timeline directly above the selected one
            //The height of each cell is actually 85.9795 pixels. I know, I hate it too.
            listScrollPane.setVvalue(( 85.9795 / (list.getHeight() - listScrollPane.getHeight())) * (list.getSelectionModel().getSelectedIndex() - 1));

            if (list.getSelectionModel().getSelectedItem().getOwnerID() == GUIManager.loggedInUser.getUserID()) {
                btnDelete.setDisable(false);
                btnEdit.setDisable(false);
            } else {
                btnDelete.setDisable(true);
                btnEdit.setDisable(true);
            }
            timelineViewButton.setDisable(false);

        } else {        //If a timeline is not selected
            timelineViewButton.setDisable(true);
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
        }
    }


    //applies equivalent setups to both start and end spinners
    private void setupTimeInputStartAndEnd(String timeSpinnerLabel, int minValue, int maxValue, int column, int row, int index) {
        setupTimeInput(timeSpinnerLabel, minValue, maxValue, column, row, startInputs, startDates, index);
        setupTimeInput(timeSpinnerLabel, minValue, maxValue, column, row, endInputs, endDates, index);
    }

    //creates spinners to handle dates with appropriate min/max values and invalid input handling
    private void setupTimeInput(String timeSpinnerLabel, int minValue, int maxValue, int column, int row, List<Spinner<Integer>> spinnerList, GridPane spinnerDates, int index) {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, minValue) {
            @Override
            public void increment(int steps) {
                super.increment(steps);                         //makes blank years pretend to be 0 when using buttons, by incrementing to 1 and decrementing to -1
                if (getValue() == -999999999)
                    setValue(1);
            }

            @Override
            public void decrement(int steps) {
                super.decrement(steps);
                if (getValue() == 999999999)
                    setValue(-1);
            }
        };
        valueFactory.setConverter(new StringConverter<>() { //makes spinners revert to default values in case of invalid input
            @Override
            public String toString(Integer value) {     //called by spinner to update the displayed value in the box
                if (value == null)
                    return "";
                if (value == minValue)
                    return "";
                return value.toString();
            }

            @Override
            public Integer fromString(String string) {  //called by spinner to read the value from the box and convert to int
                try {
                    if (string == null)
                        return minValue;
                    string = string.trim();
                    if (string.length() < 1)
                        return minValue;
                    return Integer.parseInt(string);
                } catch (NumberFormatException ex) {
                    return minValue;
                }
            }
        });

        valueFactory.setWrapAround(true);
        spinnerList.add(index, new Spinner<>(valueFactory));
        spinnerList.get(index).setEditable(true);

        spinnerList.get(index).focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue)                                  //the display doesn't restore if invalid info is entered repeatedly, this fixes that
                spinnerList.get(column).cancelEdit();        //note: cancelEdit() is really more like "update display" as implemented. this triggers it upon losing focus
        });                                                 //why this isn't default behavior I'll never know

        //adds each spinner to a VBox underneath its label, to keep the two connected as they move around
        Label spinnerHeader = new Label(timeSpinnerLabel);
        spinnerHeader.getStyleClass().add("smallText");
        if (column == 2 && row == 2)
            spinnerDates.add(spinnerHeader, column, row, 2, 1);
        else
            spinnerDates.add(spinnerHeader, column, row);
        spinnerDates.add(spinnerList.get(index), column, row + 1);
    }

    // open DeletePopUp
    @FXML
    public boolean deleteConfirmation() {
        Alert confirmDeleteTimeline = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDeleteTimeline.setTitle("Confirm Deletion");
        confirmDeleteTimeline.setHeaderText("Are you sure you want to delete " + activeTimeline.getName() + "?");
        confirmDeleteTimeline.setContentText("This can not be undone.");
        
        Optional<ButtonType> result = confirmDeleteTimeline.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        else {
            try {
                activeTimeline.deleteOrphans();
                DBM.deleteFromDB(activeTimeline);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            filteredTimelines.getSource().remove(activeTimeline);
            list.getSelectionModel().select(0);
            return true;
        }
    }

    private static class RatingsListCell extends ListCell<Integer> {    //for displaying ratings in combobox
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || item == 0) {           //zero treated as blank/unused
                setText(null);
            } else {
                setText(item.toString());
            }
        }
    }

    //approach from https://stackoverflow.com/a/27439026
    private class TimelineCellListCell extends ListCell<Timeline> {         //for displaying timelines
        private Node cellNode;
        private TimelineCell cell;

        public TimelineCellListCell() {
            super();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/TimelineCell.fxml"));
            try {
                cellNode = loader.load();
                cell = loader.getController();
            } catch (IOException e) {
                System.err.println("Could not load TimelineCell.fxml");
            }
            cell.list = list;
            cell.filteredTimelines = filteredTimelines;

            this.focusedProperty().addListener((observable, oldValue, newValue) -> {
                cell.focused = newValue;
                int height = newValue ? 400 : 100;
                this.resize(1200, height);
                cell.ratingBox.setDisable(!newValue);

                if (cell.timeline != null)
                {
                    cell.setBGImage();

                    if (newValue)
                    {
                        cell.pane.add(cell.cellButtonBox, 1, 0);

                        if (cell.timeline.getOwnerID() == GUIManager.loggedInUser.getUserID()) {
                            cell.cellDeleteTimelineButton.setDisable(false);
                            cell.cellDeleteTimelineButton.setOpacity(1);
                            cell.cellEditTimelineButton.setDisable(false);
                            cell.cellEditTimelineButton.setOpacity(1);
                        } else {
                            cell.cellDeleteTimelineButton.setDisable(true);
                            cell.cellDeleteTimelineButton.setOpacity(0);
                            cell.cellEditTimelineButton.setDisable(true);
                            cell.cellEditTimelineButton.setOpacity(0);
                        }
                    }
                    else
                        cell.pane.getChildren().remove(cell.cellButtonBox);
                }
            });
        }

        @Override
        protected void updateItem(Timeline item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) //test for null item and empty parameter
            {
                setGraphic(cellNode);
                if (cell != null)
                    cell.setTimeline(item);
            } else
                setGraphic(null);
        }
    }
}
