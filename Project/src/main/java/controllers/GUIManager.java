package controllers;

import database.DBM;
import database.Timeline;
import database.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class GUIManager {

    //currently logged in user, null if no log in
    public static User loggedInUser;
    public static Stage mainStage;
    public static TopMenu menu;
    public static BorderPane main;
    public static FXMLLoader loader;

    public static <T> T swapScene(String fxml) throws IOException {
        loader = new FXMLLoader(GUIManager.class.getResource("../FXML/" + fxml + ".fxml"));
        main.setCenter(loader.load());
        return loader.getController();
    }

    public static void applyStyle(String style) {
        mainStage.getScene().getStylesheets().remove(1);
        mainStage.getScene().getStylesheets().add("File:src/main/resources/styles/" + style + ".css");
        if (loggedInUser != null) {
            loggedInUser.setTheme(style);
            try {
                DBM.updateInDB(loggedInUser);
            } catch (SQLException e) {
                System.out.println("Could not access user database.");
            }
        }
    }

    public static void start(Stage stage) throws Exception {
        //loggedInUser = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM users"), new User()).get(0);  //TODO delete for final
        main = new BorderPane();
        loader = new FXMLLoader(GUIManager.class.getResource("../FXML/TopMenu.fxml"));
        main.setTop(loader.load());
        menu = loader.getController();

        mainStage = stage;
        mainStage.setScene(new Scene(main));
        swapScene("LoginAndRegistration");
        //TimelineView systemUnderDevelopment = swapScene("TimelineView");        //TODO delete for final
        //systemUnderDevelopment.setActiveTimeline(DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM timelines"), new Timeline()).get(0));
        mainStage.getScene().getStylesheets().add("File:src/main/resources/styles/Base.css");
        mainStage.getScene().getStylesheets().add("File:src/main/resources/styles/Default.css");
        FileInputStream icon = new FileInputStream("src/main/resources/LogoIcon.png");
        mainStage.getIcons().add(new Image(icon));
        icon.close();
        mainStage.show();
    }
}
