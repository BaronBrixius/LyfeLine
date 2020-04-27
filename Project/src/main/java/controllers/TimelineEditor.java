package controllers;

import database.DBM;
import database.Event;
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
import utils.Date;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TimelineEditor {
    private final List<VBox> startBoxes = new ArrayList<>();
    private final List<Spinner<Integer>> startInputs = new ArrayList<>();
    private final List<VBox> endBoxes = new ArrayList<>();
    private final List<Spinner<Integer>> endInputs = new ArrayList<>();
    @FXML public GridPane editor;
    @FXML public Button editButton;
    @FXML public Button deleteButton;
    @FXML public Label headerText;
    @FXML public Text errorMessage;
    @FXML public FlowPane startPane;
    @FXML public FlowPane endPane;
    public Button removeButton;
    public Button addKeyWord;
    public ListView<String> listView;
    public Text feedbackText;
    public HBox keyWordBox;
    @FXML TextField titleInput = new TextField();
    @FXML TextArea descriptionInput = new TextArea();
    boolean editable = true;
    TimelineView parentController;
    private boolean startExpanded;
    private boolean endExpanded;
    public Timeline timeline;

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
        }
        //fix ranges for years since they're a little different
        startInputs.get(0).setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
        endInputs.get(0).setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
        toggleEditable(false);
        listView.setItems(keywords);
    }

    private void setupTimeInputBoxes(String timeSpinnerLabel, int maxValue, int i, List<Spinner<Integer>> startTimes, List<VBox> startBoxes) {
        startTimes.add(i, new Spinner<>(0, maxValue, 0));
        startBoxes.add(i, new VBox(new Label(timeSpinnerLabel), startTimes.get(i)));
        startBoxes.get(i).setPrefWidth(70);
        startBoxes.get(i).getChildren().get(0).getStyleClass().add("smallText");
    }

    public void setParentController(TimelineView parentController) {             //TODO delete this inelegant solution
        this.parentController = parentController;
    }

    //@FXML
    //private void toggleHasDuration() {
    //    endPane.setDisable(!hasDuration.isSelected());
    //    setExpansion(false, hasDuration.isSelected() && endExpanded);   //compresses if disabled, if enabled leave it as user wanted
    //    if (hasDuration.isSelected())
    //        endPane.getStyleClass().remove("DisabledAnyways");
    //    else
    //        endPane.getStyleClass().add("DisabledAnyways");
    //}

    public void saveEditButton() {
        if (editable && hasChanges())   //if unsaved changes, try to save
            if (!saveConfirm())         //if save cancelled, don't change mode
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

        if (editable)
            editor.getStylesheets().remove("styles/DisabledViewable.css");
        else
            editor.getStylesheets().add("styles/DisabledViewable.css");

        editButton.setText(editable ? "Save" : "Edit");
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

        setExpansion(true, false);
        setExpansion(false, false);


        keywords.addAll(timeline.getKeywords());
        keywords.sort((s1,s2)->s1.compareTo(s2));

        return false;
    }

    @FXML
    private boolean saveConfirm() {
        Alert confirmsave = new Alert(Alert.AlertType.CONFIRMATION);
        confirmsave.setTitle("Confirm Save");
        confirmsave.setHeaderText("Die (change later)"); //TODO change text
        confirmsave.setContentText("Would you like to save?");

        Optional<ButtonType> result = confirmsave.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        return saveTimeline();
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


    }

    private boolean saveTimeline() {
        updateTimeline();
        try {
            if (timeline.getTimelineID() == 0) {
                DBM.insertIntoDB(timeline);
                //event.addToTimeline(parentController.timelineList.getSelectionModel().getSelectedItem().getTimelineID());
                //parentController.populateEventList();             //TODO fix updating the display on the event selector
            } else
                DBM.updateInDB(timeline);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @FXML
    private boolean deleteEvent() {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("Die (change later)"); //TODO change text
        confirmDelete.setContentText("Are you ok with this?");

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;

        return true;

        //try {
        //    if (this.timeline.getEventID() == 0)
        //        throw new IllegalArgumentException("event not in database");
        //    else {
        //        DBM.deleteFromDB(timeline);
        //        close();
        //    }
        //    //parentController.populateEventList();             //TODO fix updating the display on the event selector
        //    return true;
        //} catch (SQLException e) {
        //    return false;
        //}
    }

    public void toggleStartExpanded(ActionEvent actionEvent) {
        startExpanded = !startExpanded;
        setExpansion(true, startExpanded);
    }

    public void toggleEndExpanded(ActionEvent actionEvent) {
        endExpanded = !endExpanded;
        setExpansion(false, endExpanded);
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

    private boolean hasChanges() {
        Date readStart = new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue());   //milliseconds not implemented yet, do we need to?

        Date readEnd = new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue());

        if (timeline.getKeywords().size() != keywords.size())
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

    @FXML
    void close() {
        if (timeline != null && hasChanges())
            saveConfirm();        //do you wanna save and exit or just exit?
        parentController.rightSidebar.getChildren().remove(editor);
    }

    public boolean isUniqueKeyword(String k) {
        for(String s:keywords) {
            if(k.equalsIgnoreCase(s)) return false;

        }
        return true;
    }


    public void addKeyword(ActionEvent event) {
        String inputWord = keywordInput.getText();
        inputWord = inputWord.replace(",", " ");
        if(inputWord.isBlank()) {
            feedbackText.setText("Keyword cannot be empty!");
        }
        else {
            if(!isUniqueKeyword(inputWord)) {
                feedbackText.setText("Keyword already exists!");
            }
            else {
                keywords.add(inputWord);
                feedbackText.setText("Keyword "+inputWord+" added");
                keywords.sort((s1,s2)->s1.compareTo(s2));
                keywordInput.setText("");
            }
        }
    }

    public void removeKeyword(ActionEvent event) {
        if(listView.getSelectionModel().getSelectedIndex()<0) {
            feedbackText.setText("No keyword selected!");
        }
        else {
            String removedWord=listView.getSelectionModel().getSelectedItem();
            keywords.remove(listView.getSelectionModel().getSelectedIndex());
            feedbackText.setText("Keyword "+removedWord+" removed!");
            listView.getSelectionModel().select(-1);
        }
    }
}

