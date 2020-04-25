package controllers;

import database.Event;
import database.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class EventNode {

    @FXML
    public Pane displayPane;
    @FXML
    private Label eventNameDisplay;
    private Event activeEvent;
    private int startColumn;
    private int columnSpan;
    private TimelineView parentController;

    public Pane getDisplayPane() {
        return displayPane;
    }

    public void initialize() {

    }

    public Event getActiveEvent() {
        return activeEvent;
    }

    void setActiveEvent(Event event, Timeline activeTimeline, TimelineView parentController) {
        this.activeEvent = event;
        this.parentController = parentController;

        startColumn = activeTimeline.getStartDate().distanceTo(activeEvent.getStartDate(), activeTimeline.getScale());
        columnSpan = Math.max(activeEvent.getStartDate().distanceTo(activeEvent.getEndDate(), activeTimeline.getScale()), 1);   //instant events still need 1 whole column
        eventNameDisplay.setText(activeEvent.getEventName());
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getColumnSpan() {
        return columnSpan;
    }


    @FXML
    public void openDetails(MouseEvent mouseEvent) {
    }

    @FXML
    public void closeDetails(MouseEvent mouseEvent) {
        //close stuff
    }

    @FXML
    public void openEventViewer() {       //on click
        try {
            FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("../FXML/EventEditor.fxml"));
            parentController.rightSidebar.getChildren().add(editorLoader.load());

            EventEditor editor = editorLoader.getController();
            editor.setEvent(activeEvent);
            editor.toggleEditable(false);
            editor.setParentController(parentController);
        } catch (IOException e) {
            e.printStackTrace();        //TODO replace with better error message once dev is done
        }


    }
}
