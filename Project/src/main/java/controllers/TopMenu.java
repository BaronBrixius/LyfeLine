package controllers;

import database.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.io.IOException;

public class TopMenu {

    public Menu fileMenu;
    MenuItem export = new MenuItem("Export");
    @FXML
    MenuItem saveButton = new MenuItem();
    @FXML
    Menu loggedInStatus = new Menu();

    public void initialize() {
        updateLoggedInStatus();
        showExportMenu(false);
    }

    @FXML
    public void saveFile(ActionEvent actionEvent) {
        System.out.println("Save");
    }
    
    @FXML
    public void styleDefaultPressed() {
    	GUIManager.applyStyle("DefaultStyle");
    }
    
    @FXML
    public void styleNonePressed() {
    	GUIManager.applyStyle("None");
    }

    void showExportMenu(boolean show){
        if (fileMenu.getItems().contains(export) == show)        //check if file menu already contains export button
            return;

        if (show)
            fileMenu.getItems().add(export);
        else
            fileMenu.getItems().remove(export);
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
        try {
            GUIManager.swapScene("Welcome");
        } catch (IOException e) {

        }
    }

    @FXML
    void importFromJSON() {
    }

    void exportToJSON(Timeline timelineToExport) {
        System.out.println(timelineToExport.getName());
    }
}
