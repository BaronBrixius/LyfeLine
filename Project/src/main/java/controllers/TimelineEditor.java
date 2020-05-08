package controllers;

import database.DBM;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        super.populateDisplay(timeline);    //populate inputs common to editors

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
        super.updateItem(timeline);     //update variables common to TimelineObjects

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
        if (super.hasChanges(timeline))
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
        super.save(timeline);
        parentController.populateDisplay();
        return true;
    }

    @Override
    void uploadImage() throws IOException {super.uploadImage(this.timeline);

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
}

