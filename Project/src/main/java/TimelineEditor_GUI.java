import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TimelineEditor_GUI extends AnchorPane implements GUI_Interface {

    public TimelineEditor_GUI() {
        this(new Timeline());
    }

    public TimelineEditor_GUI(Timeline timeline) {

        //These are the names of the fields from top to bottom.
        Label titleText = new Label("Title");
        Label descriptionText = new Label("Description");
        Label keywordsText = new Label("Keywords");
        Label startDateText = new Label("Start Date");
        Label endDateText = new Label("End Date");
        Label timeText = new Label("Time Units");

        //These are the input fields from top to bottom.
        TextArea titleInput = new TextArea(timeline.getName());

        TextArea descriptionInput = new TextArea();  //put timeline.getTimelineDescription, or however it's called, into the constructor.


        StringBuilder keywordsList = new StringBuilder();
        /*
        This is how I'm assuming the keywords get into the TextArea when they are implemented.
        Store them as a String Array, and append them one by one to a StringBuilder.
        Append a comma and a space for everyone of them except the last one, then append that one manually.

        for (int i = 0; i < timeline.getKeywords - 1; i++) {
            keywordsList.append(timeline.getKeywords.get(i) + ", ");
        }
        keywordsList.append(timeline.getKeywords.get(timeline.getKeywords.size - 1));
        */
        TextArea keywordsInput = new TextArea(keywordsList.toString());

        TextArea startDateInput = new TextArea();    //timeline.getStartDate in constructor
        startDateInput.getStyleClass().addAll("smallTextArea");

        TextArea endDateInput = new TextArea();  //timeline.getEndDate in constructor
        endDateInput.getStyleClass().addAll("smallTextArea");

        ObservableList<String> timeUnits = FXCollections.observableArrayList();
        timeUnits.addAll("Seconds", "Minutes", "Hours", "Days", "Years");
        ComboBox<String> timeInput = new ComboBox<>(timeUnits);



        //This VBox holds all the input fields and their names
        VBox infoFields = new VBox(12);
        infoFields.getChildren().addAll(titleText, titleInput, descriptionText, descriptionInput,
                keywordsText, keywordsInput, startDateText, startDateInput, endDateText, endDateInput, timeText, timeInput);

        //Make it a bit smaller to fit nicely into the background rectangle, I'll make it pretty later I promise.
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

        //This button returns to the previous screen without saving anything.
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> GUIManager.swapScene(new LoginAndRegistration_GUI()));

        //This HBox holds the Save and Cancel buttons.
        HBox buttons = new HBox(5);
        buttons.getChildren().addAll(saveButton, cancelButton);

        //This VBox holds the drop down menus.
        VBox dropDownMenu = new DropDownMenu();

        this.getChildren().addAll(dropDownMenu, background, infoFields, buttons);

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

    @Override
    public String getWindowName() {
        return "Timeline Editor";
    }

    public class DropDownMenu extends VBox {
        //This method creates the dropdown menus in the top right of most windows
        public DropDownMenu() {
            //These are the items in the File dropdown menu
            MenuItem save = new MenuItem("Save");
            save.setOnAction(e -> System.out.println("The \"Save\" menu button has been pressed."));

            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(e -> System.out.println("The \"Delete\" menu button has been pressed."));

            MenuItem zoom = new MenuItem("Zoom");
            zoom.setOnAction(e -> System.out.println("The \"Zoom\" menu button has been pressed."));

            //This is the File dropdown menu in the top left
            Menu menuFile = new Menu("File");
            menuFile.getItems().addAll(save, delete, zoom);

            //This is the only item in the Edit dropdown menu
            MenuItem editMode = new MenuItem("Edit Mode");
            editMode.setOnAction(e -> System.out.println("The \"Edit Mode\" menu button has been pressed."));

            //This is the Edit dropdown menu in the top left
            Menu menuEdit = new Menu("Edit");
            menuEdit.getItems().addAll(editMode);

            //This is the only item in the View dropdown menu
            MenuItem viewMode = new MenuItem("View Mode");
            viewMode.setOnAction(e -> System.out.println("The \"View Mode\" menu button has been pressed."));

            //This is the View dropdown menu in the top left
            Menu menuView = new Menu("View");
            menuView.getItems().addAll(viewMode);

            //This is the bar that holds the dropdown menus in the top left
            MenuBar bar = new MenuBar();
            bar.getMenus().addAll(menuFile, menuEdit, menuView);

            getChildren().addAll(bar);
        }
    }

}
