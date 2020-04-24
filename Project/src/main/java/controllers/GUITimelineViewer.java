package controllers;

import database.*;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import java.util.*;

public class GUITimelineViewer {
	@FXML
	private GridPane timelineGridPane;
	private Timeline activeTimeline;
	private int length;

	public GUITimelineViewer() {

	}

	public GUITimelineViewer(Timeline activeTimeline) {
		this.activeTimeline = activeTimeline;
	}

	public void initialize() {
		// working on a distanceTo method, for the number of colums
		//length = activeTimeline.getStartDate().distanceTo(activeTimeline.getEndDate();
			//	activeTimeline.getScale());
		length = 5;
		for (int i = 0; i > length - 2; i++) {
			timelineGridPane.addColumn(i, null);
		}
	}

}
