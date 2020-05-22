package controllers;

import database.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.util.Optional;

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

    void showExportMenu(boolean show) {
        if (fileMenu.getItems().contains(export) == show)        //check if file menu already contains export button
            return;

        if (show)
            fileMenu.getItems().add(export);
        else
            fileMenu.getItems().remove(export);
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

    @FXML
    void importFromJSON() {
    }

    void exportToJSON(Timeline timelineToExport) {
        System.out.println(timelineToExport.getName());
    }

    @FXML
    void zoom() {
        if (!(GUIManager.loader.getController() instanceof TimelineView))
            return;

        TextInputDialog zoomInput = new TextInputDialog("100");
        zoomInput.setTitle("Zoom");
        zoomInput.setHeaderText("Enter Zoom%");

        Optional<String> result = zoomInput.showAndWait();


        result.ifPresent(e -> zoomTimeline(result.get()));


    }

    private void zoomTimeline(String string) {
        string = (string.replaceAll("[^\\d]", ""));
        if (string.isEmpty())
            string = "100";

        double zoomValue = Double.parseDouble(string) / 100;
        if (zoomValue > 100)
            zoomValue = 100;
        if (zoomValue < 0.01)
            zoomValue = 0.01;

        ((TimelineView) GUIManager.loader.getController()).timelineGrid.setScaleX(zoomValue);
        ((TimelineView) GUIManager.loader.getController()).timelineGrid.setScaleY(zoomValue);
    }
}
