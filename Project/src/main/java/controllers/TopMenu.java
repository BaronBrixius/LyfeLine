package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.io.IOException;

public class TopMenu {

    @FXML
    MenuItem saveButton = new MenuItem();
    @FXML
    Menu loggedInStatus = new Menu();

    public void initialize() {
        updateLoggedInStatus();
    }

    @FXML
    public void saveFile(ActionEvent actionEvent) {
        System.out.println("Save");
    }
    
    @FXML
    public void styleDefaultPressed() {
    	GUIManager.applyStyle("Default");
    }
    
    @FXML
    public void styleBeigePressed() {
    	GUIManager.applyStyle("Beige");
    }
    
    @FXML
    public void styleBluePressed() {
    	GUIManager.applyStyle("Blue");
    }

    @FXML
    public void styleDarkPressed() {
    	GUIManager.applyStyle("Dark");
    }
    
    @FXML
    public void styleMaroonPressed() {
    	GUIManager.applyStyle("Maroon");
    }
    
    @FXML
    public void updateLoggedInStatus() {
        if (GUIManager.loggedInUser == null) {
            loggedInStatus.setText("Not logged in");
            loggedInStatus.setDisable(true);
        } else {
            loggedInStatus.setText("Logged in as: " + GUIManager.loggedInUser.getUserEmail());
            loggedInStatus.setDisable(false);
        }
    }

    @FXML
    public void logOutPressed() {
        GUIManager.loggedInUser = null;
        updateLoggedInStatus();
        GUIManager.applyStyle("Default");
        try {
            GUIManager.swapScene("Welcome");
        } catch (IOException e) {

        }
    }
}
