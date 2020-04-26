package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimelineView {

    public GridPane timelineGrid;
    public Timeline activeTimeline;
    public BorderPane mainBorderPane;
    public StackPane rightSidebar;
    EventSelector selectorController;
    EventEditor editorController;
    @FXML
    private Button backButton;
    @FXML
    private HBox everythingHBox;
    private List<EventNode> eventList = new ArrayList<>();

    public void initialize() {
        try {
            FXMLLoader selectorLoader = new FXMLLoader(getClass().getResource("../FXML/EventSelector.fxml"));
            selectorLoader.load();
            selectorController = selectorLoader.getController();
            selectorController.setParentController(this);
        } catch (IOException e) {
            e.printStackTrace();        //TODO replace with better error message once dev is done
        }

        try {
            FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("../FXML/EventEditor.fxml"));
            editorLoader.load();
            editorController = editorLoader.getController();
            editorController.setParentController(this);
        } catch (IOException e) {
            e.printStackTrace();        //TODO replace with better error message once dev is done
        }
    }

    public List<EventNode> getEventList() {
        return eventList;
    }

    public void goBackButton() {
        try {
            GUIManager.swapScene("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Call this method when swapping scenes
    public void setActiveTimeline(Timeline t) {
        this.activeTimeline = t;
        selectorController.setTimelineSelected(activeTimeline);  //sets the selected index to the currently viewed timeline
        populateDisplay();
    }

    //This method is probably not needed, but whatever      //useful for dev work to set things up quickly!
    public boolean setActiveTimeline(int id) {
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineID = ?");
            stmt.setInt(1, id);
            List<Timeline> list = DBM.getFromDB(stmt, new Timeline());

            setActiveTimeline(list.get(0));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (IndexOutOfBoundsException i) {
            System.out.println("Could not find that timeline.");
            return false;
        }
    }

    void populateDisplay() {
        timelineGrid.getChildren().clear();

        Pane mainLine = new Pane();
        mainLine.setStyle("-fx-background-color: #ff4251;");
        timelineGrid.add(mainLine, 0, 0, GridPane.REMAINING, 1);
        //TODO set grid column count to actual timeline length, make the above look better (possibly with its own fxml?)

        EventNode newNode;
        for (Event e : activeTimeline.getEventList()) {
            newNode = addEvent(e);
            eventList.add(newNode);
            placeEvent(newNode);
        }
    }

    EventNode addEvent(Event event) {
        try {
            FXMLLoader nodeLoader = new FXMLLoader(getClass().getResource("../FXML/EventNode.fxml"));
            nodeLoader.load();
            EventNode newNode = nodeLoader.getController();
            newNode.setActiveEvent(event, activeTimeline, this);
            return newNode;
        } catch (IOException e) {
            e.printStackTrace();        //TODO replace with better error message once dev is done
            return null;
        }
    }

    void placeEvent(EventNode newNode) {
        int startColumn = newNode.getStartColumn();
        int columnSpan = newNode.getColumnSpan();
        if (startColumn < 0) {          //if node starts before the timeline begins, cut the beginning
            columnSpan += startColumn;
            startColumn = 0;
        }
        if (startColumn + columnSpan > timelineGrid.getColumnCount())   //if node goes past the timeline's end, cut the end
            columnSpan = timelineGrid.getColumnCount() - startColumn;
        if (columnSpan < 1)         //if, after cutting, nothing remains, don't display it at all
            return;

        int row = 2;    //TODO calculate which row it needs to go in based on availability
        timelineGrid.add(newNode.getDisplayPane(), startColumn, row, columnSpan, 1);
    }

    public void openEventSelector() {
        rightSidebar.getChildren().remove(selectorController.selector);       //resets the event selector if it already exists
        rightSidebar.getChildren().add(selectorController.selector);
    }
}
