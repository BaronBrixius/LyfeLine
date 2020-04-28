package controllers;

import database.Event;
import database.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class EventNode implements Comparable<EventNode> {

    @FXML
    public Pane displayPane;
    @FXML
    private Label eventNameDisplay;
    private Event activeEvent;
    private int startColumn;
    private int columnSpan;
    private TimelineView parentController;
    @FXML
    private Tooltip hoverFlag;

    public Pane getDisplayPane() {
        return displayPane;
    }

    public void initialize() {
        hoverFlag.setShowDelay(hoverFlag.getShowDelay().divide(8));
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
    public void openDetails() {
        hoverFlag.setText(activeEvent.getEventName() + "\n" + activeEvent.getEventDescrition());
    }

    @FXML
    public void openEventViewer() {       //upon clicking a node
        parentController.editorController.close();
        parentController.editorController.setEvent(activeEvent);
        parentController.editorController.toggleEditable(false);
        parentController.rightSidebar.getChildren().add(parentController.editorController.editor);
    }

    @Override
    public int compareTo(EventNode o) {     //sorts by earlier start, then by longest span as tiebreaker
        if (this.startColumn != o.startColumn)
            return this.startColumn - o.startColumn;
        return o.columnSpan - this.columnSpan;
    }
}