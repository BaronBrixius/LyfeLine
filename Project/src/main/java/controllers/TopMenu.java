package controllers;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.JSONTimeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import utils.DateUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
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
    void importFromJSON() {
        FileChooser chooser = new FileChooser();                                            //open FileChooser for user to pick import .json
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File fileChosen = chooser.showOpenDialog(GUIManager.mainStage);

        try {
            String inJSON = FileUtils.readFileToString(fileChosen, (Charset) null);         //import Json from file
            Gson gson = JSONTimeline.getGson();
            JSONTimeline readJson = gson.fromJson(inJSON, JSONTimeline.class);              //parse Json with GSON object
            readJson.importToDB();                                                          //add imported data to database

            Alert alert = new Alert(Alert.AlertType.INFORMATION);                           //inform user of success
            alert.setTitle("File Import");
            alert.setHeaderText("File has been successfully imported.");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();     //TODO better exception handling after dev work
        }
    }
}
