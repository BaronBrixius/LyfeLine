package controllers;

import com.google.gson.Gson;
import database.DBM;
import database.JSONTimeline;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class TimelineEditor extends Editor {
    private final ObservableList<String> keywords = FXCollections.observableArrayList();
    @FXML
    CheckBox zoom;
    @FXML
    HBox keywordBox;
    @FXML
    Button snapshotButton;
    @FXML
    Button exportButton;
    @FXML
    ComboBox<String> timeInput;
    @FXML
    Button addKeywordButton;
    @FXML
    Button removeKeywordButton;
    @FXML
    ListView<String> keywordView;
    @FXML
    Label feedbackText;
    @FXML
    private TextField keywordInput;
    private Timeline timeline;
    private File imageChosen;

    @Override
    public void initialize() {
        super.initialize();
        outPath = "src/main/resources/images/timeline/";

        toggleEditable(false);
        keywordView.setItems(keywords);

        timeInput.valueProperty().addListener(e -> {
            setExpansion(startPane, startBoxes, startExpanded, timeInput.getSelectionModel().getSelectedIndex() + 1);
            setExpansion(endPane, endBoxes, endExpanded, timeInput.getSelectionModel().getSelectedIndex() + 1);
        });

        // Get list of scales
        try {
            PreparedStatement state = DBM.conn.prepareStatement("SELECT unit FROM scale_lookup");
            timeInput.setItems(FXCollections.observableArrayList(DBM.getFromDB(state, rs -> rs.getString("unit"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        exportButton.setDisable(!GUIManager.loggedInUser.getAdmin());        //only admins can export

        GUIManager.mainStage.setTitle("Timeline Editor");
    }

    void setTimeline(Timeline timeline) {
        this.timeline = timeline;
        itemInEditor = timeline;
        // Check if Admin
        setOwner(GUIManager.loggedInUser.getID() == timeline.getOwnerID());
        populateDisplay();
    }

    @Override
    void toggleEditable(boolean editable) {
        super.toggleEditable(editable);

        keywordBox.setDisable(!editable);
        timeInput.setDisable(!editable);
    }

    @Override
    void populateDisplay() {
        super.populateDisplay(); // populate inputs common to editors

        if (timeline.getKeywords() != null) {
            keywords.clear();
            keywords.addAll(timeline.getKeywords());
            keywords.sort(String::compareTo);
        } else
            timeline.setKeywords(FXCollections.observableArrayList());
        timeInput.getSelectionModel().select(timeline.getScale() > 0 ? timeline.getScale() - 1 : 4);
    }

    @Override
    void updateItem() {
        super.updateItem(); // update variables common to TimelineObjects

        timeline.getKeywords().clear();
        timeline.getKeywords().addAll(keywords);

        timeline.setScale((timeInput.getSelectionModel().getSelectedIndex()) + 1);
        parentController.setActiveTimeline(timeline);
        parentController.eventSelectorController.populateTimelineList();
    }

    @FXML
    boolean deleteTimeline() {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("This will delete your timeline permanently!");
        confirmDelete.setContentText("Are you ok with this?");

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;

        try {
            DBM.deleteFromDB(timeline);
            GUIManager.swapScene("Dashboard");
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    boolean hasChanges() {
        if (super.hasChanges())
            return true;

        if (timeline.getKeywords().size() != keywords.size())
            return true;
        if (timeline.getScale() != timeInput.getSelectionModel().getSelectedIndex() + 1)
            return true;
        for (int i = 0; i < keywords.size(); i++)
            if (timeline.getKeywords().get(i).compareTo(keywords.get(i)) != 0)
                return true;

        return false;
    }

    boolean isUniqueKeyword(String k) {
        for (String s : keywords) {
            if (k.equalsIgnoreCase(s))
                return false;

        }
        return true;
    }

    @FXML
    void addKeyword() {
        String inputWord = keywordInput.getText();
        inputWord = inputWord.replace(",", " ");
        if (inputWord.isBlank()) {
            feedbackText.setText("Keyword cannot be empty!");
        } else {
            if (!isUniqueKeyword(inputWord)) {
                feedbackText.setText("Keyword already exists!");
            } else {
                keywords.add(inputWord);
                feedbackText.setText("Keyword " + inputWord + " added");
                keywords.sort(String::compareTo);
                keywordInput.setText("");
            }
        }
    }

    @FXML
    void removeKeyword() {
        if (keywordView.getSelectionModel().getSelectedIndex() < 0) {
            feedbackText.setText("No keyword selected!");
        } else {
            String removedWord = keywordView.getSelectionModel().getSelectedItem();
            keywords.remove(keywordView.getSelectionModel().getSelectedIndex());
            feedbackText.setText("Keyword " + removedWord + " removed!");
            keywordView.getSelectionModel().select(-1);
        }
    }

    @Override
    boolean save() {
        updateItem();
        super.save();
        parentController.populateDisplay();
        parentController.eventSelectorController.populateDisplay();
        parentController.eventSelectorController.setTimelineSelected(timeline);
        return true;
    }

    @Override
    String copyImage(File image, String filename) throws IOException { // Takes the file chosen and the name of it
        String imageName = filename;
        imageName = imageName.replaceAll("\\s", "_");
        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(image);
            // Path for saving, have special events folder now so if timeline guys are doing something they don't override copies
            int duplicateDigit = 2;

            while (folderHasImage(imageName)) {
                int indexOfDot = filename.lastIndexOf(".");
                if (imageName.matches(".*\\s\\(\\d\\)\\..*")) {
                    int indexOfBrackets = imageName.lastIndexOf("(");
                    imageName = imageName.substring(0, indexOfBrackets + 1) + duplicateDigit + ")" + "."
                            + getFormat(image);

                } else {
                    imageName = imageName.substring(0, indexOfDot) + duplicateDigit + "."
                            + imageName.substring(imageName.lastIndexOf(".") + 1);
                }
                duplicateDigit++;
            }

            os = new FileOutputStream(new File(outPath + imageName));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e);
        } finally {
            if (is != null)
                is.close();
            if (os != null)
                os.close();
        }
        return outPath + imageName;
    }

    // Method to check if the image folder has this name already to avoid duplicates overriding earlier uploads
    @Override
    boolean folderHasImage(String path) {
        File folder = new File("src/main/resources/images/timeline/");
        File[] listOfFiles = folder.listFiles();
        List<String> images = new ArrayList<>();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                images.add(file.getName());
            }
        }
        for (String s : images) {
            if (path.equalsIgnoreCase(s))
                return true;
        }
        return false;
    }

    @Override
    void uploadImage() throws IOException {
        boolean confirm = true;
        if (itemInEditor.getImagePath() != null) {
            confirm = ImageSaveConfirm();
        }
        if (confirm) {
            FileChooser chooser = new FileChooser(); // For the file directory
            chooser.setTitle("Upload image");

            // All the image formats supported by java.imageio
            // https://docs.oracle.com/javase/7/docs/api/javax/imageio/package-summary.html
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif",
                            "*.wbmp"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png"), new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                    new FileChooser.ExtensionFilter("GIF", "*.gif"), new FileChooser.ExtensionFilter("WBMP", "*.wbmp"));
            // The current image chosen by FileChooser
            imageChosen = chooser.showOpenDialog(GUIManager.mainStage);
        }

        if (imageChosen != null) {
            if (getFormat(imageChosen).matches("(JPEG|png|jpg|bmp|gif|wbmp)")) {
                if (checkResolution(imageChosen)) {
                    imageFilePath = copyImage(imageChosen, imageChosen.getName());
                    image.setImage(new Image("File:" + imageFilePath));
                } else {
                    ImageResolutionNotification();
                }
            } else {
                WrongFormatNotification();
            }
        }
    }

    @FXML
    private boolean ImageResolutionNotification() {
        Alert resolutionSaveImage = new Alert(Alert.AlertType.INFORMATION);
        resolutionSaveImage.setTitle("Too low resolution for timeline image");
        resolutionSaveImage.setHeaderText("Resolution of the picture is too low. Minimum resolution is 1280x720");

        Optional<ButtonType> result = resolutionSaveImage.showAndWait();
        return result.get() == ButtonType.OK;
    }

    // Check resolution implementation based on
    // http://bethecoder.com/applications/tutorials/java/image-io/how-to-get-image-width-height-and-format.html
    private boolean checkResolution(File file) throws IOException {
        boolean check = false;
        final int REQUIRED_HEIGHT = 576;
        final int REQUIRED_WIDTH = 1024;
        ImageInputStream iis = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        if (readers.hasNext()) {
            ImageReader reader = readers.next();
            reader.setInput(iis, true);
            check = reader.getWidth(0) >= REQUIRED_WIDTH && reader.getHeight(0) >= REQUIRED_HEIGHT;
        }
        return check;
    }

    @Override
    boolean validData() {
        if (timeInput.getSelectionModel().getSelectedIndex() >= 0)
            return super.validData();
        else {
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDelete.setTitle("Invalid Units");
            confirmDelete.setHeaderText("A time unit must be selected.");
            confirmDelete.setContentText("Make sure to selected a time unit appropriate for your timeline before saving.");

            confirmDelete.showAndWait();
            return false;
        }
    }

    @FXML
    void imageExport() throws IOException {
        Stage imageExport = new Stage();
        imageExport.setTitle("Export Image");
        imageExport.initOwner(GUIManager.mainStage);         //These two lines make sure you can't click back to the timeline window,
        imageExport.initModality(Modality.WINDOW_MODAL);     //so you can't have 10 windows open at once.

        FXMLLoader loader = new FXMLLoader(GUIManager.class.getResource("../FXML/ImageExport.fxml"));
        imageExport.setScene(new Scene(loader.load()));
        ImageExport imageExportObject = loader.getController();
        imageExportObject.setUp(parentController.snapshot(zoom.isSelected()), parentController.activeTimeline);

        imageExport.getScene().getStylesheets().addAll(GUIManager.mainStage.getScene().getStylesheets());
        imageExport.show();
    }

    @FXML
    void jsonExport() {
        FileChooser chooser = new FileChooser();                                                //open FileChooser for user to choose save location
        chooser.setTitle("Save Timeline as JSON");
        chooser.setInitialFileName(parentController.activeTimeline.getName());
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File outFile = chooser.showSaveDialog(GUIManager.mainStage);

        if (outFile == null)
            return;

        try {
            Gson gson = JSONTimeline.getGson();
            JSONTimeline exportable = new JSONTimeline(parentController.activeTimeline);        //gather all relevant information about a timeline into one object
            String outJSON = gson.toJson(exportable);                                     //convert that to JSON-formatted String

            FileUtils.writeStringToFile(outFile, outJSON, (Charset) null);                      //output

            Alert alert = new Alert(Alert.AlertType.INFORMATION);                               //inform user successful
            alert.setTitle("File Export");
            alert.setHeaderText("File has been successfully exported.");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
