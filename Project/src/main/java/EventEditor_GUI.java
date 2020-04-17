import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EventEditor_GUI extends VBox {
    Label header = new Label("Event Editor");
    Label titleLabel = new Label("Title");
    Label descriptionLabel = new Label("Description");
    Label startLabel = new Label("Start");
    Label endLabel = new Label("End");
    Label imageLabel = new Label("Image");
    TextField titleInput = new TextField();
    TextArea descriptionInput = new TextArea();
    DatePicker startInput = new DatePicker();
    CheckBox hasDuration = new CheckBox();
    DatePicker endInput = new DatePicker();             //only a datepicker for skeleton, will figure best way to enter info later
    ComboBox<String> imageInput = new ComboBox<>();
    Button uploadButton = new Button("Upload img");     //not sure if good idea, but it's on the sketch
    Button saveButton = new Button("Save");
    Button deleteButton = new Button("Delete");
    Button closeButton = new Button("Close");
    private Event event;

    public EventEditor_GUI() {
        this(new Event());
    }

    public EventEditor_GUI(Event event) {
        Label[] labels = new Label[]{titleLabel, descriptionLabel, startLabel, endLabel, imageLabel};

        GridPane buttons = new GridPane();
        buttons.addColumn(0, uploadButton, saveButton, deleteButton);
        buttons.add(closeButton, 1, 2);

        for (Node b: buttons.getChildren())
            b.getStyleClass().add("smallButton");
        saveButton.setPadding(new Insets(50,50,50,50));

        this.event = event;
        setScaleX(0.9);
        setScaleY(0.9);


        setSpacing(5);



        hasDuration.setPadding(new Insets(5,0,0,0));

        HBox endHeader = new HBox(50);
        endHeader.getChildren().addAll(endLabel, hasDuration);

        //HBox bottom

        getChildren().addAll(header,
                titleLabel, titleInput,
                descriptionLabel, descriptionInput,
                startLabel, startInput, endHeader, endInput,
                imageLabel, imageInput, buttons);
        //add(closeButton, 1, 13);


        setHasDuration(false);

        hasDuration.setOnAction(e ->
                setHasDuration(hasDuration.isSelected()));



        updateDisplay();
    }

    private void setHasDuration(boolean set) {
        endInput.setDisable(!set);
    }

    public boolean changeEvent(int eventID) {
        /*Event newEvent = logic to find Event in database and get its info
        if (newEvent != null)
            return changeEvent(newEvent);*/

        return false;
    }

    public boolean changeEvent(Event event) {
        this.event = event;
        return updateDisplay();
    }

    private boolean updateDisplay() {
        //populate fields with info from event, or leave blank if new event and such

        return false;
    }

    public boolean saveToDB() {
        //setters to update each field of this.event, based on the current info in the text fields

        /*try {
         if (event.getEventID = 0)
            DBM.addToDB(event);
        else
            DBM.updateInDB(event);
         return true;
        } catch (SQLException e){
            return false;
        }*/

        return false;
    }
}