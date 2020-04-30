package controllers;

import database.DBM;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Dashboard {

    @FXML
    private Button eventEditorButton;
    @FXML
    private Button adminGUI;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnCreate;
    @FXML
    private TextFlow displayInfo;
    @FXML
    private ListView<Timeline> list;
    @FXML
    private TextField searchInput;
    @FXML
    private CheckBox cbOnlyViewPersonalLines;
    @FXML
    private ComboBox<String> sortBy;
    @FXML
    private GridPane gridButtons;
    @FXML
    private Text titleText;
    List<Timeline> timelines;


    private Timeline activeTimeline;

    public void initialize() {
        //TODO fix this to be cleaner, I did it as a last second thing because it used to prevent nonadmins from even viewing anything
        btnCreate.setVisible(GUIManager.loggedInUser.getAdmin());
        btnCreate.setDisable(!GUIManager.loggedInUser.getAdmin());
        btnEdit.setVisible(GUIManager.loggedInUser.getAdmin());
        btnEdit.setDisable(!GUIManager.loggedInUser.getAdmin());
        btnDelete.setVisible(GUIManager.loggedInUser.getAdmin());
        btnDelete.setDisable(!GUIManager.loggedInUser.getAdmin());
        adminGUI.setVisible(GUIManager.loggedInUser.getAdmin());
        adminGUI.setDisable(!GUIManager.loggedInUser.getAdmin());

        // Fill ListView with the timelines
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
            timelines = DBM.getFromDB(stmt, new Timeline());
            list.setItems(FXCollections.observableArrayList(timelines));
        } catch (SQLException e) {
            System.err.println("Could not get timelines from database.");
        }

        // approach adapted from https://stackoverflow.com/a/36657553
        list.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Timeline item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        // Add sorting options
        ObservableList<String> sortOptions = FXCollections.observableArrayList();
        sortOptions.add("Alphabetically");
        sortOptions.add("Reverse-Alphabetically");
        sortOptions.add("Most Recent");
        sortOptions.add("Oldest");
        sortBy.setItems(sortOptions);

        // Sort order selection events
        sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> {
            switch (sortBy.getSelectionModel().getSelectedIndex()) {
                case 0:
                    list.getItems().sort(Comparator.comparing(Timeline::getName));
                    break;
                case 1:
                    list.getItems().sort((t1, t2) -> (t2.getName().compareTo(t1.getName())));
                    break;
                case 2:
                    list.getItems().sort((t1, t2) -> (t2.getDateCreated().compareTo(t1.getDateCreated())));
                    break;
                case 3:
                    list.getItems().sort(Comparator.comparing(Timeline::getDateCreated));
                    break;
            }
        });

        // Initialised sorting
        sortBy.getSelectionModel().select(0);

        // Search field
        searchInput.focusedProperty().addListener(ov -> {
            if (searchInput.isPressed())
                searchInput.setText("");
                searchTimelines();


        });

        list.getSelectionModel().selectedIndexProperty().addListener(e -> {
            activeTimeline = list.getSelectionModel().getSelectedItem();
            updateDisplays();
        });

        titleText.setText("Select a Timeline.");
    }

    @FXML
    public void adminScreen() throws IOException {
        GUIManager.swapScene("AdminRoleManager");
    }


    @FXML
    public void searchTimelines() {
            searchInput.setOnKeyPressed(keyEvent -> {
                  String[] inputs = searchInput.getText().trim().split("\\s++");
                List<Timeline> templist = new ArrayList<>();
                  for(int i = 0; i<timelines.size(); i++){
                      for(int j = 0; j<inputs.length;j++){
                          String toFind = inputs[j];
                          boolean found = Arrays.asList(timelines.get(i).getKeywords().toArray()).stream().anyMatch(s -> s.toString().toLowerCase().contains( toFind.toLowerCase()));
                          if(found){
                              if(!templist.contains(timelines.get(i)))
                              templist.add(timelines.get(i));}
                      }
                      list.setItems(FXCollections.observableArrayList(templist));
                      if (searchInput.getText().equalsIgnoreCase(""))
                          list.setItems(FXCollections.observableArrayList(timelines));
                  }
            });
    }

    @FXML
    public void onlyUserTimelines() {

        if (cbOnlyViewPersonalLines.isSelected()) {
            try {
                PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineOwner = ?");
                stmt.setInt(1, GUIManager.loggedInUser.getUserID()); // GUIManager.loggedInUser.getUserID() uncomment
                // this for real version
                list.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
            } catch (SQLException e) {
                System.err.println("Could not get timelines from database.");
            }
        } else {
            try {
                PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
                list.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
                sortBy.getSelectionModel().select(0);
            } catch (SQLException e) {
                System.err.println("Could not get timelines from database.");
            }
        }

    }

    @FXML
    public void createTimeline() {
        Timeline t = new Timeline();
        t.setTimelineOwner(GUIManager.loggedInUser.getUserID());
        openTimelineView(t);
    }

    @FXML
    public void editTimeline() {
        if (activeTimeline != null) {
            openTimelineView(this.activeTimeline);
        }
    }

    @FXML
    public void openTimeline() {
        openTimelineView(list.getSelectionModel().getSelectedItem());
    }

    private void openTimelineView(Timeline newActiveTimeline) {
        try {
            TimelineView timelineView = GUIManager.swapScene("TimelineView");
            timelineView.setActiveTimeline(newActiveTimeline);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // open DeletePopUp
    @FXML
    public void deleteConfirmation(ActionEvent event) throws IOException {

        Stage delConfirm = new Stage();
        delConfirm.setTitle("Confirm Deletion");
        delConfirm.initOwner(GUIManager.mainStage);

        delConfirm.initModality(Modality.WINDOW_MODAL);
        delConfirm.setResizable(false);

        FXMLLoader popupDeletion = new FXMLLoader(GUIManager.class.getResource("../FXML/Popup.fxml"));
        VBox popup = popupDeletion.load();
        popup.getStylesheets().add(GUIManager.mainStage.getScene().getStylesheets().get(0));
        delConfirm.setScene(new Scene(popup));

        Popup deletionPopup = popupDeletion.getController();
        deletionPopup.setMode(1);
        if (list.getSelectionModel().getSelectedItem() != null && list.getSelectionModel().getSelectedItem().getTimelineOwnerID() == GUIManager.loggedInUser.getUserID()) {
            titleText.setText("");
            deletionPopup.setList(list);
            deletionPopup.setDisplayTxt(
                    "Are you sure you want to delete " + list.getSelectionModel().getSelectedItem().getName() + "?");
            delConfirm.show();

        }
    }

    @FXML
    private void updateDisplays() {
        if (list.getSelectionModel().getSelectedItem() != null) {
            if (list.getSelectionModel().getSelectedItem().getTimelineOwnerID() == GUIManager.loggedInUser.getUserID()) {
                btnDelete.setDisable(false);
                btnEdit.setDisable(false);
            } else {
                btnDelete.setDisable(true);
                btnEdit.setDisable(true);
            }

            Timeline timeline = list.getSelectionModel().getSelectedItem();

            int year = timeline.getDateCreated().getYear();
            int month = timeline.getDateCreated().getMonth();
            int day = timeline.getDateCreated().getDay();

            StringBuilder keyWords = new StringBuilder();
            for (String s : timeline.getKeywords())
                keyWords.append(s + ", ");
            keyWords.delete(keyWords.length() - 2, keyWords.length());

            titleText.setText("Title: " + timeline.getName()
                    + "\nDescription: " + timeline.getTimelineDescription()
                    + "\nDate Created: " + year + "/" + month + "/" + day
                    + "\nKeywords: " + keyWords);

        } else {
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
            titleText.setText("Select a Timeline.");
        }
    }
}
