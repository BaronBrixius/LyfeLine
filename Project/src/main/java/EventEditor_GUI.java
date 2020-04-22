import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
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
    public Label headerText;
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

    public void saveEditButton() {      //I know this is ugly right now
        if (editable && hasChanges())   //if unsaved changes, try to save
            if (!saveConfirm())         //if save cancelled, don't change mode
                return;

        toggleEditable(!editable);
    }

    void toggleEditable(boolean editable) {
        this.editable = editable;
        titleInput.setEditable(editable);
        descriptionInput.setEditable(editable);

        startDate.setEditable(editable);
        startTime1.setEditable(editable);
        startTime2.setEditable(editable);
        startTime3.setEditable(editable);
        startTime1.setDisable(!editable);
        startTime2.setDisable(!editable);
        startTime3.setDisable(!editable);

        endDate.setEditable(editable);
        endTime1.setEditable(editable);
        endTime2.setEditable(editable);
        endTime3.setEditable(editable);
        endTime1.setDisable(!editable);
        endTime2.setDisable(!editable);
        endTime3.setDisable(!editable);

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

        titleInput.setText(event.getEventName());
        descriptionInput.setText(event.getEventDescrition());

        startDate.setValue(LocalDate.of(event.getStartDate().getYear(), event.getStartDate().getMonth(), event.getStartDate().getDay()));

        startTime1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, event.getStartDate().getHours()));
        startTime2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getStartDate().getMinutes()));
        startTime3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getStartDate().getSeconds()));

        if (event.getStartDate().compareTo(event.getEndDate()) == 0)
        {
            endTime1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));
            endTime2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
            endTime3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        }
        else
        {
            hasDuration.setSelected(true);
            toggleHasDuration();
            endDate.setValue(LocalDate.of(event.getEndDate().getYear(), event.getEndDate().getMonth(), event.getEndDate().getDay()));
            endTime1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, event.getEndDate().getHours()));
            endTime2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getEndDate().getMinutes()));
            endTime3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getEndDate().getSeconds()));
        }


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

    void updateEvent() {
        //setters to update each field of this.event, based on the current info in the text fields
        event.setTitle(titleInput.getText());
        event.setDescription(descriptionInput.getText());
        LocalDate start = startDate.getValue();
        event.setStartDate(new Date(start.getYear(), start.getMonth().getValue(), start.getDayOfMonth(), startTime1.getValue(), startTime2.getValue(), startTime3.getValue(), 0));

        LocalDate end;
        if (hasDuration.isSelected()) {
            end = endDate.getValue();
            event.setEndDate(new Date(end.getYear(), end.getMonth().getValue(), end.getDayOfMonth(), endTime1.getValue(), endTime2.getValue(), endTime3.getValue(), 0));
        }
        else                //if it has no duration, end = start
            event.setEndDate(event.getStartDate());

        //this.event.setImage(); later
    }

    private boolean saveEvent() {
        updateEvent();
        try {
            if (event.getEventID() == 0)
                DBM.insertIntoDB(event);
            else
                DBM.updateInDB(event);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
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

    private boolean hasChanges() {
        LocalDate start = startDate.getValue();
        Date readStart = new Date(start.getYear(), start.getMonth().getValue(), start.getDayOfMonth(), startTime1.getValue(), startTime2.getValue(), startTime3.getValue(), 0);
        LocalDate end = endDate.getValue();
        Date readEnd = new Date(end.getYear(), end.getMonth().getValue(), end.getDayOfMonth(), endTime1.getValue(), endTime2.getValue(), endTime3.getValue(), 0);

        return (!event.getEventName().equals(titleInput.getText())
                || !event.getEventDescrition().equals(descriptionInput.getText())
                || event.getStartDate().compareTo(readStart) != 0
                || event.getEndDate().compareTo(readEnd) != 0
                //then something also for image later to see if changed
        );
    }

    @FXML
    private void close() throws IOException {
        if (hasChanges())
            saveConfirm();        //do you wanna save and exit or just save?
        GUIManager.previousPage();        //close editor, return to previous screen
    }

}