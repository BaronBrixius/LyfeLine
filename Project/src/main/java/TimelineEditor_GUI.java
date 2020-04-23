import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TimelineEditor_GUI {

	@FXML
	private TextArea titleInput;
	@FXML
	private TextArea descriptionInput;
	@FXML
	private TextArea keywordsInput;
	@FXML
	private DatePicker startDateInput;
	@FXML
	private DatePicker endDateInput;
	@FXML
	private ComboBox<String> timeInput;

	private Timeline activeTimeline;
	PreparedStatement stmt;


	public TimelineEditor_GUI() {
		GUIManager.mainStage.setTitle("Timeline Editor");
	}

	@FXML
	private void initialize() throws SQLException {
		// This is for constructing a new timeline.

		StringBuilder keywordsList = new StringBuilder();

		/*
		 * This is how I'm assuming the keywords get into the TextArea when they are
		 * implemented. Store them as a String Array, and append them one by one to a
		 * StringBuilder. Append a comma and a space for everyone of them except the
		 * last one, then append that one manually.
		 * 
		 * for (int i = 0; i < timeline.getKeywords - 1; i++) {
		 * keywordsList.append(timeline.getKeywords.get(i) + ", "); }
		 * keywordsList.append(timeline.getKeywords.get(timeline.getKeywords.size - 1));
		 */
		keywordsInput.setText(keywordsList.toString());

		// startDateInput.setValue(); //put timeline.getStartDate.toLocaleDate() as
		// parameter

		// endDateInput.setValue(); //put timeline.getEndDate.toLocaleDate() as
		// parameter

	}

	@FXML
	public void save(MouseEvent event) {
		//System.out.println("The Save button has been pushed.");
		//Creating a new timeline will run in here
		//System.out.println("First printout" + activeTimeline);
		if (activeTimeline == null) {
			try {
				Date dateCreated = new Date(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
				//create a new timeline object to work with.
				activeTimeline = new Timeline(titleInput.getText(), descriptionInput.getText(), 1, null, dateCreated, dateCreated,
						dateCreated, 0, false);

				//Write new timeline into DB
				DBM.insertIntoDB(activeTimeline);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//This part is for editing an existing timeline
		else {
			activeTimeline.setTimelineName(titleInput.getText());
			activeTimeline.setTimelineDescription(descriptionInput.getText());
			activeTimeline.setScale((timeInput.getSelectionModel().getSelectedIndex()) + 1);
			LocalDate start = startDateInput.getValue();
			activeTimeline.setStartDate(new Date(start.getYear(), start.getMonthValue(), start.getDayOfMonth()));
			LocalDate end = endDateInput.getValue();
			activeTimeline.setEndDate(new Date(end.getYear(), end.getMonthValue(), end.getDayOfMonth()));
			//Public/Private Selection - TBA
			try {
				DBM.updateInDB(activeTimeline);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// Timeline.activeTimeline.getInsertQuery();
		System.out.println(activeTimeline);


	}

	@FXML
	public void cancel() throws IOException {
		GUIManager.swapScene("Dashboard");
		activeTimeline = null;
	}

	public void setActiveTimeline(Timeline a) {
		this.activeTimeline = a;
	}

	public void populateDisplay() {
		if (activeTimeline == null) {
			try {
				activeTimeline = new Timeline(titleInput.getText(), descriptionInput.getText(), 1, null, null, null,
						null, 0, false);
			} catch (SQLException e) {

			}

		} else {
			// Get a timeline from DB. Such ineficient, much sad!
			titleInput.setText(activeTimeline.getTimelineName());
			descriptionInput.setText(activeTimeline.getTimelineDescription());
			startDateInput.setValue(LocalDate.of(activeTimeline.getStartDate().getYear(),
					activeTimeline.getStartDate().getMonth(), activeTimeline.getStartDate().getDay()));
			endDateInput.setValue(LocalDate.of(activeTimeline.getEndDate().getYear(),
					activeTimeline.getEndDate().getMonth(), activeTimeline.getEndDate().getDay()));
			timeInput.getSelectionModel().select(activeTimeline.getScale() - 1);
			//activeTimeline.setScale((timeInput.getSelectionModel().getSelectedIndex()) + 1);
			//System.out.println(activeTimeline.getScale());
		}
	}

}
