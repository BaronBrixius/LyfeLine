package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimelineView {

	private final List<EventNode> eventList = new ArrayList<>();
	public GridPane timelineGrid;
	public Timeline activeTimeline;
	public BorderPane mainBorderPane;
	public StackPane rightSidebar;
	public StackPane leftSidebar;
	TimelineEditor timelineController;
	EventSelector selectorController;
	EventEditor editorController;
	@FXML
	private Button backButton;

	public void initialize() {
		try {
			FXMLLoader TimelineLoader = new FXMLLoader(getClass().getResource("../FXML/TimelineEditor.fxml"));
			TimelineLoader.load();
			timelineController = TimelineLoader.getController();
			timelineController.setParentController(this);
		} catch (IOException e) {
			e.printStackTrace(); // TODO replace with better error message once dev is done
		}

		try {
			FXMLLoader selectorLoader = new FXMLLoader(getClass().getResource("../FXML/EventSelector.fxml"));
			selectorLoader.load();
			selectorController = selectorLoader.getController();
			selectorController.setParentController(this);
		} catch (IOException e) {
			e.printStackTrace(); // TODO replace with better error message once dev is done
		}

		try {
			FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("../FXML/EventEditor.fxml"));
			editorLoader.load();
			editorController = editorLoader.getController();
			editorController.setParentController(this);
		} catch (IOException e) {
			e.printStackTrace(); // TODO replace with better error message once dev is done
		}

		// leftSidebar.getChildren().remove(timelineController.editor);

		leftSidebar.getChildren().add(timelineController.editor);
	}

	public List<EventNode> getEventList() {
		return eventList;
	}

	public void goBackButton() {
		try {
			GUIManager.swapScene("Dashboard");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Call this method when swapping scenes

	public void setActiveTimeline(Timeline activeTimeline) {
		this.activeTimeline = activeTimeline;
		selectorController.setTimelineSelected(this.activeTimeline); // sets the selected index to the currently viewed
																// timeline
		timelineController.setTimeline(this.activeTimeline);
	}

	// This method is probably not needed, but whatever //useful for dev work to set
	// things up quickly!
	public boolean setActiveTimeline(int id) {
		try {
			PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineID = ?");
			stmt.setInt(1, id);
			List<Timeline> list = DBM.getFromDB(stmt, new Timeline());

			setActiveTimeline(list.get(0));
			timelineController.setTimeline(activeTimeline);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IndexOutOfBoundsException i) {
			System.out.println("Could not find that timeline.");
			return false;
		}
	}

	void populateDisplay() {
		timelineGrid.getChildren().clear();
		timelineGrid.getColumnConstraints().clear();

		Pane mainLine = new Pane();
		mainLine.getStyleClass().add("timeline");
		int numberOfCol = activeTimeline.getStartDate().distanceTo(activeTimeline.getEndDate(),
				activeTimeline.getScale());
		int counter, start = 0, frequency=1;

		int scale=activeTimeline.getScale();
		switch(scale) {
		  case 1:
			start=1;
			break;
		  case 2:
			start=1;
			break;
		  case 3:
			start=1;
			break;
		  case 4:
			start=1;
			break;
		  case 5:
			start=1;
			break;
		  case 6:
			start=1;
			break;
		  case 7:
			start=1;
			break;
		  case 8:
			frequency=2;
			start=activeTimeline.getStartDate().getYear();
			break;
		  case 9:
			start=activeTimeline.getStartDate().getYear()/10;
			break;
		  case 10:
			start=activeTimeline.getStartDate().getYear()/100;
			break;
		  case 11:
			start=activeTimeline.getStartDate().getYear()/1000;
			break;
		}

		counter=start;

		for (int i = 0; i <= numberOfCol; i++) {
			ColumnConstraints colConst = new ColumnConstraints();
			colConst.setPercentWidth(100.0 / numberOfCol);
			timelineGrid.getColumnConstraints().add(colConst);

			if (i % frequency == 0) {
				timelineGrid.add(new Text("" + counter), i, 0);
				counter+=frequency;

			} else if (i == 0) {
				timelineGrid.add(new Text("" + start), i, 0);
			} else if (i == numberOfCol) {
				timelineGrid.add(new Text("" + (numberOfCol+start)), i, 0);
			}
		}

		// System.out.print("Count from .getColumnCount " +
		// timelineGrid.getColumnCount()); for testing columns for the timeline

		if (numberOfCol >= 1) // if the start date is later than the end date, numberOfCol would be negative,
								// which does not work for the amount of columns
			timelineGrid.add(mainLine, 0, 0, numberOfCol, 1);

		// TODO set grid column count to actual timeline length, make the above look
		// better (possibly with its own fxml?)
		GridPane.setMargin(mainLine, new Insets(25, 0, -25, 0));

		EventNode newNode;

		eventList.clear();
		for (Event e : activeTimeline.getEventList()) {
			newNode = addEvent(e);
			eventList.add(newNode);
		}
		Collections.sort(eventList); // sort so that earlier events are placed first (longer first in case of tie)
		for (int i = 0; i < eventList.size(); i++)
			placeEvent(eventList.get(i), i);
	}

	EventNode addEvent(Event event) {
		try {
			FXMLLoader nodeLoader = new FXMLLoader(getClass().getResource("../FXML/EventNode.fxml"));
			nodeLoader.load();
			EventNode newNode = nodeLoader.getController();
			newNode.setActiveEvent(event, activeTimeline, this);
			return newNode;
		} catch (IOException e) {
			e.printStackTrace(); // TODO replace with better error message once dev is done
			return null;
		}
	}

	void placeEvent(EventNode newNode, int eventsPlacedCount) {
		int startColumn = newNode.getStartColumn();
		int columnSpan = newNode.getColumnSpan();
		if (startColumn < 0) { // if node starts before the timeline begins, cut the beginning
			columnSpan += startColumn;
			startColumn = 0;
		}
		if (startColumn + columnSpan > timelineGrid.getColumnCount()) // if node goes past the timeline's end, cut the
																		// end
			columnSpan = timelineGrid.getColumnCount() - startColumn;
		if (columnSpan < 1) // if, after cutting, nothing remains, don'activeTimeline display it at all
			return;

		int row = 1;
		for (int i = 0; i < eventsPlacedCount; i++) { // check previous nodes to see if they occupy desired columns
			if (eventList.get(i).getStartColumn() <= newNode.getStartColumn() + newNode.getColumnSpan() // if a previous
																										// node starts
																										// before the
																										// new one would
																										// end
					&& eventList.get(i).getStartColumn() + eventList.get(i).getColumnSpan() >= newNode.getStartColumn()) // and
																															// it
																															// ends
																															// after
																															// the
																															// new
																															// one
																															// starts
				row++; // try next row
		}
		timelineGrid.add(newNode.getDisplayPane(), startColumn, row, columnSpan, 1);
	}

	public void openEventSelector() {
		rightSidebar.getChildren().remove(selectorController.selector); // resets the event selector if it already
																		// exists
		rightSidebar.getChildren().add(selectorController.selector);
	}

	public void returnToDashboard() {
		try {
			GUIManager.swapScene("Dashboard");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
