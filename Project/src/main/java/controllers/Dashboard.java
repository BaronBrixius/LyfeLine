package controllers;

import database.DBM;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import utils.Date;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Dashboard {
    final List<Spinner<Integer>> startInputs = new ArrayList<>();
    final List<Spinner<Integer>> endInputs = new ArrayList<>();
    public Timeline timeline;
    @FXML
    protected Button eventEditorButton;
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
    protected TextFlow displayInfo;
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
    protected ComboBox searchRating;
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
    protected GridPane topLabels;
    @FXML
    protected GridPane bottomLabels;

    @FXML
    protected Text titleText;
    @FXML
    protected Hyperlink AdvancedSearch;
    @FXML
    protected Hyperlink toggleHHMMSS;

    private List<Timeline> timelines;
    private List<Timeline> userTimelines;
    private Timeline activeTimeline;

    public void initialize() {
        //Set Up the Spinners for Start/End Inputs, would have bloated the .fxml and variable list a ton if these were in fxml
        setupTimeInputStartAndEnd("Year", Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        setupTimeInputStartAndEnd("Month", 0, 12, 1);
        setupTimeInputStartAndEnd("Day", 0, 31, 2);
        setupTimeInputStartAndEnd("Hour", -1, 23, 3);
        setupTimeInputStartAndEnd("Minute", -1, 59, 4);
        setupTimeInputStartAndEnd("Second", -1, 59, 5);
        setupTimeInputStartAndEnd("Millisecond", -1, 999, 6);
        // TODO fix this to be cleaner, I did it as a last second thing because it used
        // to prevent nonadmins from even viewing anything
        //
        btnCreate.setVisible(GUIManager.loggedInUser.getAdmin());
        btnCreate.setDisable(!GUIManager.loggedInUser.getAdmin());
        btnEdit.setVisible(GUIManager.loggedInUser.getAdmin());
        btnEdit.setDisable(!GUIManager.loggedInUser.getAdmin());
        btnDelete.setVisible(GUIManager.loggedInUser.getAdmin());
        btnDelete.setDisable(!GUIManager.loggedInUser.getAdmin());
        adminGUI.setVisible(GUIManager.loggedInUser.getAdmin());
        adminGUI.setDisable(!GUIManager.loggedInUser.getAdmin());

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
        searchInput.focusedProperty().addListener(ov -> {
            searchTimelines();

        });

        list.getSelectionModel().selectedIndexProperty().addListener(e -> {
            activeTimeline = list.getSelectionModel().getSelectedItem();
            updateDisplays();
        });

        titleText.setText("Select a Timeline.");

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

			}
			// If range is defined in start
			else if (startDateSpinner != null) {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getStartDate().compareTo(start) != -1)
						rightTimelines.add(list.get(i));
				}
			}
			// If range is defined in end
			else {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getEndDate().compareTo(end) != 1)
						rightTimelines.add(list.get(i));
				}
			}
		}

		for (int i = 0; i < rightTimelines.size(); i++)
			System.out.println(list.get(i).getName());

		// If searching with Range amongst else
		if (!list.isEmpty() & (startDateSpinner != null || endDateSpinner != null)) {
			PreparedStatement out = DBM.conn.prepareStatement("SELECT * FROM timelines");
			rightTimelines = new ArrayList<>();
			// If range is defined in both ends
			if (startDateSpinner != null & endDateSpinner != null) {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getStartDate().compareTo(start) != -1
							|| list.get(i).getEndDate().compareTo(end) != 1)
						rightTimelines.add(list.get(i));
				}

			}
			// If range is defined in start
			else if (startDateSpinner != null) {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getStartDate().compareTo(start) != -1)
						rightTimelines.add(list.get(i));
				}
			}
			// If range is defined in end
			else {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getEndDate().compareTo(end) != 1)
						rightTimelines.add(list.get(i));
				}
			}
		}
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

    @FXML
    public void adminScreen() throws IOException {
        GUIManager.swapScene("AdminRoleManager");
    }

    @FXML
    public TimelineView createTimeline() {
        Timeline t = new Timeline();
        t.setOwnerID(GUIManager.loggedInUser.getUserID());
        openTimelineView(t);
        return null;
    }

    @FXML
    public TimelineView editTimeline() {
        if (activeTimeline != null) {
            openTimelineView(this.activeTimeline);
        }
        return null;
    }

    @FXML
    public void openTimeline() {
        openTimelineView(list.getSelectionModel().getSelectedItem());
    }

    private void openTimelineView(Timeline newActiveTimeline) {
        try {
            TimelineView timelineView = GUIManager.swapScene("TimelineView");
            timelineView.setActiveTimeline(newActiveTimeline);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // open DeletePopUp
    @FXML
    public void deleteConfirmation(ActionEvent event) throws IOException {

        Stage delConfirm = new Stage();
        delConfirm.setTitle("Confirm Deletion");
        delConfirm.initOwner(GUIManager.mainStage);

        delConfirm.initModality(Modality.WINDOW_MODAL);
        delConfirm.setResizable(false);

        FXMLLoader popupDeletion = new FXMLLoader(GUIManager.class.getResource("../FXML/Popup.fxml"));
        VBox popup = popupDeletion.load();
        popup.getStylesheets().add(GUIManager.mainStage.getScene().getStylesheets().get(0));
        delConfirm.setScene(new Scene(popup));

        Popup deletionPopup = popupDeletion.getController();
        deletionPopup.setMode(1);
        if (list.getSelectionModel().getSelectedItem() != null
                && list.getSelectionModel().getSelectedItem().getOwnerID() == GUIManager.loggedInUser.getUserID()) {
            titleText.setText("");
            deletionPopup.setList(list);
            deletionPopup.setDisplayTxt(
                    "Are you sure you want to delete " + list.getSelectionModel().getSelectedItem().getName() + "?");
            delConfirm.show();

        }
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
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
            titleText.setText("Select a Timeline.");
        }
    }

    public void advancedSearch() throws SQLException {

        Date startDateSpinner = new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue());
        if (startDateSpinner.compareTo(new Date()) == 0)
            startDateSpinner = null;


        Date endDateSpinner = new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue());
        if (endDateSpinner.compareTo(new Date()) == 0)
            endDateSpinner = null;


        String[] keywords = null;
        StringBuilder dynamicParameter = new StringBuilder();
        if (searchKeywords.getText() != null) {
            keywords = searchKeywords.getText().split(" ");

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
        List<Timeline> tempAllList;
        List<Timeline> rightTimelines = list; //Currently the right list unless we need to update it with spinner search
        //If only searching with Range and nothing else
        if (list.isEmpty() & (startDateSpinner != null || endDateSpinner != null)) {
            rightTimelines = new ArrayList<>();
            PreparedStatement out = DBM.conn.prepareStatement("SELECT * FROM timelines");
            tempAllList = DBM.getFromDB(out, new Timeline());
            //If range is defined in both ends
            if (startDateSpinner != null & endDateSpinner != null) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for (int i = 0; i < tempAllList.size(); i++) {
                    if (tempAllList.get(i).getStartDate().compareTo(start) != -1 || tempAllList.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(tempAllList.get(i));
                }

            }
            //If range is defined in start
            else if (startDateSpinner != null) {
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

        //If searching with Range amongst else
        if (!list.isEmpty() & (startDateSpinner != null || endDateSpinner != null)) {
            PreparedStatement out = DBM.conn.prepareStatement("SELECT * FROM timelines");
            rightTimelines = new ArrayList<>();
            //If range is defined in both ends
            if (startDateSpinner != null & endDateSpinner != null) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getStartDate().compareTo(start) != -1 || list.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(list.get(i));
                }

            }
            //If range is defined in start
            else if (startDateSpinner != null) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getStartDate().compareTo(start) != -1)
                        rightTimelines.add(list.get(i));
                }
            }
            //If range is defined in end
            else {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(list.get(i));
                }
            }
        }

        for (int i = 0; i < rightTimelines.size(); i++)
            System.out.println(list.get(i).getName());

        // If searching with Range amongst else
        if (!list.isEmpty() & (startDateSpinner != null || endDateSpinner != null)) {
            PreparedStatement out = DBM.conn.prepareStatement("SELECT * FROM timelines");
            rightTimelines = new ArrayList<>();
            // If range is defined in both ends
            if (startDateSpinner != null & endDateSpinner != null) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getStartDate().compareTo(start) != -1
                            || list.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(list.get(i));
                }

            }
            // If range is defined in start
            else if (startDateSpinner != null) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getStartDate().compareTo(start) != -1)
                        rightTimelines.add(list.get(i));
                }
            }
            // If range is defined in end
            else {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(list.get(i));
                }
            }
        }
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

    private void setupTimeInputStartAndEnd(String timeSpinnerLabel, int minValue, int maxValue, int index) {    //applies equivalent setups to both start and end spinners
        setupTimeInput(timeSpinnerLabel, minValue, maxValue, index, startInputs, startDates);
        setupTimeInput(timeSpinnerLabel, minValue, maxValue, index, endInputs, endDates);
    }

    //creates spinners to handle dates with appropriate min/max values and invalid input handling
    private void setupTimeInput(String timeSpinnerLabel, int minValue, int maxValue, int index, List<Spinner<Integer>> spinnerList, GridPane spinnerDates) {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, minValue){
            @Override public void increment(int steps) {
                super.increment(steps);                         //makes blank years pretend to be 0 when using buttons, by incrementing to 1 and decrementing to -1
                if (getValue() == Integer.MIN_VALUE + 1)
                    setValue(1);
            }
            @Override public void decrement(int steps) {
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
                spinnerList.get(index).cancelEdit();        //note: cancelEdit() is really more like "update display" as implemented. this triggers it upon losing focus
        });                                                 //why this isn't default behavior I'll never know

        //adds each spinner to a VBox underneath its label, to keep the two connected as they move around
        Label spinnerHeader = new Label(timeSpinnerLabel);
        spinnerHeader.getStyleClass().add("smallText");
        spinnerDates.add(spinnerHeader, index, 0);
        spinnerDates.add(spinnerList.get(index), index, 1);
        spinnerList.get(index).setPrefWidth(70);
    }
}
