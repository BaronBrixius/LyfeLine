package controllers;

import database.DBM;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import utils.Date;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TimelineEditor {
    private final List<VBox> startBoxes = new ArrayList<>();
    private final List<Spinner<Integer>> startInputs = new ArrayList<>();
    private final List<VBox> endBoxes = new ArrayList<>();
    private final List<Spinner<Integer>> endInputs = new ArrayList<>();
    @FXML
    public GridPane editor;
    @FXML
    public Button editButton;
    @FXML
    public Button moreStart;
    @FXML
    public Button moreEnd;
    @FXML
    public Button deleteButton;
    @FXML
    public Label headerText;
    @FXML
    public Text errorMessage;
    @FXML
    public FlowPane startPane;
    @FXML
    public FlowPane endPane;
    public Button removeButton;
    public Button addKeyWord;
    public ListView<String> listView;
    public Text feedbackText;
    public HBox keyWordBox;
    public Timeline timeline;
    @FXML
    TextField titleInput = new TextField();
    @FXML
    TextArea descriptionInput = new TextArea();
    boolean editable = true;
    TimelineView parentController;
    @FXML
    private ComboBox<String> timeInput;
    private boolean startExpanded;
    private boolean endExpanded;
    @FXML
    private TextField keywordInput;
    private ObservableList<String> keywords = FXCollections.observableArrayList();

    public void initialize() {

        //Set Up the Spinners for Start/End Inputs, would have bloated the .fxml and variable list a ton if these were in fxml
        String timeSpinnerLabel = null;
        int maxValue = 0;
        for (int i = 0; i < 7; i++) {
            switch (i) {                //labels
                case 0:
                    timeSpinnerLabel = "Year";
                    break;
                case 1:
                    timeSpinnerLabel = "Month";
                    break;
                case 2:
                    timeSpinnerLabel = "Day";
                    break;
                case 3:
                    timeSpinnerLabel = "Hour";
                    break;
                case 4:
                    timeSpinnerLabel = "Minute";
                    break;
                case 5:
                    timeSpinnerLabel = "Second";
                    break;
                case 6:
                    timeSpinnerLabel = "Millisecond";
                    break;
            }

            switch (i) {            //max values
                case 1:
                    maxValue = 12;
                    break;
                case 2:
                    maxValue = 31;
                    break;
                case 3:
                    maxValue = 23;
                    break;
                case 4:
                case 5:
                    maxValue = 59;
                    break;
                case 6:
                    maxValue = 999;
                    break;
            }

            setupTimeInputBoxes(timeSpinnerLabel, maxValue, i, startInputs, startBoxes);
            setupTimeInputBoxes(timeSpinnerLabel, maxValue, i, endInputs, endBoxes);

            //Get list of scales
            try {
                PreparedStatement state = DBM.conn.prepareStatement("SELECT unit FROM scale_lookup");
                timeInput.setItems(FXCollections.observableArrayList(DBM.getFromDB(state, rs -> rs.getString("unit"))));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //fix ranges for years since they're a little different
        startInputs.get(0).setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
        endInputs.get(0).setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
        toggleEditable(false);
        listView.setItems(keywords);
    }

    private void setupTimeInputBoxes(String timeSpinnerLabel, int maxValue, int i, List<Spinner<Integer>> spinnerList, List<VBox> boxList) {
        //startTimes.add(i, new Spinner<>(0, maxValue, 0));
        //startBoxes.add(i, new VBox(new Label(timeSpinnerLabel), startTimes.get(i)));
        //startBoxes.get(i).setPrefWidth(70);
        //startBoxes.get(i).getChildren().get(0).getStyleClass().add("smallText");
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxValue, 0);
        valueFactory.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                if (value == null)
                    return "0";
                return value.toString();
            }

            @Override
            public Integer fromString(String string) {
                try {
                    // If the specified value is null or zero-length, return null
                    if (string == null)
                        return 0;
                    string = string.trim();
                    if (string.length() < 1)
                        return null;
                    return Integer.parseInt(string);

                } catch (NumberFormatException ex) {
                    return 0;
                }
            }
        });
        spinnerList.add(i, new Spinner<>(valueFactory));
        spinnerList.get(i).setEditable(true);

        boxList.add(i, new VBox(new Label(timeSpinnerLabel), spinnerList.get(i)));
        boxList.get(i).setPrefWidth(70);
        boxList.get(i).getChildren().get(0).getStyleClass().add("smallText");
    }

    public void setParentController(TimelineView parentController) {             //TODO delete this inelegant solution
        this.parentController = parentController;
    }

    public boolean setTimeline(Timeline timeline) {
        this.timeline = timeline;
        //Check if Admin
        if (GUIManager.loggedInUser.getUserID() != timeline.getTimelineOwnerID()) {
            editButton.setVisible(false);
            editButton.setDisable(true);
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
        }
        return populateDisplay();
    }

    private boolean populateDisplay() {
        titleInput.setText(timeline.getTimelineName());
        descriptionInput.setText(timeline.getTimelineDescription());

        if (timeline.getStartDate() != null) {
            startInputs.get(0).getValueFactory().setValue(timeline.getStartDate().getYear());
            startInputs.get(1).getValueFactory().setValue(timeline.getStartDate().getMonth());
            startInputs.get(2).getValueFactory().setValue(timeline.getStartDate().getDay());
            startInputs.get(3).getValueFactory().setValue(timeline.getStartDate().getHour());
            startInputs.get(4).getValueFactory().setValue(timeline.getStartDate().getMinute());
            startInputs.get(5).getValueFactory().setValue(timeline.getStartDate().getSecond());
            startInputs.get(6).getValueFactory().setValue(timeline.getStartDate().getMillisecond());

            endInputs.get(0).getValueFactory().setValue(timeline.getEndDate().getYear());
            endInputs.get(1).getValueFactory().setValue(timeline.getEndDate().getMonth());
            endInputs.get(2).getValueFactory().setValue(timeline.getEndDate().getDay());
            endInputs.get(3).getValueFactory().setValue(timeline.getEndDate().getHour());
            endInputs.get(4).getValueFactory().setValue(timeline.getEndDate().getMinute());
            endInputs.get(5).getValueFactory().setValue(timeline.getEndDate().getSecond());
            endInputs.get(6).getValueFactory().setValue(timeline.getEndDate().getMillisecond());
        }

        setExpansion(true, false);
        setExpansion(false, false);

        if (timeline.getKeywords() != null) {
            keywords.clear();
            keywords.addAll(timeline.getKeywords());
            keywords.sort(String::compareTo);
        } else
            timeline.setKeywords(FXCollections.observableArrayList());

        timeInput.getSelectionModel().select(timeline.getScale() - 1);

        return false;
    }

    void updateTimeline() {
        //setters to update each field of this.event, based on the current info in the text fields
        timeline.setTimelineName(titleInput.getText());
        timeline.setTimelineDescription(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n"));

        timeline.setStartDate(new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue()));

        timeline.setEndDate(new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue()));

        timeline.getKeywords().clear();
        timeline.getKeywords().addAll(keywords);

        timeline.setScale((timeInput.getSelectionModel().getSelectedIndex()) + 1);
    }

    @FXML
    private boolean deleteTimeline() {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("This will delete your timeline permanently!");
        confirmDelete.setContentText("Are you ok with this?");

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;

        try {
            DBM.deleteFromDB(timeline);
            GUIManager.swapScene("Dashboard");
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void toggleStartExpanded() {
        startExpanded = !startExpanded;
        setExpansion(true, startExpanded);
        moreStart.setText(startExpanded ? "Less..." : "More...");
    }

    public void toggleEndExpanded() {
        endExpanded = !endExpanded;
        setExpansion(false, endExpanded);
        moreEnd.setText(endExpanded ? "Less..." : "More...");
    }

    private int setExpansion(boolean start, boolean expanding) {
        FlowPane expandPane = start ? startPane : endPane;
        List<VBox> boxesToAddFrom = start ? startBoxes : endBoxes;
        expandPane.getChildren().removeAll(boxesToAddFrom);         //clear out the current contents except the expansion button
        int scale = parentController.activeTimeline.getScale();     //TODO change to this event's scale

        if (expanding) {                //if expanding, add everything in
            expandPane.getChildren().addAll(0, boxesToAddFrom);

        } else {                        //if contracting, add based on scale
            if (scale == 1)             //don't try to convert to switch statement unless you're a genius, the overlaps made it ugly when I tried
                expandPane.getChildren().add(0, boxesToAddFrom.get(6)); //milliseconds
            if (scale <= 3)
                expandPane.getChildren().add(0, boxesToAddFrom.get(5)); //seconds
            if (scale >= 3 && scale <= 5)
                expandPane.getChildren().add(0, boxesToAddFrom.get(4)); //minutes
            if (scale >= 4 && scale <= 6)
                expandPane.getChildren().add(0, boxesToAddFrom.get(3)); //hours
            if (scale >= 5 && scale <= 8)
                expandPane.getChildren().add(0, boxesToAddFrom.get(2)); //days
            if (scale >= 7)
                expandPane.getChildren().add(0, boxesToAddFrom.get(1)); //months
            if (scale >= 8)
                expandPane.getChildren().add(0, boxesToAddFrom.get(0)); //years
        }
        return expandPane.getChildren().size();
    }

    public void saveEditButton() {
        if (editable && hasChanges())   //if unsaved changes, try to save
            if (!validData() || !saveConfirm())         //if save cancelled, don't change mode
                return;
        toggleEditable(!editable);
    }

    void toggleEditable(boolean editable) {
        this.editable = editable;

        titleInput.setEditable(editable);
        descriptionInput.setEditable(editable);
        for (VBox box : startBoxes)
            box.getChildren().get(1).setDisable(!editable);
        for (VBox box : endBoxes)
            box.getChildren().get(1).setDisable(!editable);
        keyWordBox.setDisable(!editable);
        timeInput.setDisable(!editable);

        if (editable)
            editor.getStylesheets().remove("styles/DisabledViewable.css");
        else
            editor.getStylesheets().add("styles/DisabledViewable.css");

        editButton.setText(editable ? "Save" : "Edit");
    }

    boolean hasChanges() {
        Date readStart = new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue());   //milliseconds not implemented yet, do we need to?

        Date readEnd = new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue());


        if (timeline.getKeywords().size() != keywords.size())
            return true;

        if (timeline.getScale() != timeInput.getSelectionModel().getSelectedIndex() + 1)
            return true;

        for (int i = 0; i < keywords.size(); i++)
            if (timeline.getKeywords().get(i).compareTo(keywords.get(i)) != 0)
                return true;

        return (
                !timeline.getTimelineName().equals(titleInput.getText())
                        || !timeline.getTimelineDescription().equals(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n"))     //textArea tends to change the newline from \r\n to just \n which breaks some things
                        || timeline.getStartDate().compareTo(readStart) != 0
                        || timeline.getEndDate().compareTo(readEnd) != 0
        );
    }

    boolean validData() {
        Date newStartDate = new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue());

        Date newEndDate = new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue());

        if (newStartDate.compareTo(newEndDate) > 0) {
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDelete.setTitle("Invalid Dates");
            confirmDelete.setHeaderText("The End Date must be after the Start Date.");
            confirmDelete.setContentText("Make sure to check your dates before saving.");

            confirmDelete.showAndWait();
            return false;
        } else
            return true;
    }

    @FXML
    private boolean saveConfirm() {
        Alert confirmsave = new Alert(Alert.AlertType.CONFIRMATION);
        confirmsave.setTitle("Confirm Save");
        confirmsave.setHeaderText("This will make permanent changes to your timeline!"); //TODO change text
        confirmsave.setContentText("Would you like to save?");

        Optional<ButtonType> result = confirmsave.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        return saveTimeline();
    }

    boolean saveTimeline() {
        updateTimeline();
        try {
            if (timeline.getTimelineID() == 0) {
                DBM.insertIntoDB(timeline);
            } else
                DBM.updateInDB(timeline);

            parentController.populateDisplay();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @FXML
    void close() {
        if (timeline != null && hasChanges())
            saveConfirm();        //do you wanna save and exit or just exit?
        parentController.rightSidebar.getChildren().remove(editor);
    }

    public boolean isUniqueKeyword(String k) {
        for (String s : keywords) {
            if (k.equalsIgnoreCase(s)) return false;

        }
        return true;
    }


    public void addKeyword() {
        String inputWord = keywordInput.getText();
        inputWord = inputWord.replace(",", " ");
        if (inputWord.isBlank()) {
            feedbackText.setText("Keyword cannot be empty!");
        } else {
            if (!isUniqueKeyword(inputWord)) {
                feedbackText.setText("Keyword already exists!");
            } else {
                keywords.add(inputWord);
                feedbackText.setText("Keyword " + inputWord + " added");
                keywords.sort(String::compareTo);
                keywordInput.setText("");
            }
        }
    }

    public void removeKeyword() {
        if (listView.getSelectionModel().getSelectedIndex() < 0) {
            feedbackText.setText("No keyword selected!");
        } else {
            String removedWord = listView.getSelectionModel().getSelectedItem();
            keywords.remove(listView.getSelectionModel().getSelectedIndex());
            feedbackText.setText("Keyword " + removedWord + " removed!");
            listView.getSelectionModel().select(-1);
        }
    }
}

