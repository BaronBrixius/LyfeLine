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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TimelineView {

    public GridPane timelineGrid;
    public Timeline activeTimeline;
    public BorderPane mainBorderPane;
    public StackPane rightSidebar;
    @FXML
    private Button backButton;
    @FXML
    private HBox everythingHBox;


    public void initialize() {
        /*try {
            everythingHBox.getChildren().add(FXMLLoader.load(GUIManager.class.getResource("../FXML/EventSelector.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
        for (Event e : activeTimeline.getEventList())
            addEvent(e);

        timelineGrid.add(new Circle(5, Color.DARKGRAY), 3, 3);
    }

    private void addEvent(Event event) {
        try {
            FXMLLoader nodeLoader = new FXMLLoader(getClass().getResource("../FXML/EventNode.fxml"));
            nodeLoader.load();
            EventNode newNode = nodeLoader.getController();
            newNode.setActiveEvent(event, activeTimeline, this);
            placeEvent(newNode);
        } catch (IOException e) {
            e.printStackTrace();        //TODO replace with better error message once dev is done
        }
    }

    private void placeEvent(EventNode newNode) {
        int row = 2;    //TODO calculate which row it needs to go in based on availability
        timelineGrid.add(newNode.getDisplayPane(), newNode.getStartColumn(), row, newNode.getColumnSpan(), 1);
    }
}
