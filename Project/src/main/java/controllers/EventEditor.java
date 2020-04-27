package controllers;

import database.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class EventEditor {
/*
    private final List<VBox> startBoxes = new ArrayList<>();
    private final List<Spinner<Integer>> startInputs = new ArrayList<>();
    private final List<VBox> endBoxes = new ArrayList<>();
    private final List<Spinner<Integer>> endInputs = new ArrayList<>();
    @FXML
    public GridPane editor;
    @FXML
    public Button editButton;
    @FXML
    public Button uploadButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Label headerText;
    @FXML
    public Text errorMessage;
    @FXML
    public FlowPane startPane;
    @FXML
    public FlowPane endPane;
    @FXML
    TextField titleInput = new TextField();
    @FXML
    TextArea descriptionInput = new TextArea();
    @FXML
    CheckBox hasDuration = new CheckBox();
    @FXML
    ComboBox<ImageView> imageInput = new ComboBox<>();
    ImageView image;
    boolean editable = true;
    TimelineView parentController;
    private boolean startExpanded;
    private boolean endExpanded;
    private Event event;
    private File imageChosen; //The current image chosen by FileChooser
    String filename ; //THis is to take the name of the image choosen to add it to the copied version
    String fullOutPath; //When event is saved the path to the image in resource folder is sent here (the one we can use to send to DB)



    public void initialize() {
        //Check if Admin
        if (!GUIManager.loggedInUser.getAdmin()) {
            editButton.setVisible(false);
            editButton.setDisable(true);
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
        }
            image.setOnMouseEntered(e -> {
                image.setScaleX(8);
                image.setScaleY(8);
                image.setScaleZ(8);
            });
            image.setOnMouseExited(e -> {
                image.setScaleX(1);
                image.setScaleY(1);
                image.setScaleZ(1);
            });

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

        //Get Images
        try (PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM Images")) {
            List<String> images = DBM.getFromDB(stmt,
                    rs -> rs.getString("ImageURL"));


            //TODO delete the below if you're not using it, I put it in so I could get a blank and I was super rushed cuz it was thursday at like 2pm -Max
            List<ImageView> views = new ArrayList<>();
            ImageView blank = new ImageView(new Image("file:src/main/resources/images/pleasedontnameanythingthis.png"));
            blank.setFitHeight(40);
            blank.setFitWidth(40);
            views.add(blank);

            ImageView currImage;
            for (String s : images) {
                currImage = new ImageView(new Image("file:src/main/resources/images/" + s));
                currImage.setFitHeight(40);
                currImage.setFitWidth(40);
                views.add(currImage);
            }

    }

    private void setupTimeInputBoxes(String timeSpinnerLabel, int maxValue, int i, List<Spinner<Integer>> startTimes, List<VBox> startBoxes) {
        startTimes.add(i, new Spinner<>(0, maxValue, 0));
        startBoxes.add(i, new VBox(new Label(timeSpinnerLabel), startTimes.get(i)));
        startBoxes.get(i).setPrefWidth(70);
        startBoxes.get(i).getChildren().get(0).getStyleClass().add("smallText");
    }

    public void setParentController(TimelineView parentController) {             //TODO delete this inelegant solution
        this.parentController = parentController;
    }

    @FXML
    private void toggleHasDuration() {
        endPane.setDisable(!hasDuration.isSelected());
        setExpansion(false, hasDuration.isSelected() && endExpanded);   //compresses if disabled, if enabled leave it as user wanted
        if (hasDuration.isSelected())
            endPane.getStyleClass().remove("DisabledAnyways");
        else
            endPane.getStyleClass().add("DisabledAnyways");
    }

    public void saveEditButton() {
        if (editable && hasChanges())   //if unsaved changes, try to save
            if (!saveConfirm())         //if save cancelled, don't change mode
                return;
        toggleEditable(!editable);
    }

    void toggleEditable(boolean editable) {
        this.editable = editable;

        titleInput.setEditable(editable);
        descriptionInput.setEditable(editable);
        hasDuration.setDisable(!editable);
        for (VBox box : startBoxes)
            box.getChildren().get(1).setDisable(!editable);
        for (VBox box : endBoxes)
            box.getChildren().get(1).setDisable(!editable);
        imageInput.setDisable(!editable);
        uploadButton.setVisible(editable);
        uploadButton.setDisable(!editable);

        if (editable)
            editor.getStylesheets().remove("styles/DisabledViewable.css");
        else
            editor.getStylesheets().add("styles/DisabledViewable.css");

        editButton.setText(editable ? "Save" : "Edit");
    }


    @FXML
    private void uploadImage() throws IOException {    //Only working now for .jpg
        FileChooser chooser = new FileChooser(); //For the filedirectory
        chooser.setTitle("Upload image");
        //All the image formats supported by java.imageio https://docs.oracle.com/javase/7/docs/api/javax/imageio/package-summary.html
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter( "All Images", "*.jpg","*.jpeg","*.png","*.bmp","*.gif","*.wbmp" ),
                new FileChooser.ExtensionFilter( "JPG", "*.jpg" ),
                new FileChooser.ExtensionFilter( "JPEG", "*.jpeg" ),
                new FileChooser.ExtensionFilter( "PNG", "*.png" ),
                new FileChooser.ExtensionFilter( "BMP", "*.bmp" ),
                new FileChooser.ExtensionFilter( "GIF", "*.gif" ),
                new FileChooser.ExtensionFilter( "WBMP", "*.wbmp" )
        );
        this.imageChosen = chooser.showOpenDialog(new Stage()); //This is the stage that needs to be edited (ok,cancel button) for the filechooser... do in FXML ?
        this.filename = imageChosen.getName(); //THis is to take the name of the image choosen to add it to the copied version
        System.out.println(this.imageChosen.getAbsolutePath());
        image.setImage(new Image("File:" + this.imageChosen.getAbsolutePath()));

        System.out.println("Button pressed.");
    }

     //Method that returns the image format as a string i.e sun.png == "png"
    private String getFormat(File f) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(f);
        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
        String type = "png";
        while (imageReaders.hasNext()) {
            ImageReader reader = imageReaders.next();
           type = reader.getFormatName();
        }
        return type;
    }

    private String copyImage(File image, String filename) throws IOException { //Takes the file chosen and the name of it
        String outPath = "src/main/resources/images/";
        String imageName ="";
        try{
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(image);
            System.out.println("reading complete.");
            int imageNumer = 1; //For updating the number in the parenthesis based on how many with the same name in resources folder
             imageName = filename;
            //Path for saving, have special events folder now so if timeline guys are doing something they don't override copies
            while (folderHasImage(imageName)==true){ //Check if our folder has the imagename already if so, add (int) untill no more true
                int index = imageName.indexOf(".");            ;
                imageName = imageName.substring(0, index)+ "("+ imageNumer+")" + ".jpg";
                imageNumer++;
            }
            os = new FileOutputStream(new File(outPath + imageName));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

        } finally {
            is.close();
            os.close();
        }
            System.out.println("Writing complete.");
        }catch(IOException e){
            System.out.println("Error: "+e);
        }
        return outPath+imageName;
    }
    //Method to check if the image folder has this name already to avoid if two are copied with same name the latter will just override the firs
    private boolean folderHasImage(String path){
        File folder = new File("src/main/resources/images/");
        File[] listOfFiles = folder.listFiles();
        List<String> images = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                images.add(listOfFiles[i].getName());
            }
        }
        for(String s : images){
            if (path.equalsIgnoreCase(s))
                return true;
        }
        return false;
    }

    public boolean setEvent(int eventID) {       //is this even needed? don't implement yet
        /*Event newEvent = logic to find Event in database and get its info
        if (newEvent != null)
            return changeEvent(newEvent);*/
/*
        return false;
    }

    public boolean setEvent(Event event) {
        this.event = event;
        return populateDisplay();
    }

    private boolean populateDisplay() {
        titleInput.setText(event.getEventName());
        descriptionInput.setText(event.getEventDescrition());

        startInputs.get(0).getValueFactory().setValue(event.getStartDate().getYear());
        startInputs.get(1).getValueFactory().setValue(event.getStartDate().getMonth());
        startInputs.get(2).getValueFactory().setValue(event.getStartDate().getDay());
        startInputs.get(3).getValueFactory().setValue(event.getStartDate().getHour());
        startInputs.get(4).getValueFactory().setValue(event.getStartDate().getMinute());
        startInputs.get(5).getValueFactory().setValue(event.getStartDate().getSecond());
        startInputs.get(6).getValueFactory().setValue(event.getStartDate().getMillisecond());

        if (event.getStartDate().compareTo(event.getEndDate()) != 0) {
            hasDuration.setSelected(true);
            toggleHasDuration();
        }
        endInputs.get(0).getValueFactory().setValue(event.getEndDate().getYear());
        endInputs.get(1).getValueFactory().setValue(event.getEndDate().getMonth());
        endInputs.get(2).getValueFactory().setValue(event.getEndDate().getDay());
        endInputs.get(3).getValueFactory().setValue(event.getEndDate().getHour());
        endInputs.get(4).getValueFactory().setValue(event.getEndDate().getMinute());
        endInputs.get(5).getValueFactory().setValue(event.getEndDate().getSecond());
        endInputs.get(6).getValueFactory().setValue(event.getEndDate().getMillisecond());

        setExpansion(true, false);
        setExpansion(false, false);


        return false;
    }

    @FXML
    private boolean saveConfirm() throws IOException {
        Alert confirmsave = new Alert(Alert.AlertType.CONFIRMATION);
        confirmsave.setTitle("Confirm Save");
        confirmsave.setHeaderText("Saving changes to this event will alter it for all other timelines as well.");
        confirmsave.setContentText("Would you like to save?");

        Optional<ButtonType> result = confirmsave.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        return saveEvent();
    }

    void updateEvent() throws IOException {
        //setters to update each field of this.event, based on the current info in the text fields

        event.setTitle(titleInput.getText());
        event.setDescription(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n"));

        event.setStartDate(new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue()));
        if (hasDuration.isSelected()) {
            event.setEndDate(new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                    endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue()));
        } else                //if it has no duration, end = start
            event.setEndDate(event.getStartDate());


    }

    private boolean saveEvent() throws IOException {
        updateEvent();
        try {
            if (event.getEventID() == 0) {
                //Save button clicked, the image chosen is saved and the String field is set as the path to the image in the resource folder
                DBM.insertIntoDB(event);
                //event.addToTimeline(parentController.timelineList.getSelectionModel().getSelectedItem().getTimelineID());
                //parentController.populateEventList();             //TODO fix updating the display on the event selector
            } else
                 //Save button clicked, the image chosen is saved and the String field is set as the path to the image in the resource folder
                DBM.updateInDB(event);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @FXML
    private boolean deleteEvent() {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("Deleting this event will remove it from all other timelines as well.");
        confirmDelete.setContentText("Are you ok with this?");

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;

        try {
            if (this.event.getEventID() == 0)
                throw new IllegalArgumentException("event not in database");
            else {
                DBM.deleteFromDB(event);
                close();
            }
            //parentController.populateEventList();             //TODO fix updating the display on the event selector
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void addToTimeline() {
        parentController.activeTimeline.getEventList().add(event);
        try {
            if (event.addToTimeline(parentController.activeTimeline.getTimelineID()))
                System.out.println(event.getEventName() + " event added to " + parentController.activeTimeline + " timeline."); // remove this later once more user feedback is implemented
            else
                System.out.println(event.getEventName() + " is already on " + parentController.activeTimeline + " timeline.");
        } catch (SQLException e) {
            System.out.println("Timeline not found.");
        }
    }

    public void toggleStartExpanded(ActionEvent actionEvent) {
        startExpanded = !startExpanded;
        setExpansion(true, startExpanded);
    }

    public void toggleEndExpanded(ActionEvent actionEvent) {
        endExpanded = !endExpanded;
        setExpansion(false, endExpanded);
    }

    private int setExpansion(boolean start, boolean expanding) {
        FlowPane expandPane = start ? startPane : endPane;
        List<VBox> boxesToAddFrom = start ? startBoxes : endBoxes;
        expandPane.getChildren().removeAll(boxesToAddFrom);         //clear out the current contents except the expansion button
        int scale = parentController.activeTimeline.getScale();

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

    private boolean hasChanges() {
        Date readStart = new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue());   //milliseconds not implemented yet, do we need to?

        Date readEnd = new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue());

        return (
                !event.getEventName().equals(titleInput.getText())
                        || !event.getEventDescrition().equals(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n"))     //textArea tends to change the newline from \r\n to just \n which breaks some things
                        || event.getStartDate().compareTo(readStart) != 0
                        || event.getEndDate().compareTo(readEnd) != 0

        );
    }

    @FXML
    void close() {
        if (event != null && hasChanges())
            saveConfirm();        //do you wanna save and exit or just exit?
        parentController.rightSidebar.getChildren().remove(editor);
    }

*/
}