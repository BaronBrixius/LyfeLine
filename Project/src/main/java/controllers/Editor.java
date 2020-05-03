package controllers;

import database.DBM;
import database.TimelineObject;
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
    TimelineObject thing;

    public void initialize() {
        setupTimeInputSpinners();

    }

    void setupTimeInputSpinners() {
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
    }

    void setupTimeInputBoxes(String timeSpinnerLabel, int maxValue, int i, List<Spinner<Integer>> spinnerList, List<VBox> boxList) {
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

    @FXML
    void toggleStartExpanded(ActionEvent actionEvent) {
        startExpanded = !startExpanded;
        setExpansion(startPane, startBoxes, startExpanded, parentController.activeTimeline.getScale());
    }

    @FXML
    void toggleEndExpanded(ActionEvent actionEvent) {
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

    abstract boolean hasChanges();

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
        Alert confirmsave = new Alert(Alert.AlertType.CONFIRMATION);
        confirmsave.setTitle("Confirm Save");
        confirmsave.setHeaderText("This will make permanent changes!"); //TODO change text
        confirmsave.setContentText("Would you like to save?");

        Optional<ButtonType> result = confirmsave.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        return save();
    }

    abstract boolean save();

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

    void setOwner(boolean owner) {        //Check if Owner
        saveEditButton.setDisable(!owner);
        saveEditButton.setVisible(owner);
        deleteButton.setDisable(!owner);
        deleteButton.setVisible(owner);
    }

    void populateDisplay(TimelineObject itemInEditor){
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

            endInputs.get(0).getValueFactory().setValue(itemInEditor.getEndDate().getYear());
            endInputs.get(1).getValueFactory().setValue(itemInEditor.getEndDate().getMonth());
            endInputs.get(2).getValueFactory().setValue(itemInEditor.getEndDate().getDay());
            endInputs.get(3).getValueFactory().setValue(itemInEditor.getEndDate().getHour());
            endInputs.get(4).getValueFactory().setValue(itemInEditor.getEndDate().getMinute());
            endInputs.get(5).getValueFactory().setValue(itemInEditor.getEndDate().getSecond());
            endInputs.get(6).getValueFactory().setValue(itemInEditor.getEndDate().getMillisecond());
        }

        setExpansion(startPane, startBoxes, false, parentController.activeTimeline.getScale());
        setExpansion(endPane, endBoxes, false, parentController.activeTimeline.getScale());
    }

    abstract boolean populateDisplay();

    void updateItem(TimelineObject itemInEditor){
        itemInEditor.setName(titleInput.getText());
        itemInEditor.setDescription(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n"));

        itemInEditor.setStartDate(new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue()));

        itemInEditor.setEndDate(new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue()));
    }

    boolean hasChanges(TimelineObject itemInEditor){
        if (!itemInEditor.getName().equals(titleInput.getText())
                || !itemInEditor.getDescription().equals(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n")))     //textArea tends to change the newline from \r\n to just \n which breaks some things)
            return true;

        Date readStart = new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue());   //milliseconds not implemented yet, do we need to?

        Date readEnd = new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue());

        return (
                itemInEditor.getStartDate().compareTo(readStart) != 0
                        || itemInEditor.getEndDate().compareTo(readEnd) != 0
        );
    }

    boolean save(TimelineObject itemInEditor){
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
}
