import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class GUIManager extends Application {

    //currently logged in user, null if no log in
    public static User loggedInUser;
    public static Stage mainStage;
    public static MenuBar menu;
    public static VBox main;

    public static void main(String[] args) {
        launch(args);
    }

    public static void swapScene(String fxml) throws IOException {
        main.getChildren().set(1, FXMLLoader.load(GUIManager.class.getResource("FXML/" + fxml + ".fxml")));
    }

    public static void applyStyle(String style) {
        mainStage.getScene().getStylesheets().add("File:src/main/resources/styles/" + style + ".css");
    }

    //default window set up
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Used to establish connection to the DB.
        try {
            new DBM();
            DBM.setupSchema(); //comment out for testing of log in
        } catch (SQLException e) {
            e.printStackTrace();
        }

        main = new VBox();

        menu = FXMLLoader.load(GUIManager.class.getResource("FXML/TopMenu.fxml"));
        main.getChildren().addAll(menu, new Pane());

        mainStage = primaryStage;
        mainStage.setScene(new Scene(main));

        swapScene("EventEditor");
        applyStyle("DefaultStyle");

        mainStage.show();
    }

    // This method creates the dropdown menus in the top right of most windows
    public static VBox dropDownMenus() {
        HBox menus = new HBox();

        // These are the items in the File dropdown menu
        MenuItem save = new MenuItem("Save");
        save.setOnAction(e -> System.out.println("The \"Save\" menu button has been pressed."));

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> System.out.println("The \"Delete\" menu button has been pressed."));

        MenuItem zoom = new MenuItem("Zoom");
        zoom.setOnAction(e -> System.out.println("The \"Zoom\" menu button has been pressed."));

        // This is the File dropdown menu in the top left
        Menu menuFile = new Menu("File");
        menuFile.getItems().addAll(save, delete, zoom);

        // This is the only item in the Edit dropdown menu
        MenuItem editMode = new MenuItem("Edit Mode");
        editMode.setOnAction(e -> System.out.println("The \"Edit Mode\" menu button has been pressed."));

        // This is the Edit dropdown menu in the top left
        Menu menuEdit = new Menu("Edit");
        menuEdit.getItems().addAll(editMode);

        // This is the only item in the View dropdown menu
        MenuItem viewMode = new MenuItem("View Mode");
        viewMode.setOnAction(e -> System.out.println("The \"View Mode\" menu button has been pressed."));

        // This is the View dropdown menu in the top left
        Menu menuView = new Menu("View");
        menuView.getItems().addAll(viewMode);

        // This is the bar that holds the dropdown menus in the top left
        MenuBar bar = new MenuBar();
        bar.getMenus().addAll(menuFile, menuEdit, menuView);

        // spacer approach adapted from https://stackoverflow.com/a/39282816
        // spacer to push the loggedInText to the right of the screen
        Region spacer = new Region();
        spacer.getStyleClass().add("menu-bar");
        HBox.setHgrow(spacer, Priority.SOMETIMES);

        // This is the bar that holds the logged-in-info
        MenuBar loggedInBar = new MenuBar();

        // set text depending on login session
        Menu loggedInText = new Menu();
        if (null == GUIManager.loggedInUser) {
            loggedInText.setText("Not logged in");
            loggedInText.setDisable(true);
        } else {
            loggedInText.setText("Logged in as: " + GUIManager.loggedInUser.getUserEmail());
            loggedInText.setDisable(false);
        }

        MenuItem logout = new MenuItem("Logout");
        logout.setOnAction(event -> {
            GUIManager.loggedInUser=null;
            //GUIManager.swapScene(LoginAndRegistration_GUI.welcomeScreen());
        });
        loggedInText.getItems().addAll(logout);


        loggedInBar.getMenus().add(loggedInText);

        menus.getChildren().addAll(bar, spacer, loggedInBar);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(menus);

        return vbox;
    }

}
