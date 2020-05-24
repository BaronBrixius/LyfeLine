package controllers;

import database.Event;
import database.Timeline;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import utils.DateUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TimelineView {
    private final List<EventNode> eventList = new ArrayList<>();
    @FXML
    GridPane timelineGrid;
    @FXML
    ScrollPane mainScrollPane;
    @FXML
    BorderPane mainBorderPane;
    @FXML
    StackPane rightSidebar;
    @FXML
    StackPane leftSidebar;
    @FXML
    StackPane centeringStack;
    @FXML
    TimelineEditor timelineEditorController;
    @FXML
    EventSelector eventSelectorController;
    @FXML
    EventEditor eventEditorController;
    Timeline activeTimeline;
    WritableImage snapshot;

    public void initialize() {
        timelineEditorController.setParentController(this);
        eventSelectorController.setParentController(this);
        eventEditorController.setParentController(this);

        leftSidebar.getChildren().add(timelineEditorController.editor);
        rightSidebar.getChildren().add(eventSelectorController.selector);

        centeringStack.addEventFilter(ScrollEvent.ANY, e -> {
            if (e.isControlDown())
                zoom(e);
        });
    }

    public boolean isZoomed() {
        return timelineEditorController.zoom.isSelected();
    }

    public void snapshot() {
        SnapshotParameters snapShotparams = new SnapshotParameters();
        Color used = new Color(255, 255, 255);
        boolean nopic = true;
        boolean beige = false;
        boolean blue = false;
        Background originalTimelinegrid = timelineGrid.getBackground();
        Background originalScrollgrid = mainScrollPane.getBackground();

        ObservableList<String> style = timelineGrid.getScene().getStylesheets();
        for (String s : style) {
            if (s.equals("File:src/main/resources/styles/Default.css")) {
                nopic = false;
                beige = true;
            }
            if (s.equals("File:src/main/resources/styles/Blue.css")) {
                nopic = false;
                blue = true;
            }
        }
        if (!nopic & beige) {
            snapShotparams.setFill(javafx.scene.paint.Paint.valueOf("#c7c3ad"));
            used = Color.decode("#c7c3ad");
        }
        if (!nopic & blue) {
            snapShotparams.setFill(javafx.scene.paint.Paint.valueOf("#ffffff"));
            timelineGrid.setBackground(new Background(
                    new BackgroundFill(javafx.scene.paint.Paint.valueOf("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
            mainScrollPane.setBackground(new Background(
                    new BackgroundFill(javafx.scene.paint.Paint.valueOf("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
            used = Color.decode("#ffffff");
        }
        if (nopic) {
            Color c = Color            //TODO fix this, timelines don't have style root any more
                    .decode("#" + mainScrollPane.getBackground().getFills().get(1).getFill().toString().substring(2, 8)); // Read the current color used for Timelinegrid background (root style) (FOR THE BURN IN PADDING)
            snapShotparams.setFill(mainScrollPane.getBackground().getFills().get(1).getFill());
            used = c;
        } // Read the current color used for Timeline grid background (root style) (IF EXTRA UNUSED ARE IN THE WRITABLE IMAGE)

        if (isZoomed()) { // snapshot just the Scrollpane
            mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            WritableImage temp = mainScrollPane.snapshot(snapShotparams,
                    new WritableImage((int) mainScrollPane.getLayoutBounds().getWidth(),
                            (int) timelineGrid.getLayoutBounds().getHeight()
                                    + (int) (mainScrollPane.getLayoutBounds().getHeight()
                                    - (int) timelineGrid.getLayoutBounds().getHeight())));
            System.out.println(" zoom printout");

            // Now create buffered image and add 15% padding on top and bottom
            BufferedImage fromFXImage = SwingFXUtils.fromFXImage(temp, null);

            // Calculate height width , offset
            int width = fromFXImage.getWidth();
            int height = fromFXImage.getHeight();
            // int width2 = (int) (width * 1.80);
            int height2 = (int) (height * 1.30);
            int offset = (int) (height * 0.15);
            // int offsetWidth = (int) (width * 0.4);

            // Create another image with new height & width
            BufferedImage backImage = new BufferedImage(width, height2, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = backImage.createGraphics();

            // Am setting the color to black to distinguish , otherwise it can be set to
            // Color.white
            g.setColor(used);
            // Fill the background with color
            g.fillRect(0, 0, width, height2);
            // Now overlay with image from offset
            g.drawImage(fromFXImage, 0, offset, null);
            snapshot = SwingFXUtils.toFXImage(backImage, null);
            g.dispose();
            mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        } else { // If not Zoomed or too much out zoom - snapshot the whole timeline
            timelineGrid.setScaleX(1);
            timelineGrid.setScaleY(1);
            WritableImage temp = timelineGrid.snapshot(snapShotparams, new WritableImage(
                    (int) timelineGrid.getLayoutBounds().getWidth(), (int) timelineGrid.getLayoutBounds().getHeight()));

            // Now create buffered image and add 10% padding on top and bottom
            BufferedImage fromFXImage = SwingFXUtils.fromFXImage(temp, null);

            // Calculate height width , offset
            int width = fromFXImage.getWidth();
            int height = fromFXImage.getHeight();
            int height2 = (int) (height * 1.30);
            int offset = (int) (height * 0.15);

            // Create another image with new height & width
            BufferedImage backImage = new BufferedImage(width, height2, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = backImage.createGraphics();

            // Am setting the color to black to distinguish , otherwise it can be set to Color.white
            g.setColor(used);

            // Fill hte background with color
            g.fillRect(0, 0, width, height2);
            // Now overlay with image from offset
            g.drawImage(fromFXImage, 0, offset, null);
            snapshot = SwingFXUtils.toFXImage(backImage, null);
            g.dispose();
        }
        timelineGrid.setBackground(originalTimelinegrid);
        mainScrollPane.setBackground(originalScrollgrid);
    }

    // Call this method when swapping scenes
    public void setActiveTimeline(Timeline t) {
        this.activeTimeline = t;
        timelineEditorController.setTimeline(t);
        eventSelectorController.setTimelineSelected(activeTimeline); // sets the selected index to the currently viewed
        // timeline
        populateDisplay();
    }

    void populateDisplay() {
        timelineGrid.getChildren().clear();
        timelineGrid.getColumnConstraints().clear();
        setupMainLine();
        setupEventNodes();
    }

    void setupMainLine() {
        Pane mainLine = new Pane();
        mainLine.getStyleClass().add("timeline");
        int numberOfCol = DateUtil.distanceBetween(activeTimeline.getStartDate(), activeTimeline.getEndDate(),
                activeTimeline.getScale());
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

        if (numberOfCol >= 1)                                                                // if the start date is later than the end date, numberOfCol would be negative,
            timelineGrid.add(mainLine, 0, 0, numberOfCol, 1);    // which does not work for the amount of columns
        GridPane.setMargin(mainLine, new Insets(25, 0, -25, 0));
    }

    private void setupEventNodes() {
        eventList.clear();
        EventNode newNode;
        for (Event e : activeTimeline.getEventList()) {
            newNode = addEvent(e);
            eventList.add(newNode);
        }
        Collections.sort(eventList);            //sort so that earlier events are placed first (longer comes first in case of tie)
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
        if (newNode.getStartColumn() < 0) {                                        //if node starts before the timeline begins, cut the beginning
            newNode.setColumnSpan(newNode.getColumnSpan() + newNode.getStartColumn());
            newNode.setStartColumn(0);
        }
        if (newNode.getStartColumn() + newNode.getColumnSpan() > timelineGrid.getColumnCount()) // if node goes past the timeline's end, cut the end
            newNode.setColumnSpan(timelineGrid.getColumnCount() - newNode.getStartColumn() - 1);
        if (newNode.getColumnSpan() < 1)                                            //if, after cutting, nothing remains, don't display it at all
            return;

        int row = 1;
        for (int i = 0; i < eventsPlacedCount; i++) {                                //check previous nodes to see if they occupy desired columns
            if (row == eventList.get(i).getRow()
                    && eventList.get(i).getStartColumn() < newNode.getStartColumn() + newNode.getColumnSpan()            //if a previous node on current row starts before the new one would end
                    && eventList.get(i).getStartColumn() + eventList.get(i).getColumnSpan() > newNode.getStartColumn()) //and it ends after the new one starts
                row++;                                                                                                    // try next row
        }
        newNode.setRow(row);
        timelineGrid.add(newNode.getDisplayPane(), newNode.getStartColumn(), row, newNode.getColumnSpan(), 1);
    }

    @FXML
    public void openEventSelector() {
        rightSidebar.getChildren().remove(eventSelectorController.selector); //resets the event selector if it already
        // exists
        rightSidebar.getChildren().add(eventSelectorController.selector);
    }

    public void returnToDashboard() {
        try {
            GUIManager.swapScene("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void zoom(ScrollEvent event) {
        final double scaleFactor = 1.2;

        double oldScale = timelineGrid.getScaleX();
        double newScale = event.getDeltaY() > 0 ? oldScale * scaleFactor : oldScale / scaleFactor; // calculate new scale based on old
        if (newScale > 100)                //max zoom is 100x
            newScale = 100;
        if (newScale < .001)                //min zoom is 1/100x
            newScale = .001;

        double hMousePosition = (event.getX() / centeringStack.getWidth());        //record mouse position for "zoom to mouse"
        double vMousePosition = (event.getY() / centeringStack.getHeight());

        double adjustedHValue = mainScrollPane.getHvalue() * oldScale / newScale    //snapshot scrollbar positions before resizing moves them
                + hMousePosition * (1 - oldScale / newScale);                        //adjust snapshots based on mouse position, weighted average of old position and mouse position,
        double adjustedVValue = mainScrollPane.getVvalue() * oldScale / newScale    //while zooming in, old position is ~83% weight (1/1.2) and mouse position is ~17% (1-(1/1.2))(assuming scaleFactor is still 1.2)
                + vMousePosition * (1 - oldScale / newScale);                        //while "zooming out away from mouse", mouse position is applied negatively. original position is 120% weight and mouse position is -20%

        timelineGrid.setScaleX(newScale);                                            //apply scaling/zooming
        timelineGrid.setScaleY(newScale);

        mainScrollPane.layout();                                                    //update contents based on new scale, which automatically jumps the view around

        mainScrollPane.setHvalue(adjustedHValue);                                    //apply (adjusted) snapshots of scrollbar positions, overriding the jumping
        mainScrollPane.setVvalue(adjustedVValue);

        event.consume();                                                            //consume the mouse event to prevent normal scrollbar functions
    }
}
