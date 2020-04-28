package controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class TimelineViewer {

	@FXML private Button backButton;
	@FXML private HBox everythingHBox;
	
	public void initialize() {
		try {
			everythingHBox.getChildren().add(FXMLLoader.load(GUIManager.class.getResource("../FXML/EventSelector.fxml")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void goBackButton() {
		try {
			GUIManager.swapScene("Dashboard");
		} catch (IOException e) {
			
		}
	}
	
}
