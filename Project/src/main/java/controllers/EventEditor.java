package controllers;

import database.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.io.*;
import java.sql.SQLException;
import java.util.Optional;

public class EventEditor extends Editor {


    @FXML
    CheckBox hasDuration = new CheckBox();
    @FXML
    Slider prioritySlider;
    Event event;


    public void initialize() {
        super.initialize();

        //set up priority slider labels
        prioritySlider.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double n) {
                if (n < 0.5) return "Not set";
                if (n < 1.5) return "Low";
                if (n < 2.5) return "Medium";
                if (n < 3.5) return "High";

                return "Not set";
            }

            //probably not used but required for the override
            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "Low":
                        return 1d;
                    case "Medium":
                        return 2d;
                    case "High":
                        return 3d;
                    default:
                        return 0d;
                }
            }
        });
    }

    @FXML
    void toggleHasDuration() {              //toggles whether the event has a distinct end date, as opposed to being instant
        endPane.setDisable(!hasDuration.isSelected());
        setExpansion(endPane, endBoxes, hasDuration.isSelected() && endExpanded, parentController.activeTimeline.getScale());   //compresses if duration is disabled, if enabled leave it as user wanted

        if (hasDuration.isSelected()) {
            populateEndInputs();
            endPane.getStyleClass().remove("DisabledAnyways");
        } else {
            for (int i = 0; i < 7; i++) {
                endInputs.get(i).getValueFactory().setValue(startInputs.get(i).getValue());
            }
            endPane.getStyleClass().add("DisabledAnyways");
        }
    }

    void toggleEditable(boolean editable) {
        super.toggleEditable(editable);
        hasDuration.setDisable(!editable);
        prioritySlider.setDisable(!editable);
    }

    boolean setEvent(Event event) {
        //parentController.eventEditorController.close();
        parentController.rightSidebar.getChildren().remove(editor);
        this.event = event;
        itemInEditor = event;
        if (this.event.getID() == 0)       //if new event, set current user as owner
            this.event.setOwnerID(GUIManager.loggedInUser.getUserID());
        setOwner(GUIManager.loggedInUser.getUserID() == this.event.getOwnerID());
        return populateDisplay();
    }

    boolean populateDisplay() {
        super.populateDisplay();    //populate inputs common to editors

        if (event.getStartDate().compareTo(event.getEndDate()) != 0) {
            hasDuration.setSelected(true);
            toggleHasDuration();
        }

        prioritySlider.setValue(event.getEventPriority());

        return true;
    }

    void updateItem() {                 //sets object's values based on input fields' values
        super.updateItem();        //update variables common to TimelineObjects
        event.setEventPriority((int) prioritySlider.getValue());
    }

    boolean save() {
        updateItem();
        boolean newEvent = event.getID() == 0;

        super.save();          //adds to database

        if (newEvent)
            addToTimeline();        //new event is automatically added to active timeline when saved
        parentController.eventSelectorController.populateDisplay();
        parentController.populateDisplay();
        return true;
    }

    private void addToTimeline() {
        parentController.activeTimeline.getEventList().add(event);
        try {
            if (event.addToTimeline(parentController.activeTimeline.getID()))
                System.out.println("Event added to " + parentController.activeTimeline + " timeline."); // remove this later once more user feedback is implemented
            else
                System.out.println("Event is already on " + parentController.activeTimeline + " timeline.");
        } catch (SQLException e) {
            System.out.println("Timeline not found in database.");
        }
    }

    @FXML
    boolean deleteEvent() {
        parentController.eventSelectorController.deleteEvent(event);
        return parentController.rightSidebar.getChildren().remove(editor);
    }

    boolean hasChanges() {
        if (!hasDuration.isSelected() && event.getStartDate().compareTo(event.getEndDate()) != 0)
            return true;
        if (super.hasChanges())
            return true;
        return event.getEventPriority() != prioritySlider.getValue();
    }

    @FXML
    boolean close() {
        parentController.rightSidebar.getChildren().remove(editor);
        parentController.rightSidebar.getChildren().add(editor);    //This moves the editor to the top of the stack pane
        if (event != null && hasChanges())
            if (closeConfirm())          //do you wanna save and exit or just exit?
            {
                if (validData())
                {
                    save();
                    return parentController.rightSidebar.getChildren().remove(editor);
                }
                else
                    return false;
            }

        return parentController.rightSidebar.getChildren().remove(editor);
    }

    @FXML
    boolean closeConfirm() {

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert confirmSave = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save them before closing?", yes, no);

        confirmSave.setTitle("Confirm Close");
        confirmSave.setHeaderText("You have made unsaved changes!"); //TODO change text

        Optional<ButtonType> result = confirmSave.showAndWait();

        if (result.get() == no)
            return false;
        return true;
    }
}