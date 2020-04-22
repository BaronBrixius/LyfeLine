import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.LocalTimeStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

public class EventEditor_GUI {

	@FXML
	public Button editButton;
	@FXML
	public Button uploadButton;
	@FXML
	public Button deleteButton;
	@FXML
	public Spinner<LocalTime> startTime;
	@FXML
	public Spinner<LocalTime> endTime;
	@FXML
	TextField titleInput = new TextField();
	@FXML
	TextArea descriptionInput = new TextArea();
	@FXML
	DatePicker startDate = new DatePicker();
	@FXML
	CheckBox hasDuration = new CheckBox();
	@FXML
	DatePicker endDate = new DatePicker(); // only a datepicker for skeleton, will figure best way to enter info later
	@FXML
	ComboBox<String> imageInput = new ComboBox<>();
	boolean editable = true;
	private Event event;

	public EventEditor_GUI() {
		GUIManager.mainStage.setTitle("Event Editor");
	}

	public void initialize() {
		/*
		 * if (!GUIManager.loggedInUser.getAdmin()) { //TODO uncomment this when hooked
		 * up to rest of program editButton.setVisible(false);
		 * editButton.setDisable(true); deleteButton.setVisible(false);
		 * deleteButton.setDisable(true); }
		 */

		startTime = new Spinner(new SpinnerValueFactory() {

			{
				setConverter(new LocalTimeStringConverter(FormatStyle.MEDIUM));
			}

			@Override
			public void decrement(int steps) {
				if (getValue() == null)
					setValue(LocalTime.now());
				else {
					LocalTime time = (LocalTime) getValue();
					setValue(time.minusMinutes(steps));
				}
			}

			@Override
			public void increment(int steps) {
				if (this.getValue() == null)
					setValue(LocalTime.now());
				else {
					LocalTime time = (LocalTime) getValue();
					setValue(time.plusMinutes(steps));
				}
			}
		});
		startTime.setEditable(true);
	}

	@FXML
	private void toggleHasDuration() {
		endDate.setDisable(!hasDuration.isSelected());
		endTime.setDisable(!hasDuration.isSelected());
	}

	public void toggleEditMode() { // I know this is ugly right now
		editable = !editable;
		titleInput.setEditable(editable);
		descriptionInput.setEditable(editable);
		startDate.setEditable(editable);
		startTime.setEditable(editable);
		endDate.setEditable(editable);
		endTime.setEditable(editable);
		imageInput.setEditable(editable);
		uploadButton.setVisible(editable);
		uploadButton.setDisable(!editable);

		editButton.setText(editable ? "Save" : "Edit");
	}

	@FXML
	private void uploadImage() {
		// don't implement, not part of current sprint
		System.out.println("Button pressed.");
	}

	public boolean setEvent(int eventID) { // is this even needed? don't implement yet
		/*
		 * Event newEvent = logic to find Event in database and get its info if
		 * (newEvent != null) return changeEvent(newEvent);
		 */

		return false;
	}

	public boolean setEvent(Event event) {
		this.event = event;
		return populateDisplay();
	}

	private boolean populateDisplay() {
		// text fields filed with info from selected event
		titleInput.setText(this.event.getEventName());
		descriptionInput.setText(this.event.getEventDescrition());
		startDate.setValue(LocalDate.of(this.event.getEventStart().getYear(), this.event.getEventStart().getMonth(),
				this.event.getEventStart().getDay()));
		// If event has a Start date that is different from end vs if it has an end
		// date, we need to deside for duration events
		if (this.event.getEventStart() != this.event.getEventEnd()) {
			hasDuration.setSelected(true);
			endDate.setValue(LocalDate.of(this.event.getEventEnd().getYear(), this.event.getEventEnd().getMonth(),
					this.event.getEventEnd().getDay()));
		}
		return true;
	}

	@FXML
	private boolean saveEvent() {

		// setters to update each field of this.event, based on the current info in the
		// text fields
		this.event.setTitle(titleInput.getText());
		this.event.setDescription(descriptionInput.getText());
		this.event.setStartDate(startDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		this.event.setEndDate(endDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		// this.event.setImage(); later

		try {
			if (this.event.getEventID() == 0)
				DBM.insertIntoDB(event);
			else
				DBM.updateInDB(event);
			return true;
		} catch (SQLException e) {
			return false;
		}

	}

	@FXML
	private boolean deleteEvent() {
		Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
		confirmDelete.setTitle("Confirm Delete");
		confirmDelete.setHeaderText("Deleting this event will remove it from all other timelines as well.");
		confirmDelete.setContentText("Are you ok with this?");

		Optional<ButtonType> result = confirmDelete.showAndWait();

		if (result.get() == ButtonType.CANCEL)
			return false;

		try {
			if (this.event.getEventID() == 0)
				throw new IllegalArgumentException("event not in database");
			else
				DBM.deleteFromDB(event);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	@FXML
	private void close() throws IOException {
		// if(!this.event.getEventName().equals(titleInput.getText()) ||
		// !this.event.getEventDescrition().equals(descriptionInput.getText()) ||
		// !this.event.getEventStart().toString().equals(startInput.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd"+0+0+0+0)))
		// ||this.event.getEventEnd().toString().equals(endInput.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd"+0+0+0+0))))
		// {//then something also for image later to see if changed
		// do you wanna save and exit or just save?
		// if save and exit:
		// saveEvent();
		// GUIManager.swapScene("example");
		// else
		// GUIManager.swapScene("example");
		// }
		// close editor, return to previous screen
		// else
		GUIManager.previousPage();
	}

}