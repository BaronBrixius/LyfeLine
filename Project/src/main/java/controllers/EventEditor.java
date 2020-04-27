package controllers;
import database.DBM;
import database.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import utils.Date;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class EventEditor {

    @FXML public GridPane editor;
    @FXML public Button editButton;
    @FXML public Button uploadButton;
    @FXML public Button deleteButton;

    @FXML public HBox startTime;
    @FXML public HBox endTime;
    @FXML public Spinner<Integer> startTime1;
    @FXML public Spinner<Integer> startTime2;
    @FXML public Spinner<Integer> startTime3;
    @FXML public Spinner<Integer> endTime1;
    @FXML public Spinner<Integer> endTime2;
    @FXML public Spinner<Integer> endTime3;

    @FXML public Label headerText;
    @FXML public Text errorMessage;
    @FXML TextField titleInput = new TextField();
    @FXML TextArea descriptionInput = new TextArea();
    @FXML DatePicker startDate = new DatePicker();
    @FXML CheckBox hasDuration = new CheckBox();@FXML DatePicker endDate = new DatePicker(); //only a datepicker for skeleton, will figure best way to enter info later
    @FXML ImageView image;

    int startYear;

    boolean editable = true;
    EventSelector prevScreen;
    private Event event;
    private File imageChosen; //The current image chosen by FileChooser
    private String filename ; //THis is to take the name of the image choosen to add it to the copied version
    private  String fullOutPath; //When event is saved the path to the image in resource folder is sent here (the one we can use to send to DB)





    public void setPrevScreen(EventSelector prevScreen) {             //TODO delete this inelegant solution
        this.prevScreen = prevScreen;
    }

    public void initialize() {
        if (
                GUIManager.loggedInUser == null ||          //TODO delete this when hooked up to rest of program
                        !GUIManager.loggedInUser.getAdmin()) {
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
    }

    @FXML
    private void toggleHasDuration() {
        endDate.setDisable(!hasDuration.isSelected());
        endTime.setDisable(!hasDuration.isSelected());
    }

    public void saveEditButton() throws IOException {      //I know this is ugly right now
        LocalDate start;
        LocalDate end;
        Date readStart = new Date();
        Date readEnd = new Date();

        if(image.isDisable()==false){//So we do not copy when edit is pressed/only save is pressed
            if(this.filename !=null) {//Only keep on with copy if there has been a chosen image
                if(this.event.getEventID() == 0) //if a new image all good copy
                    this.fullOutPath = copyImage(imageChosen,filename);
                else if (!this.event.getImagePath().equalsIgnoreCase(this.fullOutPath))
                    this.fullOutPath = copyImage(imageChosen,filename);}}

        //To save to DB
        this.event.setImage(this.fullOutPath);
        System.out.println(this.fullOutPath);

        try {
            //Date Picker is literally bugged, this line works around it.
            startDate.setValue(startDate.getConverter().fromString(startDate.getEditor().getText()));
            //Convert the Date Picker to Date and see if problems happen
            start = startDate.getValue();
            readStart = new Date(start.getYear(), start.getMonth().getValue(), start.getDayOfMonth(),
                    startTime1.getValue(), startTime2.getValue(), startTime3.getValue(), event.getStartDate().getMilliseconds());   //milliseconds not implemented yet, do we need to?
        } catch (NullPointerException e) {
            errorMessage.setText("Start date can't be empty.");
            return;
        } catch (DateTimeParseException d) {
            errorMessage.setText("Start date's format is improper.");
            return;
        }

        //If the End Date is selected, check it for problems too.
        if (hasDuration.isSelected()) {
            try {
                endDate.setValue(endDate.getConverter().fromString(endDate.getEditor().getText()));
                end = endDate.getValue();
                readEnd = new Date(end.getYear(), end.getMonth().getValue(), end.getDayOfMonth(), endTime1.getValue(), endTime2.getValue(), endTime3.getValue(), 0);

            } catch (NullPointerException e) {
                errorMessage.setText("End date can't be empty if selected.");
                return;
            } catch (DateTimeParseException d) {
                errorMessage.setText("End date's format is improper.");
                return;
            }
        }

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

        startDate.setDisable(!editable);
        startTime1.setDisable(!editable);
        startTime2.setDisable(!editable);
        startTime3.setDisable(!editable);

        endDate.setEditable(editable);
        endTime1.setEditable(editable);
        endTime2.setEditable(editable);
        endTime3.setEditable(editable);
        endTime1.setDisable(!editable);
        endTime2.setDisable(!editable);
        endTime3.setDisable(!editable);

        image.setDisable(!editable);
        uploadButton.setVisible(editable);
        uploadButton.setDisable(!editable);

        if (editable)
            editor.getStylesheets().removeAll("styles/DisabledEditing.css");
        else
            editor.getStylesheets().add("styles/DisabledEditing.css");

        editButton.setText(editable ? "Save" : "Edit");
    }


    @FXML
    private void uploadImage() throws IOException {    //Only working now for .jpg
        FileChooser chooser = new FileChooser(); //For the filedirectory
        if (event.getImagePath() == null)
        	chooser.setTitle("Upload image");
        else
        	chooser.setTitle("Update image");
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
        this.imageChosen = chooser.showOpenDialog(GUIManager.mainStage); //This is the stage that needs to be edited (ok,cancel button) for the filechooser... do in FXML ?
        if (event.getImagePath() == null){
        	this.filename = imageChosen.getName(); //THis is to take the name of the image choosen to add it to the copied version
            System.out.println(this.imageChosen.getAbsolutePath());
            image.setImage(new Image("File:" + this.imageChosen.getAbsolutePath()));
            System.out.println("img W/o previous");
        }
        else if (ImageSaveConfirm() || event.getImagePath() != null) {
        	this.filename = imageChosen.getName(); //THis is to take the name of the image choosen to add it to the copied version
            System.out.println(this.imageChosen.getAbsolutePath());
            image.setImage(new Image("File:" + this.imageChosen.getAbsolutePath()));
            System.out.println("img is in db");
        }
        
        else
        System.out.println("Cancel Button pressed.");
    }
    
    @FXML
    private boolean ImageSaveConfirm() throws IOException {
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
        String imageName ="";
        try{
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(image);
            System.out.println("reading complete.");
             imageName = filename;

            //Path for saving, have special events folder now so if timeline guys are doing something they don't override copies
            int duplicateDigit = 2;

            while(folderHasImage(imageName)) {
                int indexOfDot = filename.lastIndexOf(".");
                if(imageName.matches(".*\\s\\(\\d\\)\\..*")) {
                    int indexOfBrackets = imageName.lastIndexOf("(");
                    imageName = imageName.substring(0, indexOfBrackets + 1) +  duplicateDigit + ")" + "." + getFormat(image);

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

        return false;
    }

    public boolean setEvent(Event event) {
        this.event = event;
        return populateDisplay();
    }

    private boolean populateDisplay() {
        titleInput.setText(event.getEventName());
        descriptionInput.setText(event.getEventDescrition());
        //fullOutPath=event.getImagePath();
        startDate.setValue(LocalDate.of(event.getStartDate().getYear(), event.getStartDate().getMonth(), event.getStartDate().getDay()));

        startTime1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, event.getStartDate().getHours()));
        startTime2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getStartDate().getMinutes()));
        startTime3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getStartDate().getSeconds()));

        if (event.getStartDate().compareTo(event.getEndDate()) == 0) {
            endDate.setValue(LocalDate.of(event.getStartDate().getYear(), event.getStartDate().getMonth(), event.getStartDate().getDay()));
            endTime1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, event.getStartDate().getHours()));
            endTime2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getStartDate().getMinutes()));
            endTime3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getStartDate().getSeconds()));
        } else {
            hasDuration.setSelected(true);
            toggleHasDuration();
            endDate.setValue(LocalDate.of(event.getEndDate().getYear(), event.getEndDate().getMonth(), event.getEndDate().getDay()));
            endTime1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, event.getEndDate().getHours()));
            endTime2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getEndDate().getMinutes()));
            endTime3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getEndDate().getSeconds()));
        }


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

        LocalDate start = startDate.getValue();
        event.setStartDate(new Date(start.getYear(), start.getMonth().getValue(), start.getDayOfMonth(),
                startTime1.getValue(), startTime2.getValue(), startTime3.getValue(), event.getStartDate().getMilliseconds()));  //milliseconds not implemented yet, do we need to?


        LocalDate end;
        if (hasDuration.isSelected()) {
            end = endDate.getValue();
            event.setEndDate(new Date(end.getYear(), end.getMonth().getValue(), end.getDayOfMonth(),
                    endTime1.getValue(), endTime2.getValue(), endTime3.getValue(), event.getEndDate().getMilliseconds()));      //milliseconds not implemented yet, do we need to?
        } else                //if it has no duration, end = start
            event.setEndDate(event.getStartDate());


    }

    private boolean saveEvent() throws IOException {
        updateEvent();
        try {
            if (event.getEventID() == 0) {
                //Save button clicked, the image chosen is saved and the String field is set as the path to the image in the resource folder
                DBM.insertIntoDB(event);
                event.addToTimeline(prevScreen.timelineList.getSelectionModel().getSelectedItem().getTimelineID());
                prevScreen.populateEventList();             //TODO delete this inelegant solution
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
            prevScreen.populateEventList();             //TODO delete this inelegant solution
            return true;
        } catch (SQLException | IOException e) {
            return false;
        }
    }

    private boolean hasChanges() {
        LocalDate start = startDate.getValue();
        Date readStart = new Date(start.getYear(), start.getMonth().getValue(), start.getDayOfMonth(),
                startTime1.getValue(), startTime2.getValue(), startTime3.getValue(), event.getStartDate().getMilliseconds());   //milliseconds not implemented yet, do we need to?

        //If end is null, set end equal to start
        LocalDate end = endDate.getValue();
        Date readEnd = new Date(end.getYear(), end.getMonth().getValue(), end.getDayOfMonth(), endTime1.getValue(), endTime2.getValue(), endTime3.getValue(), event.getEndDate().getMilliseconds());

        return (
                !event.getEventName().equals(titleInput.getText())
                        || !event.getEventDescrition().equals(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n"))     //textArea tends to change the newline from \r\n to just \n which breaks some things
                        || event.getStartDate().compareTo(readStart) != 0
                        || event.getEndDate().compareTo(readEnd) != 0

        );
    }

    @FXML
    private void close() throws IOException {
        if (hasChanges())
            saveConfirm();        //do you wanna save and exit or just save?
        GUIManager.previousPage();        //close editor, return to previous screen
    }

}