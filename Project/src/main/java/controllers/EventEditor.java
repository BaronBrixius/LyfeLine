package controllers;

import database.DBM;
import database.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import utils.Date;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class EventEditor {

    private final List<VBox> startBoxes = new ArrayList<>();
    private final List<Spinner<Integer>> startInputs = new ArrayList<>();
    private final List<VBox> endBoxes = new ArrayList<>();
    private final List<Spinner<Integer>> endInputs = new ArrayList<>();
    public ScrollPane editor;
    @FXML
    public Button editButton;
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
    public Button deleteImageButton;
    @FXML
    Button uploadImageButton;
    @FXML
    TextField titleInput = new TextField();
    @FXML
    TextArea descriptionInput = new TextArea();
    @FXML
    CheckBox hasDuration = new CheckBox();
    @FXML
    ComboBox<ImageView> imageInput = new ComboBox<>();
    @FXML
    ImageView image;
    @FXML
    Slider prioritySlider;
    boolean editable = true;
    TimelineView parentController;
    String filename; //THis is to take the name of the image choosen to add it to the copied version
    String fullOutPath; //When event is saved the path to the image in resource folder is sent here (the one we can use to send to DB)
    private boolean startExpanded;
    private boolean endExpanded;
    private Event event;
    private File imageChosen; //The current image chosen by FileChooser
    private	String tempLocation;
    

    public void initialize() {
//        image.setOnMouseEntered(e -> {
////            image.setScaleX(8);
////            image.setScaleY(8);
////            image.setScaleZ(8);
//        });
//        image.setOnMouseExited(e -> {
//            image.setScaleX(1);
//            image.setScaleY(1);
//            image.setScaleZ(1);
//        });

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
                case 4:             //intentional fallthrough
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
        } catch (SQLException e) {
            errorMessage.setText("Images could not be loaded");
        }
    }

    private void setupTimeInputBoxes(String timeSpinnerLabel, int maxValue, int i, List<Spinner<Integer>> spinnerList, List<VBox> boxList) {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxValue, 0);
        valueFactory.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                if (value == null)
                    return "0";
                return value.toString();
            }

            @Override
            public Integer fromString(String string) {
                try {
                    // If the specified value is null or zero-length, return null
                    if (string == null)
                        return 0;
                    string = string.trim();
                    if (string.length() < 1)
                        return null;
                    return Integer.parseInt(string);

                } catch (NumberFormatException ex) {
                    return 0;
                }
            }
        });
        spinnerList.add(i, new Spinner<>(valueFactory));
        spinnerList.get(i).setEditable(true);

        boxList.add(i, new VBox(new Label(timeSpinnerLabel), spinnerList.get(i)));
        boxList.get(i).setPrefWidth(70);
        boxList.get(i).getChildren().get(0).getStyleClass().add("smallText");
    }

    public void setParentController(TimelineView parentController) {             //TODO delete this inelegant solution
        this.parentController = parentController;
    }

    @FXML
    private void toggleHasDuration() {
        endPane.setDisable(!hasDuration.isSelected());
        setExpansion(endPane, endBoxes, hasDuration.isSelected() && endExpanded);   //compresses if disabled, if enabled leave it as user wanted
        if (hasDuration.isSelected())
            endPane.getStyleClass().remove("DisabledAnyways");
        else
            endPane.getStyleClass().add("DisabledAnyways");
    }

    public void saveEditButton() throws IOException {

//        //To save to DB
        if (editable && hasChanges())   //if unsaved changes, try to save
            if (!saveConfirm())         //if save cancelled, don't change mode
                return;
        toggleEditable(!editable);
        saveEvent();
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
        uploadImageButton.setVisible(editable);
        uploadImageButton.setDisable(!editable);
        deleteImageButton.setVisible(editable);
        deleteImageButton.setDisable(!editable);

        if (editable)
            editor.getStylesheets().remove("styles/DisabledViewable.css");
        else
            editor.getStylesheets().add("styles/DisabledViewable.css");

        editButton.setText(editable ? "Save" : "Edit");
    }


    @FXML
    private void uploadImage() throws IOException {    //Only working now for .jpg

        boolean confirm = true;

        if(event.getImagePath()!=null) {
            confirm = ImageSaveConfirm();
        }

        if(confirm) {
            FileChooser chooser = new FileChooser(); //For the filedirectory
            chooser.setTitle("Upload image");

            //All the image formats supported by java.imageio https://docs.oracle.com/javase/7/docs/api/javax/imageio/package-summary.html
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif", "*.wbmp"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                    new FileChooser.ExtensionFilter("GIF", "*.gif"),
                    new FileChooser.ExtensionFilter("WBMP", "*.wbmp")
            );
            this.imageChosen = chooser.showOpenDialog(GUIManager.mainStage);
            if (this.imageChosen != null) {

                image.setImage(new Image("File:" + imageChosen.getAbsolutePath()));

                filename = copyImage(imageChosen, imageChosen.getName());

                if (event.getImagePath() != null) {
                    try {
                        Files.deleteIfExists(Paths.get(event.getImagePath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                event.setImage(filename);
            }
        }
    }

    @FXML
    private boolean ImageSaveConfirm() {
        Alert confirmsaveimage = new Alert(Alert.AlertType.CONFIRMATION);
        confirmsaveimage.setTitle("Confirm Change");
        confirmsaveimage.setHeaderText("Replacing or removing an image will permanently delete it from the system.");
        confirmsaveimage.setContentText("Would you like to make the change?");

        Optional<ButtonType> result = confirmsaveimage.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        else
            return true;
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
        String imageName = filename;
        InputStream is = null;
        OutputStream os = null;
        //tempLocation = fullOutPath;
        System.out.println("the state of templocation in copy Image " + tempLocation);
        try {
            is = new FileInputStream(image);
            System.out.println("reading complete.");
            //Path for saving, have special events folder now so if timeline guys are doing something they don't override copies
            int duplicateDigit = 2;

            while (folderHasImage(imageName)) {
                int indexOfDot = filename.lastIndexOf(".");
                if (imageName.matches(".*\\s\\(\\d\\)\\..*")) {
                    int indexOfBrackets = imageName.lastIndexOf("(");
                    imageName = imageName.substring(0, indexOfBrackets + 1) + duplicateDigit + ")" + "." + getFormat(image);

                } else {
                    imageName = imageName.substring(0, indexOfDot) + " (" + duplicateDigit + ")" + "." + getFormat(image);
                }
                duplicateDigit++;
            }


            os = new FileOutputStream(new File(outPath + imageName));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            System.out.println("Writing complete.");
        } catch (IOException e) {
            System.out.println("Error: " + e);

        } finally {
            if (is != null)
                is.close();
            if (os != null)
                os.close();
        }
        return outPath + imageName;
    }

    //Method to check if the image folder has this name already to avoid if two are copied with same name the latter will just override the firs
    private boolean folderHasImage(String path) {
        File folder = new File("src/main/resources/images/");
        File[] listOfFiles = folder.listFiles();
        List<String> images = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                images.add(listOfFiles[i].getName());
            }
        }
        for (String s : images) {
            if (path.equalsIgnoreCase(s))
                return true;
        }
        return false;
    }

    public boolean setEvent(int eventID) {       //is this even needed? don't implement yet
        /*Event newEvent = logic to find Event in database and get its info
        if (newEvent != null)
            return changeEvent(newEvent);*/
        return false;
    }

    public boolean setEvent(Event event) {
        parentController.editorController.close();
        this.event = event;
        if (this.event.getEventID() == 0)       //if new event, set current user as owner
            this.event.setUserID(GUIManager.loggedInUser.getUserID());
        //Check if Owner
        boolean owner = GUIManager.loggedInUser.getUserID() == this.event.getUserID();
        editButton.setDisable(!owner);
        editButton.setVisible(owner);
        deleteButton.setDisable(!owner);
        deleteButton.setVisible(owner);

        return populateDisplay();
    }

    private boolean populateDisplay() {
        titleInput.setText(event.getEventName());
        descriptionInput.setText(event.getEventDescrition());

        System.out.println(event.getImagePath());
        System.out.println(event.getEventID());
        if (event.getImagePath() != null) {
            image.setImage(new Image("File:" + event.getImagePath()));
            this.fullOutPath = event.getImagePath();
        }
        else 
        	image.setImage(null);

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

        setExpansion(startPane, startBoxes, false);
        setExpansion(endPane, endBoxes, false);
        return true;
    }

    @FXML
    private boolean saveConfirm() {
        Alert confirmsave = new Alert(Alert.AlertType.CONFIRMATION);
        confirmsave.setTitle("Confirm Save");
        confirmsave.setHeaderText("Saving changes to this event will alter it for all other timelines as well.");
        confirmsave.setContentText("Would you like to save?");

        Optional<ButtonType> result = confirmsave.showAndWait();
        if (result.get() == ButtonType.CANCEL)
            return false;
        return saveEvent();
    }

    void updateEvent() {
        //setters to update each field of this.event, based on the current info in the text fields

        event.setTitle(titleInput.getText());
        event.setDescription(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n"));
        event.setStartDate(new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue()));
        if (hasDuration.isSelected()) {
            event.setEndDate(new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                    endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue()));
        } else                //if it has no duration, end = start
            setEndEqualsStart();
    }

    private void setEndEqualsStart() {
        event.setEndDate(event.getStartDate());
        endInputs.get(0).getValueFactory().setValue(event.getEndDate().getYear());
        endInputs.get(1).getValueFactory().setValue(event.getEndDate().getMonth());
        endInputs.get(2).getValueFactory().setValue(event.getEndDate().getDay());
        endInputs.get(3).getValueFactory().setValue(event.getEndDate().getHour());
        endInputs.get(4).getValueFactory().setValue(event.getEndDate().getMinute());
        endInputs.get(5).getValueFactory().setValue(event.getEndDate().getSecond());
        endInputs.get(6).getValueFactory().setValue(event.getEndDate().getMillisecond());
    }

    private boolean saveEvent() {
        updateEvent();
        try {
            if (event.getEventID() == 0) {
                DBM.insertIntoDB(event);//Save button clicked, the image chosen is saved and the String field is set as the path to the image in the resource folder
                addToTimeline();        //new event is automatically added to active timeline when saved
            } else
                DBM.updateInDB(event);//Save button clicked, the image chosen is saved and the String field is set as the path to the image in the resource folder
            parentController.selectorController.populateTimelineList();
            parentController.selectorController.populateEventList();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void addToTimeline() {
        parentController.activeTimeline.getEventList().add(event);
        try {
            if (event.addToTimeline(parentController.activeTimeline.getTimelineID()))
                System.out.println("Event added to " + parentController.activeTimeline + " timeline."); // remove this later once more user feedback is implemented
            else
                System.out.println("Event is already on " + parentController.activeTimeline + " timeline.");
        } catch (SQLException e) {
            System.out.println("Timeline not found.");
        }
    }

    @FXML
    private boolean deleteEvent() {
        parentController.selectorController.deleteEvent(event);
        return close();
    }

    public void toggleStartExpanded(ActionEvent actionEvent) {
        startExpanded = !startExpanded;
        setExpansion(startPane, startBoxes, startExpanded);
    }

    public void toggleEndExpanded(ActionEvent actionEvent) {
        endExpanded = !endExpanded;
        setExpansion(endPane, endBoxes, endExpanded);
    }

    private int setExpansion(FlowPane expandPane, List<VBox> boxesToAddFrom, boolean expanding) {
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

    boolean hasChanges() {
        if (!event.getEventName().equals(titleInput.getText())
                || !event.getEventDescrition().equals(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n")))     //textArea tends to change the newline from \r\n to just \n which breaks some things)
            return true;

        Date readStart = new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
                startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue());   //milliseconds not implemented yet, do we need to?

        Date readEnd = new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
                endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue());

        return (
                event.getStartDate().compareTo(readStart) != 0
                        || event.getEndDate().compareTo(readEnd) != 0
        );
    }

    @FXML
    boolean close() {
        parentController.rightSidebar.getChildren().remove(editor);
        parentController.rightSidebar.getChildren().add(editor);
        if (event != null && hasChanges() && !saveConfirm())          //do you wanna save and exit or just exit?
            return false;
        parentController.rightSidebar.getChildren().remove(editor);
        return true;
    }


    
    public void clearImage(ActionEvent actionEvent) {
        if (event.getImagePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(event.getImagePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.setImage(null);
            image.setImage(null);
            updateEvent();
        }

    }
}