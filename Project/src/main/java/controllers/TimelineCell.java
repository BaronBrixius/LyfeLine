package controllers;

import database.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class TimelineCell extends GridPane{

	@FXML
	private Text timelineName;
	@FXML
	private Button timelineID;

	public Timeline timeline;

	public void initialize() {

	}

	@FXML
	public void buttonTest(ActionEvent event) {
		timelineID.setText("0");
	}

	public void update() {
		if (timeline != null) {
			timelineName.setText(timeline.getName());
			timelineID.setText("ID: "+timeline.getID());
		}
	}

	public Timeline getTimeline() {
		return timeline;
	}

	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
	}

}
