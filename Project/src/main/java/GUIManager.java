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
    public static User loggedInUser = new User("Seeqwul Encurshun'", "email@yo.mama", "Passw0rd!");
    public static Stage mainStage;
    public static MenuBar menu;
    public static VBox main;

    public static void main(String[] args) {
        loggedInUser.setAdmin(true);
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

        swapScene("Timeline_Editor_Screen");
        applyStyle("DefaultStyle");

        mainStage.show();
    }

}
