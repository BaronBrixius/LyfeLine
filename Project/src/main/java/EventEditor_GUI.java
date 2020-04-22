import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EventEditor_GUI {

    @FXML
    public Button editButton;
    @FXML
    public Button uploadButton;
    @FXML
    public Button deleteButton;
    @FXML
    public HBox startTime;
    @FXML
    public HBox endTime;
    public Spinner<Integer> startTime1;
    public Spinner<Integer> startTime2;
    public Spinner<Integer> startTime3;
    public Spinner<Integer> endTime1;
    public Spinner<Integer> endTime2;
    public Spinner<Integer> endTime3;
    @FXML
    TextField titleInput = new TextField();
    @FXML
    TextArea descriptionInput = new TextArea();
    @FXML
    DatePicker startDate = new DatePicker();
    @FXML
    CheckBox hasDuration = new CheckBox();
    @FXML
    DatePicker endDate = new DatePicker();             //only a datepicker for skeleton, will figure best way to enter info later
    @FXML
    ComboBox<String> imageInput = new ComboBox<>();
    boolean editable = true;
    private Event event;

    public void initialize() {
        if (
                GUIManager.loggedInUser == null ||          //TODO delete this when hooked up to rest of program
                        !GUIManager.loggedInUser.getAdmin()) {
            editButton.setVisible(false);
            editButton.setDisable(true);
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
        }
    }

    @FXML
    private void toggleHasDuration() {
        endDate.setDisable(!hasDuration.isSelected());
        endTime.setDisable(!hasDuration.isSelected());
    }

    public void toggleEditMode() {       //I know this is ugly right now
        editable = !editable;
        titleInput.setEditable(editable);
        descriptionInput.setEditable(editable);
        startDate.setEditable(editable);

        //for (Spinner (Spinner) s:  startTime.getChildren())
        //    s.setEditable(editable);
        endDate.setEditable(editable);
        //endTime.setEditable(editable);
        imageInput.setEditable(editable);
        uploadButton.setVisible(editable);
        uploadButton.setDisable(!editable);

        editButton.setText(editable ? "Save" : "Edit");
    }

    @FXML
    private void uploadImage() {
        //don't implement, not part of current sprint
        System.out.println("Button pressed.");
    }

    public boolean setEvent(int eventID) {       //is this even needed? don't implement yet
        /*Event newEvent = logic to find Event in database and get its info
        if (newEvent != null)
            return changeEvent(newEvent);*/

        return false;
    }

    public boolean setEvent(Event event) {
        this.event = event;
        return populateDisplay();
    }

    private boolean populateDisplay() {
        //populate fields with info from event, or leave blank if new event and such
        return false;
    }

    @FXML
    private boolean saveConfirm() {
        Alert confirmsave = new Alert(Alert.AlertType.CONFIRMATION);
        confirmsave.setTitle("Confirm Save");
        confirmsave.setHeaderText("Saving changes to this event will alter it for all other timelines as well.");
        confirmsave.setContentText("Would you like to save?");

        Optional<ButtonType> result = confirmsave.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        return saveEvent();
    }

    private boolean saveEvent() {
        //setters to update each field of this.event, based on the current info in the text fields
        this.event.setTitle(titleInput.getText());
        this.event.setDescription(descriptionInput.getText());
        this.event.setStartDate(startDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        //if ()
            this.event.setEndDate(endDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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

        try {
            if (this.event.getEventID() == 0)
                throw new IllegalArgumentException("event not in database");
            else {
                DBM.deleteFromDB(event);
                close();
            }
            return true;
        } catch (SQLException | IOException e) {
            return false;
        }
    }

    @FXML
    private void close() throws IOException {
        if (!this.event.getEventName().equals(titleInput.getText())
                || !this.event.getEventDescrition().equals(descriptionInput.getText())
                || !this.event.getEventStart().toString().equals(startDate.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd" + 0 + 0 + 0 + 0)))
                || this.event.getEventEnd().toString().equals(endDate.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd" + 0 + 0 + 0 + 0))))
            //then something also for image later to see if changed
            //do you wanna save and exit or just save?
            //if save and exit:
            saveConfirm();
            //close editor, return to previous screen
        else
            GUIManager.previousPage();
    }
}