package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

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
            selectorController.setTimelineSelected(activeTimeline);  //sets the selected index to the currently viewed timeline
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
        populateEvents();
    }

    //This method is probably not needed, but whatever      //useful for dev work to set things up quickly!
    public boolean setActiveTimeline(int id) {
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineID = ?");
            stmt.setInt(1, id);
            List<Timeline> list = DBM.getFromDB(stmt, new Timeline());

            this.activeTimeline = list.get(0);
            populateEvents();

            //For testing
            return list.size() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (IndexOutOfBoundsException i) {
            System.out.println("Could not find that timeline.");
            return false;
        }

    }

    private void populateEvents() {
        //timelineGrid.getChildren().clear();
        //TODO add main timeline at row 0 to grid and uncomment above line

        for (Event e : activeTimeline.getEventList())
            addEvent(e);
    }

    void addEvent(Event event) {
        try {
            FXMLLoader nodeLoader = new FXMLLoader(getClass().getResource("../FXML/EventNode.fxml"));
            nodeLoader.load();
            EventNode newNode = nodeLoader.getController();
            newNode.setActiveEvent(event, activeTimeline, this);

            placeEvent(newNode);
            eventList.add(newNode);
        } catch (IOException e) {
            e.printStackTrace();        //TODO replace with better error message once dev is done
        }
    }

    private void placeEvent(EventNode newNode) {
        int row = 2;    //TODO calculate which row it needs to go in based on availability
        timelineGrid.add(newNode.getDisplayPane(), newNode.getStartColumn(), row, newNode.getColumnSpan(), 1);
    }

    public void openEventSelector() {
        rightSidebar.getChildren().remove(selectorController.selector);       //resets the event selector if it already exists
        rightSidebar.getChildren().add(selectorController.selector);
    }
}
