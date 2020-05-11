package controllers;

import database.DBM;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TimelineEditor extends Editor {
    public Timeline timeline;
    @FXML
    ComboBox<String> timeInput;
    @FXML
    Button addKeywordButton;
    @FXML
    Button removeKeywordButton;
    @FXML
    ListView<String> keywordView;
    @FXML
    Text feedbackText;
    @FXML
    private TextField keywordInput;
    private final ObservableList<String> keywords = FXCollections.observableArrayList();

    public void initialize() {
        super.initialize();

        toggleEditable(false);
        keywordView.setItems(keywords);

        timeInput.valueProperty().addListener(e -> {
                    setExpansion(startPane, startBoxes, startExpanded, timeInput.getSelectionModel().getSelectedIndex() + 1);
                    setExpansion(endPane, endBoxes, endExpanded, timeInput.getSelectionModel().getSelectedIndex() + 1);
                }
        );

        //Get list of scales
        try {
            PreparedStatement state = DBM.conn.prepareStatement("SELECT unit FROM scale_lookup");
            timeInput.setItems(FXCollections.observableArrayList(DBM.getFromDB(state, rs -> rs.getString("unit"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    boolean setTimeline(Timeline timeline) {
        this.timeline = timeline;
        itemInEditor = timeline;
        //Check if Admin
        setOwner(GUIManager.loggedInUser.getUserID() == timeline.getOwnerID());
        return populateDisplay();
    }

    void toggleEditable(boolean editable) {
        super.toggleEditable(editable);
        keywordInput.setEditable(editable);
        timeInput.setDisable(!editable);
    }

    boolean populateDisplay() {
        super.populateDisplay();    //populate inputs common to editors

        if (timeline.getKeywords() != null) {
            keywords.clear();
            keywords.addAll(timeline.getKeywords());
            keywords.sort(String::compareTo);
        } else
            timeline.setKeywords(FXCollections.observableArrayList());

        timeInput.getSelectionModel().select(timeline.getScale() - 1);

        return true;
    }

    void updateItem() {
        super.updateItem();     //update variables common to TimelineObjects

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

    boolean save() {
        updateItem();
        super.save();
        parentController.populateDisplay();
        return true;
    }

    boolean isUniqueKeyword(String k) {
        for (String s : keywords) {
            if (k.equalsIgnoreCase(s)) return false;

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
    protected String copyImage(File image, String filename) throws IOException { //Takes the file chosen and the name of it
        String outPath = "src/main/resources/images/timeline/";
        String imageName = filename;
        InputStream is = null;
        OutputStream os = null;

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
    @Override
    protected boolean folderHasImage(String path) {
        File folder = new File("src/main/resources/images/timeline/");
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
}

