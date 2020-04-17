import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EventEditor_GUI extends VBox {
    Label nameLabel = new Label("New Event");
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
        this.event = event;

        setScaleX(0.9); setScaleY(0.9);
        setPrefWidth(350);

        //nameLabel.setText(event.getEventName);    event name not implemented yet
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setStyle("-fx-font-size: 2em");

        //End picker group
        HBox endHeader = new HBox(5);
        hasDuration.setPadding(new Insets(5, 0, 0, 0));
        endHeader.getChildren().addAll(hasDuration, endLabel);

        //Image selection group
        HBox imageChooser = new HBox(10);
        imageChooser.getChildren().addAll(imageInput, uploadButton);
        imageInput.setPrefWidth(200);

        //Buttons at bottom group
        GridPane buttons = new GridPane();
        buttons.addColumn(0, saveButton, deleteButton);
        buttons.add(closeButton, 1, 1);
        buttons.setPadding(new Insets(10,0,0,0));

        for (Node b : buttons.getChildren())
            b.getStyleClass().add("smallButton");

        uploadButton.setStyle("-fx-pref-width: 100px;" +
                "    -fx-pref-height: 30px;" +
                "    -fx-font-size: 1em;");

        //Add to window
        getChildren().addAll(nameLabel,
                titleLabel, titleInput,
                descriptionLabel, descriptionInput,
                startLabel, startInput, endHeader, endInput,
                imageLabel, imageChooser, buttons);

        //Define actions
        endInput.setDisable(true);
        hasDuration.setOnAction(e ->
                endInput.setDisable(!hasDuration.isSelected())
        );

        uploadButton.setOnAction(e ->
                uploadImage()
        );

        saveButton.setOnAction(e ->
                saveEvent()
        );

        deleteButton.setOnAction(e ->
                deleteEvent()
        );

        closeButton.setOnAction(e ->
                close()
        );

        populateDisplay();
    }

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

    private boolean saveEvent() {
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
        System.out.println("Button pressed.");
        return false;
    }

    private boolean deleteEvent() {
        // prompt if user wants to continue
        // delete event from DB, on this and all other timelines
        System.out.println("Button pressed.");
        return false;
    }


    private void close() {
        //close editor, return to previous screen
        System.out.println("Button pressed.");
    }
}