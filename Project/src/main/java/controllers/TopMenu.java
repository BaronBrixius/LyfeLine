package controllers;

import com.google.gson.Gson;
import database.JSONTimeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class TopMenu {

    @FXML
    Menu fileMenu;
    @FXML
    MenuItem saveButton = new MenuItem();
    @FXML
    Menu loggedInStatus = new Menu();

    public void initialize() {
        updateLoggedInStatus();
    }

    @FXML
    void saveFile(ActionEvent actionEvent) {
        System.out.println("Save");
    }

    @FXML
    void styleDefaultPressed() {
        GUIManager.applyStyle("DefaultStyle");
    }

    @FXML
    void styleNonePressed() {
        GUIManager.applyStyle("None");
    }

    @FXML
    void updateLoggedInStatus() {
        if (GUIManager.loggedInUser == null) {
            loggedInStatus.setText("Not logged in");
            loggedInStatus.setDisable(true);
        } else {
            loggedInStatus.setText("Logged in as: " + GUIManager.loggedInUser.getUserEmail());
            loggedInStatus.setDisable(false);
        }
    }

    @FXML
    void logOutPressed() {
        GUIManager.loggedInUser = null;
        updateLoggedInStatus();
        try {
            GUIManager.swapScene("Welcome");
        } catch (IOException e) {

        }
    }

    @FXML
    void importFromJSON() throws FileNotFoundException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"));
        File fileChosen = chooser.showOpenDialog(GUIManager.mainStage);
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Look, Your file now in Database");
            alert.setContentText("You can use it now :)");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();    //TODO better exception handling after dev work
        }
        Gson gson = new Gson();
        File file = new File(String.valueOf(fileChosen));
        Scanner inFile = new Scanner(file);
        JSONTimeline readJson = gson.fromJson(inFile.nextLine(), JSONTimeline.class);
        readJson.importToDB();
        inFile.close();
    }
}
