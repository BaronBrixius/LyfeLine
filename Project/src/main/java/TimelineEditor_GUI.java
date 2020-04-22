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
	PreparedStatement stmt2;

	int id = 0;

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
		System.out.println("The Save button has been pushed.");
		// Timeline.activeTimeline.getInsertQuery();

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

			// Timeline(String TimelineName, String TimelineDescription, String Scale,
			// String Theme, Date StartDate, Date Enddate, Date DateCreated,
			// int TimelineOwner, boolean Private) Date(LocalDate.now())
		} else {
			// Get a timeline from DB. Such ineficient, much sad!
			titleInput.setText(activeTimeline.getTimelineName());
			descriptionInput.setText(activeTimeline.getTimelineDescription());
			startDateInput.setValue(LocalDate.of(activeTimeline.getStartDate().getYear(),
					activeTimeline.getStartDate().getMonth(), activeTimeline.getStartDate().getDay()));
			endDateInput.setValue(LocalDate.of(activeTimeline.getEndDate().getYear(),
					activeTimeline.getEndDate().getMonth(), activeTimeline.getEndDate().getDay()));
		}
	}

}
