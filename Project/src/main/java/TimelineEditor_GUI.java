import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TimelineEditor_GUI extends AnchorPane {

    //These are the names of the fields from top to bottom.
    private Label titleText;
    private Label descriptionText;
    private Label keywordsText;
    private Label startDateText;
    private Label endDateText;
    private Label timeText;

    //These are the input fields from top to bottom.
    private TextArea titleInput;
    private TextArea descriptionInput;
    private TextArea keywordsInput;
    private TextArea startDateInput;
    private TextArea endDateInput;
    private ComboBox<String> timeInput;

    public TimelineEditor_GUI() {
        this(new Timeline());
    }

    public TimelineEditor_GUI(Timeline timeline) {

        //These are the names of the fields from top to bottom.
        titleText = new Label("Title");
        descriptionText = new Label("Description");
        keywordsText = new Label("Keywords");
        startDateText = new Label("Start Date");
        endDateText = new Label("End Date");
        timeText = new Label("Time Units");

        //These are the input fields from top to bottom.
        titleInput = new TextArea(timeline.getName());

        descriptionInput = new TextArea();//timeline.timelineDescription

        keywordsInput = new TextArea();//timeline.timelineKeywords

        startDateInput = new TextArea();
        startDateInput.getStyleClass().addAll("smallTextArea");

        endDateInput = new TextArea();
        endDateInput.getStyleClass().addAll("smallTextArea");

        ObservableList<String> timeUnits = FXCollections.observableArrayList();
        timeUnits.addAll("Seconds", "Minutes", "Hours", "Days", "Years");
        timeInput = new ComboBox<>(timeUnits);



        //This VBox holds all the input fields and their names
        VBox infoFields = new VBox(12);
        infoFields.getChildren().addAll(titleText, titleInput, descriptionText, descriptionInput,
                keywordsText, keywordsInput, startDateText, startDateInput, endDateText, endDateInput, timeText, timeInput);
        //Make it a bit smaller to fit nicely into the background rectangle
        infoFields.setScaleX(.9);
        infoFields.setScaleY(.9);

        //This is that background rectangle I was talking about.
        Rectangle background = new Rectangle(0, 0,400,710);
        background.setFill(Color.GRAY);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(4);

        //This button will eventually finalize the creation of the Timeline.
        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> System.out.println("The Save button has been pushed."));

        //This button will eventually return to the previous screen without saving anything.
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> System.out.println("The Cancel button has been pushed."));

        //This HBox holds the Save and Cancel buttons.
        HBox buttons = new HBox(5);
        buttons.getChildren().addAll(saveButton, cancelButton);

        //This VBox holds the drop down menus.
        VBox dropDownMenu = LoginAndRegistration_GUI.dropDownMenus();



        getChildren().addAll(dropDownMenu, background, infoFields, buttons);

        //The drop down menu runs across the top of the window from left to right.
        setTopAnchor(dropDownMenu, 0.0);
        setRightAnchor(dropDownMenu, 0.0);
        setLeftAnchor(dropDownMenu, 0.0);

        //The background and the information fields are held in the top left of the window.
        setLeftAnchor(background, 5.0);
        setTopAnchor(background, 33.0);

        setTopAnchor(infoFields, 3.0);

        //The buttons are held in the lower right of the window.
        setRightAnchor(buttons, 5.0);
        setBottomAnchor(buttons, 10.0);
    }



    //public Scene timelineCreationScreen() {
//
    //    //These are the names of the fields from top to bottom.
    //    Text titleText = new Text("Title");
    //    Text descriptionText = new Text("Description");
    //    Text keywordsText = new Text("Keywords");
    //    Text rangeText = new Text("Range");
    //    Text timeText = new Text("Time");
//
    //    //These are the input fields from top to bottom.
    //    TextArea titleInput = new TextArea();
    //    TextArea descriptionInput = new TextArea();
    //    TextArea keywordsInput = new TextArea();
    //    TextArea rangeInput = new TextArea();
    //    TextArea timeInput = new TextArea();
//
    //    //This VBox holds all the input fields and their names
    //    VBox infoFields = new VBox(12);
    //    infoFields.getChildren().addAll(titleText, titleInput, descriptionText, descriptionInput,
    //                    keywordsText, keywordsInput, rangeText, rangeInput, timeText, timeInput);
    //    //Make it a bit smaller to fit nicely into the background rectangle
    //    infoFields.setScaleX(.9);
    //    infoFields.setScaleY(.9);
//
    //    //This is that background rectangle I was talking about.
    //    Rectangle background = new Rectangle(0, 0,400,710);
    //    background.setFill(Color.GRAY);
    //    background.setStroke(Color.BLACK);
    //    background.setStrokeWidth(4);
//
    //    //This button will eventually finalize the creation of the Timeline.
    //    Button saveButton = new Button("Save");
    //    saveButton.setOnAction(event -> System.out.println("The Save button has been pushed."));
//
    //    //This button will eventually return to the previous screen without saving anything.
    //    Button cancelButton = new Button("Cancel");
    //    cancelButton.setOnAction(event -> System.out.println("The Cancel button has been pushed."));
//
    //    //This HBox holds the Save and Cancel buttons.
    //    HBox buttons = new HBox(5);
    //    buttons.getChildren().addAll(saveButton, cancelButton);
//
    //    //This VBox holds the drop down menus.
    //    VBox dropDownMenu = LoginAndRegistration_GUI.dropDownMenus();
//
//
    //    //This AnchorPane places everything exactly where it needs to be in relation to the edges of the window.
    //    AnchorPane pane = new AnchorPane(dropDownMenu, background, infoFields, buttons);
//
    //    //The drop down menu runs across the top of the window from left to right.
    //    AnchorPane.setTopAnchor(dropDownMenu, 0.0);
    //    AnchorPane.setRightAnchor(dropDownMenu, 0.0);
    //    AnchorPane.setLeftAnchor(dropDownMenu, 0.0);
//
    //    //The background and the information fields are held in the top left of the window.
    //    AnchorPane.setLeftAnchor(background, 5.0);
    //    AnchorPane.setTopAnchor(background, 33.0);
//
    //    AnchorPane.setTopAnchor(infoFields, 3.0);
//
    //    //The buttons are held in the lower right of the window.
    //    AnchorPane.setRightAnchor(buttons, 5.0);
    //    AnchorPane.setBottomAnchor(buttons, 10.0);
//
//
    //    return new Scene(pane, 1300, 750);
    //}

}
