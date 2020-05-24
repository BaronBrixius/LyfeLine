package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.util.Optional;


public class TopMenu {

    @FXML
    Menu loggedInStatus = new Menu();

    public void initialize() {
        updateLoggedInStatus();
    }

    @FXML
    void styleDefaultPressed() {
        GUIManager.applyStyle("Default");
    }

    @FXML
    void styleLightPressed() {
        GUIManager.applyStyle("Light");
    }

    @FXML
    void styleBluePressed() {
        GUIManager.applyStyle("Blue");
    }

    @FXML
    void styleDarkPressed() {
        GUIManager.applyStyle("Dark");
    }

    @FXML
    void styleMaroonPressed() {
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
    void logOutPressed() {
        GUIManager.loggedInUser = null;
        updateLoggedInStatus();
        GUIManager.applyStyle("Default");
        try {
            GUIManager.swapScene("LoginAndRegistration");
        } catch (IOException e) {

        }
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

        TimelineView view = GUIManager.loader.getController();

        double Hvalue = view.mainScrollPane.getHvalue();
        double Vvalue = view.mainScrollPane.getVvalue();

        view.timelineGrid.setScaleX(zoomValue);
        view.timelineGrid.setScaleY(zoomValue);

        view.mainScrollPane.layout();

        view.mainScrollPane.setHvalue(Hvalue);
        view.mainScrollPane.setVvalue(Vvalue);
    }
}
