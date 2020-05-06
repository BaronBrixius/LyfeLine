package controllers;

import database.DBM;
import database.TimelineObject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import utils.Date;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Editor {
    final List<VBox> startBoxes = new ArrayList<>();
    final List<Spinner<Integer>> startInputs = new ArrayList<>();
    final List<VBox> endBoxes = new ArrayList<>();
    final List<Spinner<Integer>> endInputs = new ArrayList<>();
    @FXML
    HBox editor;
    @FXML
    TextField titleInput = new TextField();
    @FXML
    TextArea descriptionInput = new TextArea();
    @FXML
    Button saveEditButton;
    @FXML
    Button deleteButton;
    @FXML
    Text errorMessage;
    @FXML
    Button moreStart;
    @FXML
    Button moreEnd;
    @FXML
    FlowPane startPane;
    @FXML
    FlowPane endPane;
    @FXML
    GridPane inputFields;
    @FXML
    GridPane controlButtons;
    boolean editable = true;
    boolean startExpanded;
    boolean endExpanded;
    TimelineView parentController;

    public void initialize() {
        //Set Up the Spinners for Start/End Inputs, would have bloated the .fxml and variable list a ton if these were in fxml
        setupTimeInputStartAndEnd("Year", Integer.MIN_VALUE + 1, Integer.MAX_VALUE, 0);
        setupTimeInputStartAndEnd("Month", 1, 12, 1);
        setupTimeInputStartAndEnd("Day", 1, 31, 2);
        setupTimeInputStartAndEnd("Hour", 0, 23, 3);
        setupTimeInputStartAndEnd("Minute", 0, 59, 4);
        setupTimeInputStartAndEnd("Second", 0, 59, 5);
        setupTimeInputStartAndEnd("Millisecond", 0, 999, 6);
    }

    @FXML
    void toggleStartExpanded() {
        startExpanded = !startExpanded;
        setExpansion(startPane, startBoxes, startExpanded, parentController.activeTimeline.getScale());
    }

    @FXML
    void toggleEndExpanded() {
        endExpanded = !endExpanded;
        setExpansion(endPane, endBoxes, endExpanded, parentController.activeTimeline.getScale());
    }

    int setExpansion(FlowPane expandPane, List<VBox> boxesToAddFrom, boolean expanding, int scale) {
        expandPane.getChildren().removeAll(boxesToAddFrom);         //clear out the current contents except the expansion button

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

    void setParentController(TimelineView parentController) {             //TODO delete this inelegant solution
        this.parentController = parentController;
    }

    @FXML
    void saveEditButton() {
        if (editable && hasChanges())   //if unsaved changes, try to save
            if (!validData() || !saveConfirm())         //if save cancelled, don't change mode
                return;
        toggleEditable(!editable);
    }

    void toggleEditable(boolean editable) {
        this.editable = editable;
        inputFields.setDisable(!editable);

        if (editable)
            editor.getStylesheets().remove("styles/DisabledViewable.css");
        else
            editor.getStylesheets().add("styles/DisabledViewable.css");

        saveEditButton.setText(editable ? "Save" : "Edit");
    }

    @FXML
    boolean saveConfirm() {
        Alert confirmSave = new Alert(Alert.AlertType.CONFIRMATION);
        confirmSave.setTitle("Confirm Save");
        confirmSave.setHeaderText("This will make permanent changes!"); //TODO change text
        confirmSave.setContentText("Would you like to save?");

        Optional<ButtonType> result = confirmSave.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        return save();
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
        }
        return true;
    }

    void setOwner(boolean owner) {
        saveEditButton.setDisable(!owner);
        deleteButton.setDisable(!owner);
    }

    abstract boolean populateDisplay();

    void populateDisplay(TimelineObject itemInEditor) {
        titleInput.setText(itemInEditor.getName());
        descriptionInput.setText(itemInEditor.getDescription());

        if (itemInEditor.getStartDate() != null) {
            startInputs.get(0).getValueFactory().setValue(itemInEditor.getStartDate().getYear());
            startInputs.get(1).getValueFactory().setValue(itemInEditor.getStartDate().getMonth());
            startInputs.get(2).getValueFactory().setValue(itemInEditor.getStartDate().getDay());
            startInputs.get(3).getValueFactory().setValue(itemInEditor.getStartDate().getHour());
            startInputs.get(4).getValueFactory().setValue(itemInEditor.getStartDate().getMinute());
            startInputs.get(5).getValueFactory().setValue(itemInEditor.getStartDate().getSecond());
            startInputs.get(6).getValueFactory().setValue(itemInEditor.getStartDate().getMillisecond());

            populateEndInputs(itemInEditor);
        }

        setExpansion(startPane, startBoxes, false, parentController.activeTimeline.getScale());
        setExpansion(endPane, endBoxes, false, parentController.activeTimeline.getScale());
    }

    void populateEndInputs(TimelineObject itemInEditor) {            //so that end dates can have their display toggled separately for events
        endInputs.get(0).getValueFactory().setValue(itemInEditor.getEndDate().getYear());
        endInputs.get(1).getValueFactory().setValue(itemInEditor.getEndDate().getMonth());
        endInputs.get(2).getValueFactory().setValue(itemInEditor.getEndDate().getDay());
        endInputs.get(3).getValueFactory().setValue(itemInEditor.getEndDate().getHour());
        endInputs.get(4).getValueFactory().setValue(itemInEditor.getEndDate().getMinute());
        endInputs.get(5).getValueFactory().setValue(itemInEditor.getEndDate().getSecond());
        endInputs.get(6).getValueFactory().setValue(itemInEditor.getEndDate().getMillisecond());
    }

    void updateItem(TimelineObject itemInEditor) {                  //sets object's values based on input fields' values
        itemInEditor.setName(titleInput.getText());
        itemInEditor.setDescription(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n"));

        itemInEditor.setStartDate(new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue()));

        itemInEditor.setEndDate(new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue()));
    }

    abstract boolean hasChanges();

    boolean hasChanges(TimelineObject itemInEditor) {           //returns true if any input fields don't match the object's values
        if (!itemInEditor.getName().equals(titleInput.getText())
                || !itemInEditor.getDescription().equals(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n")))     //textArea tends to change the newline from \r\n to just \n which breaks some things)
            return true;

        Date readStart = new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue());

        Date readEnd = new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue());

        return (
                itemInEditor.getStartDate().compareTo(readStart) != 0
                        || itemInEditor.getEndDate().compareTo(readEnd) != 0
        );
    }

    abstract boolean save();

    boolean save(TimelineObject itemInEditor) {
        try {
            if (itemInEditor.getID() == 0) {
                DBM.insertIntoDB(itemInEditor);
            } else
                DBM.updateInDB(itemInEditor);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void setupTimeInputStartAndEnd(String timeSpinnerLabel, int minValue, int maxValue, int index) {    //applies equivalent setups to both start and end spinners
        setupTimeInput(timeSpinnerLabel, minValue, maxValue, index, startInputs, startBoxes);
        setupTimeInput(timeSpinnerLabel, minValue, maxValue, index, endInputs, endBoxes);
    }

    //creates spinners to handle dates with appropriate min/max values and invalid input handling
    private void setupTimeInput(String timeSpinnerLabel, int minValue, int maxValue, int index, List<Spinner<Integer>> spinnerList, List<VBox> boxList) {
        int initValue = (timeSpinnerLabel.equals("Year")) ? 0 : minValue;   //initial value is equal to minimum, except in the case of years

        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, initValue);
        valueFactory.setConverter(new StringConverter<>() {                 //makes spinners revert to default values in case of invalid input
            @Override
            public String toString(Integer value) {     //called by spinner to update the displayed value in the box
                if (value == null)
                    return String.valueOf(initValue);
                return value.toString();
            }

            @Override
            public Integer fromString(String string) {  //called by spinner to read the value from the box and convert to int
                try {
                    if (string == null)
                        return initValue;
                    string = string.trim();
                    if (string.length() < 1)
                        return initValue;
                    return Integer.parseInt(string);

                } catch (NumberFormatException ex) {
                    return initValue;
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
        boxList.add(index, new VBox(new Label(timeSpinnerLabel), spinnerList.get(index)));
        boxList.get(index).setPrefWidth(70);
        boxList.get(index).getChildren().get(0).getStyleClass().add("smallText");
    }
}
