import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EventEditor_GUI extends VBox {


    @FXML
    TextField titleInput = new TextField();
    @FXML
    TextArea descriptionInput = new TextArea();
    @FXML
    DatePicker startInput = new DatePicker();
    @FXML
    CheckBox hasDuration = new CheckBox();
    @FXML
    DatePicker endInput = new DatePicker();             //only a datepicker for skeleton, will figure best way to enter info later
    @FXML
    ComboBox<String> imageInput = new ComboBox<>();
    private Event event;

    public EventEditor_GUI() {

    }

    public void initialize() {
        populateDisplay();
    }

    @FXML
    private void toggleHasDuration() {
        endInput.setDisable(!hasDuration.isSelected());
    }

    @FXML
    private void uploadImage() {
        //don't implement, not part of current sprint
        System.out.println("Button pressed.");
    }

    public boolean changeEvent(int eventID) {       //is this even needed? don't implement yet
        /*Event newEvent = logic to find Event in database and get its info
        if (newEvent != null)
            return changeEvent(newEvent);*/

        return false;
    }

    public boolean changeEvent(Event event) {       //is this even needed? don't implement yet
        this.event = event;
        return populateDisplay();
    }

    private boolean populateDisplay() {
        //populate fields with info from event, or leave blank if new event and such
        return false;
    }

    @FXML
    private boolean saveEvent() throws SQLException {

        //setters to update each field of this.event, based on the current info in the text fields
        this.event.setTitle(titleInput.getText());
        this.event.setDescription(descriptionInput.getText());
        this.event.setStartDate(startInput.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        this.event.setEndDate(endInput.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        //this.event.setImage(); later

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

        // delete event from DB, on this and all other timelines
        System.out.println("Delete event.");
        return true;
    }


    @FXML
    private void close() throws IOException {
        //If this.event fields != textboxes throw warning,unsaved changes -> before exit or
        //close editor, return to previous screen
        GUIManager.swapScene("example");
        System.out.println("Button pressed.");
    }
}