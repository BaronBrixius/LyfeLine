package controllers;

import database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;
import utils.Date;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Dashboard {
    final List<Spinner<Integer>> startInputs = new ArrayList<>();
    final List<Spinner<Integer>> endInputs = new ArrayList<>();
    public Timeline timeline;
    public Label KeywordLabel;
    public Label RatingLabel;
    public StackPane stack;
    @FXML protected Button timelineViewButton;
    //@FXML protected Button eventEditorButton;
    @FXML protected Button adminGUI;
    @FXML protected Button btnDelete;
    @FXML protected Button btnEdit;
    @FXML protected Button btnCreate;
    @FXML protected Button searchButton;
    @FXML protected TextFlow displayInfo;
    @FXML protected ListView<Timeline> list;
    @FXML protected TextField searchInput;
    @FXML protected TextField searchTimelineName;
    @FXML protected TextField searchCreator;
    @FXML protected TextField searchKeywords;
    @FXML protected ComboBox searchRating;
    @FXML protected Button clearButton;
    @FXML protected CheckBox cbOnlyViewPersonalLines;
    @FXML protected ComboBox<String> sortBy;
    @FXML protected GridPane gridButtons;
    @FXML protected GridPane advancedSearchView;
    @FXML protected GridPane startDates;
    @FXML protected GridPane endDates;
    @FXML protected GridPane topLabels;
    @FXML protected GridPane bottomLabels;
    @FXML protected Text titleText;
    @FXML protected Hyperlink AdvancedSearch;
    @FXML protected Hyperlink toggleHHMMSS;
    private List<Timeline> timelines;
    private List<Timeline> userTimelines;
    private Timeline activeTimeline;

    public void initialize() {
        //Set Up the Spinners for Start/End Inputs, would have bloated the .fxml and variable list a ton if these were in fxml
        setupTimeInputStartAndEnd("Year", Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0, 0);
        setupTimeInputStartAndEnd("Month", 0, 12, 1, 0, 1);
        setupTimeInputStartAndEnd("Day", 0, 31, 2, 0, 2);
        setupTimeInputStartAndEnd("Hour", -1, 23, 3, 0, 3);
        setupTimeInputStartAndEnd("Minute", -1, 59, 0, 2, 4);
        setupTimeInputStartAndEnd("Second", -1, 59, 1, 2, 5);
        setupTimeInputStartAndEnd("Millisecond", -1, 999, 2, 2, 6);
        // TODO fix this to be cleaner, I did it as a last second thing because it used
        // to prevent nonadmins from even viewing anything
        //
        stack.getChildren().remove(advancedSearchView);
        btnCreate.setVisible(GUIManager.loggedInUser.getAdmin());
        btnCreate.setDisable(!GUIManager.loggedInUser.getAdmin());
        btnEdit.setVisible(GUIManager.loggedInUser.getAdmin());
        btnEdit.setDisable(list.getSelectionModel().isEmpty()
                || list.getSelectionModel().getSelectedItem().getOwnerID() != GUIManager.loggedInUser.getUserID());
        btnDelete.setVisible(GUIManager.loggedInUser.getAdmin());
        btnDelete.setDisable(list.getSelectionModel().isEmpty()
                || list.getSelectionModel().getSelectedItem().getOwnerID() != GUIManager.loggedInUser.getUserID());
        adminGUI.setVisible(GUIManager.loggedInUser.getAdmin());
        adminGUI.setDisable(!GUIManager.loggedInUser.getAdmin());
        timelineViewButton.setDisable(list.getSelectionModel().getSelectedItem() == null);

        // Fill ListView with the timelines
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
            timelines = DBM.getFromDB(stmt, new Timeline());
            list.setItems(FXCollections.observableArrayList(timelines));
        } catch (SQLException e) {
            System.err.println("Could not get timelines from database.");
        }

        // approach adapted from https://stackoverflow.com/a/36657553
        list.setCellFactory(param -> new ListCell<>() {
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

        // login.setOnAction(e -> browse.setVisible(true));

        // Add sorting options
        ObservableList<String> sortOptions = FXCollections.observableArrayList();
        sortOptions.add("Alphabetically");
        sortOptions.add("Reverse-Alphabetically");
        sortOptions.add("Most Recent");
        sortOptions.add("Oldest");
        sortBy.setItems(sortOptions);

        // Sort order selection events
        sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> sortTimelines());

        // Initialised sorting
        sortBy.getSelectionModel().select(0);

        // Search field
        searchInput.focusedProperty().addListener(ov -> searchTimelines());

        list.getSelectionModel().selectedIndexProperty().addListener(e -> {
            activeTimeline = list.getSelectionModel().getSelectedItem();
            updateDisplays();
        });

        titleText.setText("Select a Timeline.");

    }

    @FXML
    public void searchTimelines() {
        searchInput.setOnKeyReleased(keyEvent -> {// Each time new key is pressed
            String[] inputs = searchInput.getText().trim().split("\\s++"); // String is updated by the newest textfield
            // read, if spaces the strings are split up
            // into "string keywords" for search l
            List<Timeline> templist = new ArrayList<>(); // List of timelines that fulfill the textfield input string -
            // used to fill the ListView of timelines

            //only the logged in user timelines
            if (cbOnlyViewPersonalLines.isSelected()) {
                onlyUserTimelines(); // If only search user's timelines
                for (int i = 0; i < userTimelines.size(); i++) { // go trough all the current user's timelines in the
                    // database
                    for (int j = 0; j < inputs.length; j++) {// No check all the search words used if they are to be
                        // found anywhere as keywords
                        String toFind = inputs[j]; // while a keyword is just one letter i.e. "f" if a keyword in
                        // timeline has that letter then it will be shown (instant search
                        // feature)
                        List<String> allThisTimelineKeywords = timelines.get(i).getKeywords();
                        List<String> possibleKeywords = new ArrayList<>();
                        String[] timelineNames = timelines.get(i).getName().trim().split("\\s++");
                        for (int k = 0; k < allThisTimelineKeywords.size(); k++) {
                            if (allThisTimelineKeywords.get(k).length() >= toFind.length()) {
                                possibleKeywords.add(allThisTimelineKeywords.get(k));
                            }
                        }
                        boolean keyWordfound = Arrays.asList(possibleKeywords.toArray()).stream().anyMatch(s -> s.toString().substring(0, toFind.length()).equalsIgnoreCase(toFind));
                        boolean namefound = Arrays.asList(timelineNames).stream().anyMatch(s -> s.toLowerCase().equalsIgnoreCase(toFind));

                        if (keyWordfound || namefound) {
                            if (!templist.contains(userTimelines.get(i))) // if the timeline has not already been
                                // associated with this search then add it// to the temporary timelinelist
                                templist.add(userTimelines.get(i));
                        }
                    }
                    list.setItems(FXCollections.observableArrayList(templist)); // populate the ListView with the
                    // timelines that fulfill the search
                    // criteria at given point in
                    // time(instant)
                    if (searchInput.getText().equalsIgnoreCase("")) // When everything is erased from search box, return
                        // all the user's timelines back to the ListView
                        list.setItems(FXCollections.observableArrayList(userTimelines));
                }
            }
            // Search all timelines
            else {
                for (int i = 0; i < timelines.size(); i++) { // go trough all the current timelines in the database
                    for (int j = 0; j < inputs.length; j++) {// No check all the search words used if they are to be
                        // found anywhere as keywords
                        String toFind = inputs[j]; // while a keyword is just one letter i.e. "f" if a keyword in
                        // timeline has that letter then it will be shown (instant search
                        // feature)
                        List<String> allThisTimelineKeywords = timelines.get(i).getKeywords();
                        List<String> possibleKeywords = new ArrayList<>();
                        String[] timelineNames = timelines.get(i).getName().trim().split("\\s++");
                        for (int k = 0; k < allThisTimelineKeywords.size(); k++) {
                            if (allThisTimelineKeywords.get(k).length() >= toFind.length()) {
                                possibleKeywords.add(allThisTimelineKeywords.get(k));
                            }
                        }


                        boolean keyWordfound = Arrays.asList(possibleKeywords.toArray()).stream().anyMatch(s -> s.toString().substring(0, toFind.length()).equalsIgnoreCase(toFind));

                        boolean namefound = Arrays.asList(timelineNames).stream().anyMatch(s -> s.equalsIgnoreCase(toFind));

                        if (keyWordfound || namefound) {
                            if (!templist.contains(timelines.get(i))) // if the timeline has not already been associated
                                // with this search then add it to the temporary
                                // timelinelist
                                templist.add(timelines.get(i));
                        }
                    }
                    list.setItems(FXCollections.observableArrayList(templist)); // populate the ListView with the
                    // timelines that fulfill the search
                    // criteria at given point in
                    // time(instant)
                    if (searchInput.getText().equalsIgnoreCase("")) // When everything is erased from search box, return
                        // all the timelines back to the ListView
                        list.setItems(FXCollections.observableArrayList(timelines));
                }
            }
        });
    }

    @FXML
    public void toggleAdvancedSearch() {
        if (stack.getChildren().size() > 0)
            stack.getChildren().remove(advancedSearchView);
        else {
            clearAdvancedSearch();
            stack.getChildren().add(advancedSearchView);
        }
    }

    @FXML
    public void clearAdvancedSearch() {
        searchTimelineName.clear();
        searchCreator.clear();
        searchKeywords.clear();
        if (cbOnlyViewPersonalLines.isSelected()) {
            onlyUserTimelines();
        } else
            this.list.setItems(FXCollections.observableArrayList(timelines));
    }

    @FXML
    public void onlyUserTimelines() {

        if (cbOnlyViewPersonalLines.isSelected()) {
            try {
                PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineOwner = ?");
                stmt.setInt(1, GUIManager.loggedInUser.getUserID()); // GUIManager.loggedInUser.getUserID() uncomment
                // this for real version
                this.userTimelines = DBM.getFromDB(stmt, new Timeline());
                list.setItems(FXCollections.observableArrayList(userTimelines));
            } catch (SQLException e) {
                System.err.println("Could not get timelines from database.");
            }
        } else {
            try {
                PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
                list.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
                sortTimelines();
            } catch (SQLException e) {
                System.err.println("Could not get timelines from database.");
            }
        }

    }


    public void sortTimelines() {
        switch (sortBy.getSelectionModel().getSelectedIndex()) {
            case 0:
                list.getItems().sort((t1, t2) -> (t1.getName().compareToIgnoreCase(t2.getName())));
                break;
            case 1:
                list.getItems().sort((t1, t2) -> (t2.getName().compareToIgnoreCase(t1.getName())));
                break;
            case 2:
                list.getItems().sort((t1, t2) -> (t2.getCreationDate().compareTo(t1.getCreationDate())));
                break;
            case 3:
                list.getItems().sort(Comparator.comparing(Timeline::getCreationDate));
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
        try {
            TimelineView timelineView = GUIManager.swapScene("TimelineView");
            timelineView.setActiveTimeline(t);
            timelineView.timelineEditorController.toggleEditable(true);
            return timelineView;
        } catch (IOException e) {e.printStackTrace(); return null;}

    }

    @FXML
    public TimelineView editTimeline() {
        return(openTimelineView(this.activeTimeline));
    }

    @FXML
    public TimelineView openTimeline() {
        return openTimelineView(list.getSelectionModel().getSelectedItem());
    }

    private TimelineView openTimelineView(Timeline newActiveTimeline) {
        try {
            TimelineView timelineView = GUIManager.swapScene("TimelineView");
            timelineView.setActiveTimeline(newActiveTimeline);
            return timelineView;
        } catch (IOException e) {e.printStackTrace(); return null;}
    }

    @FXML
    private void updateDisplays() {
        if (list.getSelectionModel().getSelectedItem() != null) {
            if (list.getSelectionModel().getSelectedItem().getOwnerID() == GUIManager.loggedInUser.getUserID()) {
                btnDelete.setDisable(false);
                btnEdit.setDisable(false);
            } else {
                btnDelete.setDisable(true);
                btnEdit.setDisable(true);
            }
            timelineViewButton.setDisable(false);

            Timeline timeline = list.getSelectionModel().getSelectedItem();

            int year = timeline.getCreationDate().getYear();
            int month = timeline.getCreationDate().getMonth();
            int day = timeline.getCreationDate().getDay();

            StringBuilder keyWords = new StringBuilder();
            for (String s : timeline.getKeywords())
                keyWords.append(s + ", ");
            keyWords.delete(keyWords.length() - 2, keyWords.length());

            titleText.setText("Title: " + timeline.getName() + "\nDescription: " + timeline.getDescription()
                    + "\nDate Created: " + year + "/" + month + "/" + day + "\nKeywords: " + keyWords);

        } else {
            timelineViewButton.setDisable(true);
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
            titleText.setText("Select a Timeline.");
        }
    }

    public void advancedSearch() throws SQLException {
        boolean startDate= true;
        boolean endDate= true;
        Date startDateSpinner = new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue());
        System.out.println(startDateSpinner.toString());
        if(startDateSpinner.getYear() == -2147483647)
            startDateSpinner.setYear(0);
        if(startDateSpinner.getMonth() == 1)
            startDateSpinner.setMonth(0);
        if(startDateSpinner.getDay() == 1)
            startDateSpinner.setDay(0);
        System.out.println(startDateSpinner.toString());
        if (startDateSpinner.toString().equals("0-000000"))
            startDate = false;


        Date endDateSpinner = new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue());
        System.out.println(endDateSpinner.toString());

        if(endDateSpinner.getYear() == -2147483647)
            endDateSpinner.setYear(0);
        if(endDateSpinner.getMonth() == 1)
            endDateSpinner.setMonth(0);
        if(endDateSpinner.getDay() == 1)
            endDateSpinner.setDay(0);
        if (endDateSpinner.toString().equals("0-000000"))
            endDate = false;
        System.out.println(endDateSpinner.toString());

        List<Timeline> tempAllList;
        List<Timeline> rightTimelines =  new ArrayList<>(); //Currently the right list unless we need to update it with spinner search
        //If only searching with Range and nothing else
        //If searching with Range amongst else
        if ((startDate || endDate)) {
            PreparedStatement out = DBM.conn.prepareStatement("SELECT * FROM timelines");
            tempAllList = DBM.getFromDB(out, new Timeline());
            //If range is defined in both ends
            if (startDate  & endDate) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for (int i = 0; i < tempAllList.size(); i++) {
                    if (tempAllList.get(i).getStartDate().compareTo(start) != -1 & tempAllList.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(tempAllList.get(i));
                }

            }
            //If range is defined in start
            else if (startDate) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for (int i = 0; i < tempAllList.size(); i++) {
                    if (tempAllList.get(i).getStartDate().compareTo(start) != -1)
                        rightTimelines.add(tempAllList.get(i));
                }
            }
            //If range is defined in end
            else {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for (int i = 0; i < tempAllList.size(); i++) {
                    if (tempAllList.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(tempAllList.get(i));
                }
            }
        }


        String[] keywords = null;
        StringBuilder dynamicParameter = new StringBuilder();
        if (searchKeywords.getText() != null) {
            keywords = searchKeywords.getText().trim().split(" ");

            for (int i = 1; i < keywords.length; i++) {
                System.out.println(keywords[i]);
                dynamicParameter.append("OR  CONCAT(',', `Keywords`, ',') LIKE CONCAT('%,', COALESCE(?, '%'), ',%')");
            }
        }

        PreparedStatement stmt3 = DBM.conn.prepareStatement("SELECT * FROM `timelines` LEFT JOIN `users` ON users.UserID = timelines.TimelineOwner WHERE " +
                " CONCAT(' ', `TimelineName`, ' ') LIKE CONCAT('% ', COALESCE(?, '%'), ' %') AND `UserName` = COALESCE(NULLIF(?, ''), `UserName`) AND `Rating` = COALESCE(NULLIF(?, ''), `Rating`)  AND (CONCAT(',', `Keywords`, ',') LIKE CONCAT('%,', COALESCE(?, '%'), ',%') " + dynamicParameter + ")  ;");
        if (searchTimelineName.getText().isEmpty())
            stmt3.setString(1, "%");
        else
            stmt3.setString(1, searchTimelineName.getText());
        stmt3.setString(2, searchCreator.getText());
        stmt3.setInt(3, 0); //For now untill the Rating combobox provides something
        if (keywords != null)
            for (int i = 4; i < keywords.length + 4; i++) {
                stmt3.setString(i, keywords[i - 4]);
                System.out.println(keywords[i - 4]);
            }
        else
            stmt3.setString(4, searchKeywords.getText());


        //EXAMPLE OF RETURNING THE TIMELINES THAT FULFILL THE SEARCH AS TIMELINE OBJECT
        System.out.println();
        System.out.println("======SEARCH RESULTS as objects - THE TIMELINES NAMES==========");
        System.out.println(stmt3);
        List<Timeline> list = DBM.getFromDB(stmt3, new Timeline());
        System.out.println(startInputs.toString() + "what the startINput says");

        if(!startDate & !endDate){
            rightTimelines=list;
            System.out.println("Spinner not used print result from SQL ");}

         else if(rightTimelines.isEmpty())
            System.out.println("Spinner used but no match so ignore sql result");

         else if(!rightTimelines.isEmpty() & list.isEmpty()) {
             rightTimelines = new ArrayList<>();
             System.out.println("Spinner used but AND match but sql no match");
         }

        else if(!rightTimelines.isEmpty() & !list.isEmpty()) {
            List<Timeline> temp = new ArrayList<>();
            for(int i= 0; i<rightTimelines.size(); i++){
                for(int j= 0; j<list.size(); j++){
                   if (rightTimelines.get(i).getID() == list.get(j).getID())
                       temp.add(rightTimelines.get(i));
                }
            }
            rightTimelines=temp;
            System.out.println("have matches");
        }

        //for (int i = 0; i < rightTimelines.size(); i++)
            //System.out.println(list.get(i).getName());

        if (cbOnlyViewPersonalLines.isSelected()) {

            List<Timeline> userline = new ArrayList<>();
            for (int i = 0; i < rightTimelines.size(); i++) {
                for (int j = 0; j < userTimelines.size(); j++) {
                    if (userTimelines.get(j).getID() == rightTimelines.get(i).getID())
                        userline.add(rightTimelines.get(i));
                }
            }
            this.list.setItems(FXCollections.observableArrayList(userline));
        } else
            this.list.setItems(FXCollections.observableArrayList(rightTimelines));
    }

    private void setupTimeInputStartAndEnd(String timeSpinnerLabel, int minValue, int maxValue, int column, int row, int index) {    //applies equivalent setups to both start and end spinners
        setupTimeInput(timeSpinnerLabel, minValue, maxValue, column, row, startInputs, startDates, index);
        setupTimeInput(timeSpinnerLabel, minValue, maxValue, column, row, endInputs, endDates, index);
    }

    //creates spinners to handle dates with appropriate min/max values and invalid input handling
    private void setupTimeInput(String timeSpinnerLabel, int minValue, int maxValue, int column, int row, List<Spinner<Integer>> spinnerList, GridPane spinnerDates, int index) {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, minValue) {
            @Override
            public void increment(int steps) {
                super.increment(steps);                         //makes blank years pretend to be 0 when using buttons, by incrementing to 1 and decrementing to -1
                if (getValue() == Integer.MIN_VALUE + 1)
                    setValue(1);
            }

            @Override
            public void decrement(int steps) {
                super.decrement(steps);
                if (getValue() == Integer.MAX_VALUE)
                    setValue(-1);
            }
        };
        valueFactory.setConverter(new StringConverter<>() {                 //makes spinners revert to default values in case of invalid input
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
    public boolean deleteConfirmation() throws IOException {

        Alert confirmDeleteTimeline = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDeleteTimeline.setTitle("Confirm Deletion");
        confirmDeleteTimeline.setHeaderText("Are you sure you want to delete " + list.getSelectionModel().getSelectedItem().getName() + "?");
        confirmDeleteTimeline.setContentText("This can not be undone.");

        Optional<ButtonType> result = confirmDeleteTimeline.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        else
        {
            try {deleteOrphans(list.getSelectionModel().getSelectedItem());
                DBM.deleteFromDB(list.getSelectionModel().getSelectedItem());
            } catch (SQLException e) {e.printStackTrace();}

            list.getItems().remove(list.getSelectionModel().getSelectedIndex());
            list.getSelectionModel().select(null);
            return true;
        }
    }

    public void deleteOrphans(Timeline timeline) throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("SELECT e.* FROM `timelines` t " +
                "LEFT JOIN timelineevents te " +
                "ON t.TimelineID = te.TimelineID " +            //destroys orphaned events (i.e. events where there are no
                "LEFT JOIN events e " +                            //junction table records for them with a different TimelineID
                "ON te.EventID = e.EventID AND e.EventID NOT IN (SELECT EventID FROM timelineevents WHERE TimelineID != ?) " +
                "WHERE t.TimelineID = ? ");

        out.setInt(1, timeline.getID());
        out.setInt(2, timeline.getID());

        DBM.deleteFromDB(DBM.getFromDB(out, new Event()));
    }
}
