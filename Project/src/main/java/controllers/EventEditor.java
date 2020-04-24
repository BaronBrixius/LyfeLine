package controllers;

import database.*;
import utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventEditor {

    @FXML public GridPane editor;
    @FXML public Button editButton;
    @FXML public Button uploadButton;
    @FXML public Button deleteButton;

    @FXML public HBox startTime;
    @FXML public HBox endTime;
    @FXML public Spinner<Integer> startTime1;
    @FXML public Spinner<Integer> startTime2;
    @FXML public Spinner<Integer> startTime3;
    @FXML public Spinner<Integer> endTime1;
    @FXML public Spinner<Integer> endTime2;
    @FXML public Spinner<Integer> endTime3;

    @FXML public Label headerText;
    @FXML public Text errorMessage;
    @FXML TextField titleInput = new TextField();
    @FXML TextArea descriptionInput = new TextArea();
    @FXML DatePicker startDate = new DatePicker();
    @FXML CheckBox hasDuration = new CheckBox();@FXML DatePicker endDate = new DatePicker(); //only a datepicker for skeleton, will figure best way to enter info later
    @FXML ComboBox<ImageView> imageInput = new ComboBox<>();
    ImageView image;

    int startYear;

    boolean editable = true;
    EventSelector prevScreen;
    private Event event;

    public void setPrevScreen(EventSelector prevScreen) {             //TODO delete this inelegant solution
        this.prevScreen = prevScreen;
    }

    public void initialize() {
        if (
                GUIManager.loggedInUser == null ||          //TODO delete this when hooked up to rest of program
                        !GUIManager.loggedInUser.getAdmin()) {
            editButton.setVisible(false);
            editButton.setDisable(true);
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
        }

        try {
            List<String> images = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM Images"),
                    rs -> rs.getString("ImageURL"));

            List<ImageView> views = new ArrayList<>();
            ImageView blank = new ImageView(new Image("file:src/main/resources/images/pleasedontnameanythingthis.png"));
            blank.setFitHeight(40);
            blank.setFitWidth(40);
            views.add(blank);

            ImageView currImage;
            for (String s : images) {
                currImage = new ImageView(new Image("file:src/main/resources/images/" + s));
                currImage.setFitHeight(40);
                currImage.setFitWidth(40);
                views.add(currImage);
            }

            imageInput.setItems(FXCollections.observableArrayList(views));
            imageInput.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(ImageView item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(item);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleHasDuration() {
        endDate.setDisable(!hasDuration.isSelected());
        endTime.setDisable(!hasDuration.isSelected());
    }

    public void saveEditButton() {      //I know this is ugly right now
        LocalDate start;
        LocalDate end;
        Date readStart = new Date();
        Date readEnd = new Date();


        try {
            //Date Picker is literally bugged, this line works around it.
            startDate.setValue(startDate.getConverter().fromString(startDate.getEditor().getText()));
            //Convert the Date Picker to Date and see if problems happen
            start = startDate.getValue();
            readStart = new Date(start.getYear(), start.getMonth().getValue(), start.getDayOfMonth(),
                    startTime1.getValue(), startTime2.getValue(), startTime3.getValue(), event.getStartDate().getMillisecond());   //milliseconds not implemented yet, do we need to?
        } catch (NullPointerException e) {
            errorMessage.setText("Start date can't be empty.");
            return;
        } catch (DateTimeParseException d) {
            errorMessage.setText("Start date's format is improper.");
            return;
        }

        //If the End Date is selected, check it for problems too.
        if (hasDuration.isSelected()) {
            try {
                endDate.setValue(endDate.getConverter().fromString(endDate.getEditor().getText()));
                end = endDate.getValue();
                readEnd = new Date(end.getYear(), end.getMonth().getValue(), end.getDayOfMonth(), endTime1.getValue(), endTime2.getValue(), endTime3.getValue(), 0);

            } catch (NullPointerException e) {
                errorMessage.setText("End date can't be empty if selected.");
                return;
            } catch (DateTimeParseException d) {
                errorMessage.setText("End date's format is improper.");
                return;
            }
        }

        if (editable && hasChanges())   //if unsaved changes, try to save
            if (!saveConfirm())         //if save cancelled, don't change mode
                return;

        toggleEditable(!editable);
    }

    void toggleEditable(boolean editable) {
        this.editable = editable;

        titleInput.setEditable(editable);
        descriptionInput.setEditable(editable);
        hasDuration.setDisable(!editable);

        startDate.setDisable(!editable);
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

        imageInput.setDisable(!editable);
        uploadButton.setVisible(editable);
        uploadButton.setDisable(!editable);

        if (editable)
            editor.getStylesheets().removeAll("styles/DisabledEditing.css");
        else
            editor.getStylesheets().add("styles/DisabledEditing.css");

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

        startTime1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, event.getStartDate().getHour()));
        startTime2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getStartDate().getMinute()));
        startTime3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getStartDate().getSecond()));

        if (event.getStartDate().compareTo(event.getEndDate()) == 0) {
            endDate.setValue(LocalDate.of(event.getStartDate().getYear(), event.getStartDate().getMonth(), event.getStartDate().getDay()));
            endTime1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, event.getStartDate().getHour()));
            endTime2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getStartDate().getMinute()));
            endTime3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getStartDate().getSecond()));
        } else {
            hasDuration.setSelected(true);
            toggleHasDuration();
            endDate.setValue(LocalDate.of(event.getEndDate().getYear(), event.getEndDate().getMonth(), event.getEndDate().getDay()));
            endTime1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, event.getEndDate().getHour()));
            endTime2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getEndDate().getMinute()));
            endTime3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, event.getEndDate().getSecond()));
        }

        imageInput.getSelectionModel().select(event.getImageID());
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
        event.setDescription(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n"));

        LocalDate start = startDate.getValue();
        event.setStartDate(new Date(start.getYear(), start.getMonth().getValue(), start.getDayOfMonth(),
                startTime1.getValue(), startTime2.getValue(), startTime3.getValue(), event.getStartDate().getMillisecond()));  //milliseconds not implemented yet, do we need to?


        LocalDate end;
        if (hasDuration.isSelected()) {
            end = endDate.getValue();
            event.setEndDate(new Date(end.getYear(), end.getMonth().getValue(), end.getDayOfMonth(),
                    endTime1.getValue(), endTime2.getValue(), endTime3.getValue(), event.getEndDate().getMillisecond()));      //milliseconds not implemented yet, do we need to?
        } else                //if it has no duration, end = start
            event.setEndDate(event.getStartDate());

        this.event.setImage(imageInput.getSelectionModel().getSelectedIndex());
    }

    private boolean saveEvent() {
        updateEvent();
        try {
            if (event.getEventID() == 0) {
                DBM.insertIntoDB(event);
                event.addToTimeline(prevScreen.timelineList.getSelectionModel().getSelectedItem().getTimelineID());
                prevScreen.populateEventList();             //TODO delete this inelegant solution
            } else
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
            prevScreen.populateEventList();             //TODO delete this inelegant solution
            return true;
        } catch (SQLException | IOException e) {
            return false;
        }
    }

    private boolean hasChanges() {
        LocalDate start = startDate.getValue();
        Date readStart = new Date(start.getYear(), start.getMonth().getValue(), start.getDayOfMonth(),
                startTime1.getValue(), startTime2.getValue(), startTime3.getValue(), event.getStartDate().getMillisecond());   //milliseconds not implemented yet, do we need to?

        //If end is null, set end equal to start
        LocalDate end = endDate.getValue();
        Date readEnd = new Date(end.getYear(), end.getMonth().getValue(), end.getDayOfMonth(), endTime1.getValue(), endTime2.getValue(), endTime3.getValue(), event.getEndDate().getMillisecond());

        return (
                !event.getEventName().equals(titleInput.getText())
                        || !event.getEventDescrition().equals(descriptionInput.getText().replaceAll("([^\r])\n", "$1\r\n"))     //textArea tends to change the newline from \r\n to just \n which breaks some things
                        || event.getStartDate().compareTo(readStart) != 0
                        || event.getEndDate().compareTo(readEnd) != 0
                        || event.getImageID() != imageInput.getSelectionModel().getSelectedIndex()
        );
    }

    @FXML
    private void close() throws IOException {
        if (hasChanges())
            saveConfirm();        //do you wanna save and exit or just save?
        GUIManager.previousPage();        //close editor, return to previous screen
    }

}