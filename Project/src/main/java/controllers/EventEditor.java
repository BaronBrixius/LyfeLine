package controllers;

import database.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class EventEditor extends Editor {

    @FXML
    Button deleteImageButton;
    @FXML
    Button uploadImageButton;
    @FXML
    CheckBox hasDuration = new CheckBox();
    @FXML
    ComboBox<ImageView> imageInput = new ComboBox<>();
    @FXML
    ImageView image;
    @FXML
    Slider prioritySlider;
    Event event;
    private String tempLocation;

    public void initialize() {
        super.initialize();

        //set up priority slider labels
        prioritySlider.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double n) {
                if (n < 0.5) return "Not set";
                if (n < 1.5) return "Low";
                if (n < 2.5) return "Medium";
                if (n < 3.5) return "High";

                return "Not set";
            }

            //probably not used but required for the override
            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "Low":
                        return 1d;
                    case "Medium":
                        return 2d;
                    case "High":
                        return 3d;
                    default:
                        return 0d;
                }
            }
        });
    }

    @FXML
    void toggleHasDuration() {              //toggles whether the event has a distinct end date, as opposed to being instant
        endPane.setDisable(!hasDuration.isSelected());
        setExpansion(endPane, endBoxes, hasDuration.isSelected() && endExpanded, parentController.activeTimeline.getScale());   //compresses if duration is disabled, if enabled leave it as user wanted

        if (hasDuration.isSelected()) {
            populateEndInputs(event);
            endPane.getStyleClass().remove("DisabledAnyways");
        } else {
            for (int i = 0; i < 7; i++) {
                endInputs.get(i).getValueFactory().setValue(startInputs.get(i).getValue());
            }
            endPane.getStyleClass().add("DisabledAnyways");
        }
    }

    void toggleEditable(boolean editable) {
        super.toggleEditable(editable);
        uploadImageButton.setDisable(!editable);
        deleteImageButton.setDisable(!editable);
        hasDuration.setDisable(!editable);
        prioritySlider.setDisable(!editable);
    }

    @FXML
    private void uploadImage() throws IOException {    //Only working now for .jpg
        boolean confirm = true;

        if (event.getImagePath() != null) {
            confirm = ImageSaveConfirm();
        }

        if (confirm) {
            FileChooser chooser = new FileChooser(); //For the file directory
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
            //The current image chosen by FileChooser
            File imageChosen = chooser.showOpenDialog(GUIManager.mainStage);
            if (imageChosen != null) {

                image.setImage(new Image("File:" + imageChosen.getAbsolutePath()));

                //THis is to take the name of the image chosen to add it to the copied version
                String filename = copyImage(imageChosen, imageChosen.getName());

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
        Alert confirmSaveImage = new Alert(Alert.AlertType.CONFIRMATION);
        confirmSaveImage.setTitle("Confirm Change");
        confirmSaveImage.setHeaderText("Replacing or removing an image will permanently delete it from the system.");
        confirmSaveImage.setContentText("Would you like to make the change?");

        Optional<ButtonType> result = confirmSaveImage.showAndWait();

        return result.get() == ButtonType.OK;
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

    //Method to check if the image folder has this name already to avoid duplicates overriding earlier uploads
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

    boolean setEvent(Event event) {
        parentController.eventEditorController.close();
        this.event = event;
        if (this.event.getID() == 0)       //if new event, set current user as owner
            this.event.setOwnerID(GUIManager.loggedInUser.getUserID());
        setOwner(GUIManager.loggedInUser.getUserID() == this.event.getOwnerID());
        return populateDisplay();
    }

    boolean populateDisplay() {
        super.populateDisplay(event);    //populate inputs common to editors

        if (event.getImagePath() != null) {
            image.setImage(new Image("File:" + event.getImagePath()));
            //When event is saved the path to the image in resource folder is sent here (the one we can use to send to DB)
            String fullOutPath = event.getImagePath();
        } else
            image.setImage(null);

        if (event.getStartDate().compareTo(event.getEndDate()) != 0) {
            hasDuration.setSelected(true);
            toggleHasDuration();
        }

        prioritySlider.setValue(event.getEventPriority());

        return true;
    }

    void updateItem() {                 //sets object's values based on input fields' values
        super.updateItem(event);        //update variables common to TimelineObjects
        event.setEventPriority((int) prioritySlider.getValue());
    }

    boolean save() {
        updateItem();
        boolean newEvent = event.getID() == 0;

        super.save(event);          //adds to database

        if (newEvent)
            addToTimeline();        //new event is automatically added to active timeline when saved
        parentController.eventSelectorController.populateDisplay();
        parentController.populateDisplay();
        return true;
    }

    private void addToTimeline() {
        parentController.activeTimeline.getEventList().add(event);
        try {
            if (event.addToTimeline(parentController.activeTimeline.getID()))
                System.out.println("Event added to " + parentController.activeTimeline + " timeline."); // remove this later once more user feedback is implemented
            else
                System.out.println("Event is already on " + parentController.activeTimeline + " timeline.");
        } catch (SQLException e) {
            System.out.println("Timeline not found in database.");
        }
    }

    @FXML
    boolean deleteEvent() {
        parentController.eventSelectorController.deleteEvent(event);
        return close();
    }

    boolean hasChanges() {
        if (!hasDuration.isSelected() && event.getStartDate().compareTo(event.getEndDate()) != 0)
            return true;
        if (super.hasChanges(event))
            return true;
        return event.getEventPriority() != prioritySlider.getValue();
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

    @FXML
    void clearImage() {
        if (event.getImagePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(event.getImagePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.setImage(null);
            image.setImage(null);
            updateItem();
        }

    }
}