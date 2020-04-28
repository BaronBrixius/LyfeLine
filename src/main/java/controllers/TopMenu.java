package controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.fxml.FXML;

public class TopMenu {

    @FXML MenuItem saveButton = new MenuItem();
    @FXML Menu loggedInStatus = new Menu();

    @FXML
    public void saveFile(ActionEvent actionEvent) {
        System.out.println("Save");
    }
    
    @FXML
    public void updateLoggedInStatus() {
    	if (null == GUIManager.loggedInUser) {
			loggedInStatus.setText("Not logged in");
			loggedInStatus.setDisable(true);
		} else {
			loggedInStatus.setText("Logged in as: " + GUIManager.loggedInUser.getUserEmail());
			loggedInStatus.setDisable(false);
		}
    }
    
    @FXML
    public void logOutPressed() {
    	GUIManager.loggedInUser=null;
    	updateLoggedInStatus();
    	try {
			GUIManager.swapScene("Welcome");
		} catch (IOException e) {
			
		}
    }
}
