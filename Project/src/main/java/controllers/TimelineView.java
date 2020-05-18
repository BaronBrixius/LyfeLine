package controllers;
import database.DBM;
import database.Event;
import database.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TimelineView {

    private final List<EventNode> eventList = new ArrayList<>();
    public GridPane timelineGrid;
    public ScrollPane mainScrollPane;
    public Timeline activeTimeline;
    public BorderPane mainBorderPane;
    public StackPane rightSidebar;
    public StackPane leftSidebar;
    @FXML
    TimelineEditor timelineEditorController;
    @FXML
    EventSelector eventSelectorController;
    @FXML
    EventEditor eventEditorController;
    @FXML
    private Button backButton;

    public void initialize() {
        timelineEditorController.setParentController(this);
        eventSelectorController.setParentController(this);
        eventEditorController.setParentController(this);

        leftSidebar.getChildren().add(timelineEditorController.editor);
        rightSidebar.getChildren().add(eventSelectorController.selector);

        mainScrollPane = (ScrollPane) mainBorderPane.getCenter();
        mainScrollPane.setOnScroll(e -> {
            timelineGrid.setScaleX(timelineGrid.getScaleX() * (1 + e.getDeltaY() / 200));     //if you want to do zoom you can start with this
            timelineGrid.setScaleY(timelineGrid.getScaleY() * (1 + e.getDeltaY() / 200));     //it doesn't quite update the scrollbar/container size properly, and zooming in zooms slightly further than zooming out because of the 1+deltaY math (e.g. 0.8 * 1.2 = 0.96)
            //setup horizontal scroll with mouse wheel
            /*if (e.getDeltaX() == 0 && e.getDeltaY() != 0) {
                mainScrollPane.setHvalue(mainScrollPane.getHvalue() - e.getDeltaY() / mainScrollPane.getWidth());
            }*/
        });
    }

    public boolean isZoomed (){
        if(timelineGrid.getScaleX() != 1 & timelineGrid.getScaleX() >= 0.25)
        return true;
        else
            return false;
    }



    public WritableImage snapshot(){
        SnapshotParameters snapShotparams = new SnapshotParameters();
        //snapShotparams.setFill(Color.TRANSPARENT);  if we want transparent background instead of white
        if (isZoomed()) {
            mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            WritableImage temp = mainScrollPane.snapshot(snapShotparams,
                    new WritableImage((int) mainScrollPane.getLayoutBounds().getWidth(),
                            (int) mainScrollPane.getLayoutBounds().getHeight()));
            System.out.println(" zoom printout");
            return temp;}
       timelineGrid.setScaleX(1);
        timelineGrid.setScaleY(1);
        WritableImage  temp = timelineGrid.snapshot(snapShotparams,
                    new WritableImage((int) timelineGrid.getLayoutBounds().getWidth(),
                            (int) timelineGrid.getLayoutBounds().getHeight()));
        System.out.println("No zoom printout");
        return temp;
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

    // Call this method when swapping scenes
    public void setActiveTimeline(Timeline t) {
        this.activeTimeline = t;
        timelineEditorController.setTimeline(t);
        eventSelectorController.setTimelineSelected(activeTimeline); // sets the selected index to the currently viewed timeline
        populateDisplay();
    }

    // This method is probably not needed, but whatever //useful for dev work to set things up quickly!
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
        timelineGrid.getColumnConstraints().clear();
        setupMainLine();
        setupEventNodes();
    }

    Pane setupMainLine() {
        Pane mainLine = new Pane();
        mainLine.getStyleClass().add("timeline");
        int numberOfCol = activeTimeline.getStartDate().distanceTo(activeTimeline.getEndDate(), activeTimeline.getScale());
        int start = 1, frequency = 1;

        switch (activeTimeline.getScale()) {
            case 8:
                frequency = 2;
                start = activeTimeline.getStartDate().getYear();
                break;
            case 9:
                start = activeTimeline.getStartDate().getYear() / 10;
                break;
            case 10:
                start = activeTimeline.getStartDate().getYear() / 100;
                break;
            case 11:
                start = activeTimeline.getStartDate().getYear() / 1000;
                break;
        }

        ColumnConstraints[] constraints = new ColumnConstraints[numberOfCol];
        Arrays.fill(constraints, new ColumnConstraints(70));
        timelineGrid.getColumnConstraints().addAll(constraints);

        for (int i = 0; i <= numberOfCol; i += frequency) {
            timelineGrid.add(new Text(String.valueOf(i + start)), i, 0);
        }

        if (numberOfCol >= 1)                               // if the start date is later than the end date, numberOfCol would be negative,
            timelineGrid.add(mainLine, 0, 0, numberOfCol, 1);   // which does not work for the amount of columns
        GridPane.setMargin(mainLine, new Insets(25, 0, -25, 0));
        return mainLine;
    }

    private void setupEventNodes() {
        eventList.clear();
        EventNode newNode;
        for (Event e : activeTimeline.getEventList()) {
            newNode = addEvent(e);
            eventList.add(newNode);
        }
        Collections.sort(eventList);            // sort so that earlier events are placed first (longer comes first in case of tie)
        for (int i = 0; i < eventList.size(); i++)
            placeEvent(eventList.get(i), i);
    }

    EventNode addEvent(Event event) {
        try {
            FXMLLoader nodeLoader = new FXMLLoader(getClass().getResource("../FXML/EventNode.fxml"));
            nodeLoader.load();
            EventNode newNode = nodeLoader.getController();
            newNode.setActiveEvent(event, activeTimeline, this);
            return newNode;
        } catch (IOException e) {
            e.printStackTrace(); // TODO replace with better error message once dev is done
            return null;
        }
    }

    void placeEvent(EventNode newNode, int eventsPlacedCount) {
        if (newNode.getStartColumn() < 0) {                                          // if node starts before the timeline begins, cut the beginning
            newNode.setColumnSpan(newNode.getColumnSpan() + newNode.getStartColumn());
            newNode.setStartColumn(0);
        }
        if (newNode.getStartColumn() + newNode.getColumnSpan() > timelineGrid.getColumnCount())   // if node goes past the timeline's end, cut the end
            newNode.setColumnSpan(timelineGrid.getColumnCount() - newNode.getStartColumn() - 1);
        if (newNode.getColumnSpan() < 1)                                             // if, after cutting, nothing remains, don't display it at all
            return;

        int row = 1;
        for (int i = 0; i < eventsPlacedCount; i++) { // check previous nodes to see if they occupy desired columns
            if (row == eventList.get(i).getRow()
                    && eventList.get(i).getStartColumn() < newNode.getStartColumn() + newNode.getColumnSpan()              // if a previous node on current row starts before the new one would end
                    && eventList.get(i).getStartColumn() + eventList.get(i).getColumnSpan() > newNode.getStartColumn())    // and it ends after the new one starts
                row++;                                                                                                     // try next row
        }
        newNode.setRow(row);
        timelineGrid.add(newNode.getDisplayPane(), newNode.getStartColumn(), row, newNode.getColumnSpan(), 1);
    }

    public void openEventSelector() {
        rightSidebar.getChildren().remove(eventSelectorController.selector); // resets the event selector if it already exists
        rightSidebar.getChildren().add(eventSelectorController.selector);
    }

    public void returnToDashboard() throws IOException {
        try {
            GUIManager.swapScene("Dashboard");
            copy(snapshot()); //just method I used to see the snapshot output

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public File fileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(this.activeTimeline.getName().replaceAll("\\s+", "_") + ".png"); //We will add read format from dropdown or use png
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif", "*.wbmp"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("WBMP", "*.wbmp")
        );

        //Show save file dialog
        File file = fileChooser.showSaveDialog(GUIManager.mainStage);
        return file;
    }

    //Just a placeholder method that creates a image of the snapshot
    public void copy(WritableImage temp) throws IOException {
        BufferedImage fromFXImage = SwingFXUtils.fromFXImage(temp, null);
        System.out.println(fromFXImage.getHeight() + " and width is " + fromFXImage.getWidth());
        ImageIO.write(fromFXImage, "PNG", fileChooser());
    }  //Printed under Project folder not images*/


}







