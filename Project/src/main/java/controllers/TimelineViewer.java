package controllers;

import database.DBM;
import database.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TimelineViewer {

	@FXML private Button backButton;
	@FXML private HBox everythingHBox;
	public Timeline activeTimeline;

	
	public void initialize() {
		try {
			everythingHBox.getChildren().add(FXMLLoader.load(GUIManager.class.getResource("../FXML/EventSelector.fxml")));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void goBackButton() {
		try {
			GUIManager.swapScene("Dashboard");
		} catch (IOException e) {e.printStackTrace();}
	}

	//Call this method when swapping scenes
	public void setActiveTimeline(Timeline t) {
		this.activeTimeline = t;
	}

	//This method is probably not needed, but whatever
	public boolean setActiveTimeline(int id) {
		try {
			PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineID = ?");
			stmt.setInt(1, id);
			List<Timeline> list = DBM.getFromDB(stmt, new Timeline());

			this.activeTimeline = list.get(0);

			//For testing
			return list.size() == 1;
		}
		catch (SQLException e) {e.printStackTrace(); return false;}
		catch (IndexOutOfBoundsException i) {System.out.println("Could not find that timeline."); return false;}
	}
}
