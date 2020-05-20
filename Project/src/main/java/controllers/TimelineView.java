package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import utils.DateUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class TimelineView {
    private final List<EventNode> eventList = new ArrayList<>();
    public GridPane timelineGrid;
    public ScrollPane mainScrollPane;
    public Timeline activeTimeline;
    public WritableImage snapshot;
    public BorderPane mainBorderPane;
    public StackPane rightSidebar;
    public StackPane leftSidebar;
    public StackPane centeringStack;
    @FXML
    TimelineEditor timelineEditorController;
    @FXML
    EventSelector eventSelectorController;
    @FXML
    EventEditor eventEditorController;
    @FXML
    ImageExport imageExportController;
    @FXML
    private Button backButton;

    public void initialize() {
        timelineEditorController.setParentController(this);
        eventSelectorController.setParentController(this);
        eventEditorController.setParentController(this);


        leftSidebar.getChildren().add(timelineEditorController.editor);
        rightSidebar.getChildren().add(eventSelectorController.selector);

        GUIManager.menu.export.setOnAction(e -> GUIManager.menu.exportToJSON(activeTimeline));
        GUIManager.menu.showExportMenu(true);

        centeringStack.addEventFilter(ScrollEvent.ANY, this::scrollHandler);
    }

    public boolean isZoomed() {
        if (timelineGrid.getScaleX() != 1 & timelineGrid.getScaleX() >= 0.1)
            return true;
        else
            return false;
    }



    public void snapshot() throws IOException {
        SnapshotParameters snapShotparams = new SnapshotParameters();

        snapShotparams.setFill(javafx.scene.paint.Paint.valueOf("#f4f4f4")); //TODO read from root background color
        if (isZoomed()) {
            mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            WritableImage temp = mainScrollPane.snapshot(snapShotparams,
                    new WritableImage((int) mainScrollPane.getLayoutBounds().getWidth(),
                            (int) mainScrollPane.getLayoutBounds().getHeight()));
            System.out.println(" zoom printout");


            //Now create buffered image and add 10% padding on top and bottom
            BufferedImage fromFXImage = SwingFXUtils.fromFXImage(temp, null);
            System.out.println(fromFXImage.getHeight() + " and width is " + fromFXImage.getWidth());

            // Calculate height width , offset
            int width = fromFXImage.getWidth();
            int height = fromFXImage.getHeight() ;
            int height2 = (int) (height * 1.20);
            int offset = (int) (height * 0.1);

            // Create another image with new height & width
            BufferedImage backImage = new BufferedImage( width, height2, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = backImage.createGraphics();

            // Am setting the color to black to distinguish , otherwise it can be set to Color.white
            g.setColor(new Color(244, 244, 244)); //TODO read from root background color
            // Fill hte background with color
            g.fillRect(0, 0, width , height2);
            // Now overlay with image from offset
            g.drawImage(fromFXImage,0,offset,null);
            snapshot= SwingFXUtils.toFXImage(backImage, null);
            System.out.println(backImage.getHeight() + " and width is " + backImage.getWidth());
            g.dispose();
            mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            }
        else{ //If not Zoomed or too much out zoom
        timelineGrid.setScaleX(1);
        timelineGrid.setScaleY(1);
        WritableImage  temp = timelineGrid.snapshot(snapShotparams,
                    new WritableImage((int) timelineGrid.getLayoutBounds().getWidth(),
                            (int) timelineGrid.getLayoutBounds().getHeight()));
        System.out.println("No zoom printout" + " and height is: " + temp.getHeight() + " and width is: " + temp.getWidth());

        //Now create buffered image and add 10% padding on top and bottom
        BufferedImage fromFXImage = SwingFXUtils.fromFXImage(temp, null);
        System.out.println(fromFXImage.getHeight() + " and width is " + fromFXImage.getWidth());

        // Calculate height width , offset
        int width = fromFXImage.getWidth();
        int height = fromFXImage.getHeight() ;
        // int height2 = (int) (height * 1.20);
        int offset = (int) (height * 0.1);

        // Create another image with new height & width
        BufferedImage backImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = backImage.createGraphics();

        // Am setting the color to black to distinguish , otherwise it can be set to Color.white
        g.setColor(new Color(244, 244, 244)); //TODO read from root background color
        // Fill hte background with color
        g.fillRect(0, 0, width , height);
        // Now overlay with image from offset
        g.drawImage(fromFXImage,0,offset,null);
        System.out.println(backImage.getHeight() + " and width is " + backImage.getWidth());
        snapshot= SwingFXUtils.toFXImage(backImage, null);
        g.dispose();}
    }

    public List<EventNode> getEventList() {
        return eventList;
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
        int numberOfCol = DateUtil.distanceBetween(activeTimeline.getStartDate(), activeTimeline.getEndDate(), activeTimeline.getScale());
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
           //copy(snapshot()); //just method I used to see the snapshot output

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scrollHandler(ScrollEvent event) {
        final double scaleFactor = 1.2;

        double oldScale = timelineGrid.getScaleX();
        double newScale = event.getDeltaY() > 0 ? oldScale * scaleFactor : oldScale / scaleFactor;  //calculate new scale based on old
        if (newScale > 100)                                                         //max zoom is 100x
            newScale = 100;
        if (newScale < .001)                                                        //min zoom is 1/100x
            newScale = .001;    //TODO ask client if he's sure he wants no minimum zoom, even at this point each bar is less than a pixel tall, i.e. invisible


        double hMousePosition = (event.getX() / centeringStack.getWidth());               //record mouse position for "zoom to mouse"
        double vMousePosition = (event.getY() / centeringStack.getHeight());

        double adjustedHValue = mainScrollPane.getHvalue() * oldScale / newScale    //snapshot scrollbar positions before resizing moves them
                + hMousePosition * (1 - oldScale / newScale);                       //adjust snapshots based on mouse position, weighted average of old position and mouse position,
        double adjustedVValue = mainScrollPane.getVvalue() * oldScale / newScale    //while zooming in, old position is ~83% weight (1/1.2) and mouse position is ~17% (1-(1/1.2)) (assuming scaleFactor is still 1.2)
                + vMousePosition * (1 - oldScale / newScale);                       //while "zooming out away from mouse", mouse position is applied negatively. original position is 120% weight and mouse position is -20%

        timelineGrid.setScaleX(newScale);                                           //apply scaling/zooming
        timelineGrid.setScaleY(newScale);

        mainScrollPane.layout();                                                    //update contents based on new scale, which jumps the view around

        mainScrollPane.setHvalue(adjustedHValue);                                   //apply (adjusted) snapshots of scrollbar positions, overriding the above jumping
        mainScrollPane.setVvalue(adjustedVValue);

        event.consume();                                                            //consume the mouse event to prevent normal scrollbar functions
    }

    private void horizontalScroll(ScrollEvent scrollEvent) {    //might wanna add this back in when user is holding a button

        //setup horizontal scroll with mouse wheel
            /*if (e.getDeltaX() == 0 && e.getDeltaY() != 0) {
                mainScrollPane.setHvalue(mainScrollPane.getHvalue() - e.getDeltaY() / mainScrollPane.getWidth());
            }*/
    }




    /*
    public File fileChooser() {
        FileChooser fileChooser = new FileChooser();
        String format = ".png";

        fileChooser.setInitialFileName(activeTimeline.getName().replaceAll("\\s+", "_") + format); //We will add read format from dropdown or use png
        fileChooser.getExtensionFilters().addAll( //keep all formats now, easy to add to the popup
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

        // Calculate height width , offset
        int width = fromFXImage.getWidth();
        int height = fromFXImage.getHeight() ;
        int height2 = (int) (height * 1.20);
        int offset = (int) (height * 0.1);

        // Create another image with new height & width
        BufferedImage backImage = new BufferedImage( width, height2, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = backImage.createGraphics();

        // Am setting the color to black to distinguish , otherwise it can be set to Color.white
        g.setColor(Color.white);
        // Fill hte background with color
        g.fillRect(0, 0, width , height2);
        // Now overlay with image from offset
        g.drawImage(fromFXImage,0,offset,null);
        // write to the file
        ImageIO.write(backImage, "PNG", fileChooser() );
        System.out.println(backImage.getHeight() + " and width is " + backImage.getWidth());
        g.dispose();
    }*/


}
