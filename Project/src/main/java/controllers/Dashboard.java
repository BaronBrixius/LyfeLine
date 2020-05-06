package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Dashboard {


    @FXML protected Button timelineViewButton;
    @FXML protected Button adminGUI;
    @FXML protected Button btnDelete;
    @FXML protected Button btnEdit;
    @FXML protected Button btnCreate;
    @FXML protected TextFlow displayInfo;
    @FXML protected ListView<Timeline> list;
    @FXML protected TextField searchInput;
    @FXML protected CheckBox cbOnlyViewPersonalLines;
    @FXML protected ComboBox<String> sortBy;
    @FXML protected GridPane gridButtons;
    @FXML protected Text titleText;
    private List<Timeline> timelines;
    private List<Timeline> userTimelines;
    private Timeline activeTimeline;

    public void initialize() {
        //TODO fix this to be cleaner, I did it as a last second thing because it used to prevent nonadmins from even viewing anything
        btnCreate.setVisible(GUIManager.loggedInUser.getAdmin());
        btnCreate.setDisable(!GUIManager.loggedInUser.getAdmin());
        btnEdit.setVisible(GUIManager.loggedInUser.getAdmin());
        btnEdit.setDisable(list.getSelectionModel().isEmpty()
                || list.getSelectionModel().getSelectedItem().getOwnerID() != GUIManager.loggedInUser.getUserID());
        btnDelete.setVisible(GUIManager.loggedInUser.getAdmin());
        btnDelete.setDisable(list.getSelectionModel().isEmpty()
                || list.getSelectionModel().getSelectedItem().getOwnerID() != GUIManager.loggedInUser.getUserID());
        adminGUI.setVisible(GUIManager.loggedInUser.getAdmin());
        adminGUI.setDisable(!GUIManager.loggedInUser.getAdmin());
        timelineViewButton.setDisable(true);

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
        sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> sortTimelines());

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

    public void sortTimelines() {
        switch (sortBy.getSelectionModel().getSelectedIndex()) {
            case 0:
                list.getItems().sort((t1, t2) -> (t1.getName().compareToIgnoreCase(t2.getName())));
                break;
            case 1:
                list.getItems().sort((t1, t2) -> (t2.getName().compareToIgnoreCase(t1.getName())));
                break;
            case 2:
                list.getItems().sort((t1, t2) -> (t2.getCreationDate().compareTo(t1.getCreationDate())));
                break;
            case 3:
                list.getItems().sort(Comparator.comparing(Timeline::getCreationDate));
                break;
        }
    }

    @FXML
    public void adminScreen() throws IOException {
        GUIManager.swapScene("AdminRoleManager");
    }


    @FXML
    public void searchTimelines() {
            searchInput.setOnKeyReleased(keyEvent -> {//Each time new key is pressed
                  String[] inputs = searchInput.getText().trim().split("\\s++"); //String is updated by the newest textfield read, if spaces the strings are split up into "string keywords" for search l
                List<Timeline> templist = new ArrayList<>(); //List of timelines that fullfill the textfield input string - used to fill the ListView of timelines
                if (cbOnlyViewPersonalLines.isSelected()){ onlyUserTimelines(); //If only search user's timelines
                    for(int i = 0; i<userTimelines.size(); i++){ //go trough all the current user's timelines in the database
                        for(int j = 0; j<inputs.length;j++){//No check all the search words used if they are to be found anywhere as keywords
                            String toFind = inputs[j]; //while a keyword is just one letter i.e. "f" if a keyword in timeline has that letter then it will be shown (instant search feature)
                            boolean found = Arrays.asList(userTimelines.get(i).getKeywords().toArray()).stream().anyMatch(s -> s.toString().toLowerCase().contains( toFind.toLowerCase()));
                            if(found){
                                if(!templist.contains(userTimelines.get(i))) //if the timline has not already been associated with this search then add it to the temporary timelinelist
                                    templist.add(userTimelines.get(i));}
                        }
                        list.setItems(FXCollections.observableArrayList(templist)); //populate the ListView with the timelines that fulfill the search criteria at given point in time(instant)
                        if (searchInput.getText().equalsIgnoreCase("")) //When everything is erased from search box, return all the user's timelines back to the ListView
                            list.setItems(FXCollections.observableArrayList(userTimelines));
                    }
                }
                else{ //Search all timelines
                  for(int i = 0; i<timelines.size(); i++){ //go trough all the current timelines in the database
                      for(int j = 0; j<inputs.length;j++){//No check all the search words used if they are to be found anywhere as keywords
                          String toFind = inputs[j]; //while a keyword is just one letter i.e. "f" if a keyword in timeline has that letter then it will be shown (instant search feature)
                          boolean found = Arrays.asList(timelines.get(i).getKeywords().toArray()).stream().anyMatch(s -> s.toString().toLowerCase().contains( toFind.toLowerCase()));
                          if(found){
                              if(!templist.contains(timelines.get(i))) //if the timline has not already been associated with this search then add it to the temporary timelinelist
                              templist.add(timelines.get(i));}
                      }
                      list.setItems(FXCollections.observableArrayList(templist)); //populate the ListView with the timelines that fulfill the search criteria at given point in time(instant)
                      if (searchInput.getText().equalsIgnoreCase("")) //When everything is erased from search box, return all the timelines back to the ListView
                          list.setItems(FXCollections.observableArrayList(timelines));
                  }}
            });
    }

    @FXML
    public void onlyUserTimelines() {

        if (cbOnlyViewPersonalLines.isSelected()) {
            try {
                PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineOwner = ?");
                stmt.setInt(1, GUIManager.loggedInUser.getUserID()); // GUIManager.loggedInUser.getUserID() uncomment
                // this for real version
                this.userTimelines = DBM.getFromDB(stmt, new Timeline());
                list.setItems(FXCollections.observableArrayList(userTimelines));
            } catch (SQLException e) {
                System.err.println("Could not get timelines from database.");
            }
        } else {
            try {
                PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
                list.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
                sortTimelines();
            } catch (SQLException e) {
                System.err.println("Could not get timelines from database.");
            }
        }

    }

    @FXML
    public TimelineView createTimeline() {
        Timeline t = new Timeline();
        t.setOwnerID(GUIManager.loggedInUser.getUserID());
        try {
            TimelineView timelineView = GUIManager.swapScene("TimelineView");
            timelineView.setActiveTimeline(t);
            timelineView.timelineEditorController.toggleEditable(true);
            return timelineView;
        } catch (IOException e) {e.printStackTrace(); return null;}
    }

    @FXML
    public TimelineView editTimeline() {
        return(openTimelineView(this.activeTimeline));
    }

    @FXML
    public TimelineView openTimeline() {
        return(openTimelineView(list.getSelectionModel().getSelectedItem()));
    }

    private TimelineView openTimelineView(Timeline newActiveTimeline) {
        try {
            TimelineView timelineView = GUIManager.swapScene("TimelineView");
            timelineView.setActiveTimeline(newActiveTimeline);
            return timelineView;
        } catch (IOException e) {e.printStackTrace(); return null;}
    }

    // open DeletePopUp
    @FXML
    public boolean deleteConfirmation() throws IOException {

        Alert confirmDeleteTimeline = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDeleteTimeline.setTitle("Confirm Deletion");
        confirmDeleteTimeline.setHeaderText("Are you sure you want to delete " + list.getSelectionModel().getSelectedItem().getName() + "?");
        confirmDeleteTimeline.setContentText("This can not be undone.");

        Optional<ButtonType> result = confirmDeleteTimeline.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        else
        {
            try {deleteOrphans(list.getSelectionModel().getSelectedItem());
                DBM.deleteFromDB(list.getSelectionModel().getSelectedItem());
            } catch (SQLException e) {e.printStackTrace();}

            list.getItems().remove(list.getSelectionModel().getSelectedIndex());
            list.getSelectionModel().select(null);
            return true;
        }

        //Stage delConfirm = new Stage();
        //delConfirm.setTitle("Confirm Deletion");
        //delConfirm.initOwner(GUIManager.mainStage);
//
        //delConfirm.initModality(Modality.WINDOW_MODAL);
        //delConfirm.setResizable(false);
//
        //FXMLLoader popupDeletion = new FXMLLoader(GUIManager.class.getResource("../FXML/Popup.fxml"));
        //VBox popup = popupDeletion.load();
        //popup.getStylesheets().add(GUIManager.mainStage.getScene().getStylesheets().get(0));
        //delConfirm.setScene(new Scene(popup));
//
        //Popup deletionPopup = popupDeletion.getController();
        //deletionPopup.setMode(1);
        //if (list.getSelectionModel().getSelectedItem() != null && list.getSelectionModel().getSelectedItem().getOwnerID() == GUIManager.loggedInUser.getUserID()) {
        //    titleText.setText("");
        //    deletionPopup.setList(list);
        //    deletionPopup.setDisplayTxt(
        //            "Are you sure you want to delete " + list.getSelectionModel().getSelectedItem().getName() + "?");
        //    delConfirm.show();
        //}
    }

    public void deleteOrphans(Timeline timeline) throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("SELECT e.* FROM `timelines` t " +
                "LEFT JOIN timelineevents te " +
                "ON t.TimelineID = te.TimelineID " +            //destroys orphaned events (i.e. events where there are no
                "LEFT JOIN events e " +                            //junction table records for them with a different TimelineID
                "ON te.EventID = e.EventID AND e.EventID NOT IN (SELECT EventID FROM timelineevents WHERE TimelineID != ?) " +
                "WHERE t.TimelineID = ? ");

        out.setInt(1, timeline.getID());
        out.setInt(2, timeline.getID());

        DBM.deleteFromDB(DBM.getFromDB(out, new Event()));
    }

    @FXML
    private void updateDisplays() {
        if (list.getSelectionModel().getSelectedItem() != null) {
            if (list.getSelectionModel().getSelectedItem().getOwnerID() == GUIManager.loggedInUser.getUserID()) {
                btnDelete.setDisable(false);
                btnEdit.setDisable(false);
            } else {
                btnDelete.setDisable(true);
                btnEdit.setDisable(true);
            }
            timelineViewButton.setDisable(false);

            Timeline timeline = list.getSelectionModel().getSelectedItem();

            int year = timeline.getCreationDate().getYear();
            int month = timeline.getCreationDate().getMonth();
            int day = timeline.getCreationDate().getDay();

            StringBuilder keyWords = new StringBuilder();
            for (String s : timeline.getKeywords())
                keyWords.append(s + ", ");
            keyWords.delete(keyWords.length() - 2, keyWords.length());

            titleText.setText("Title: " + timeline.getName()
                    + "\nDescription: " + timeline.getDescription()
                    + "\nDate Created: " + year + "/" + month + "/" + day
                    + "\nKeywords: " + keyWords);

        } else {
            timelineViewButton.setDisable(true);
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
            titleText.setText("Select a Timeline.");
        }
    }
}
