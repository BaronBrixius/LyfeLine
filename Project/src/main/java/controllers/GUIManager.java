package controllers;

import database.DBM;
import database.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class GUIManager extends Application {
    public static User loggedInUser;    //currently logged in user, null if no log in
    public static Stage mainStage;
    static TopMenu menu;
    static BorderPane mainPane;
    static FXMLLoader loader;

    public static <T> T swapScene(String fxml) throws IOException {
        loader = new FXMLLoader(GUIManager.class.getResource("../FXML/" + fxml + ".fxml"));
        mainPane.setCenter(loader.load());
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

    @Override
    public void start(Stage stage) throws Exception {
        mainPane = new BorderPane();
        mainPane.setPrefWidth(Screen.getPrimary().getBounds().getWidth() - 30);
        mainPane.setPrefHeight(Screen.getPrimary().getBounds().getHeight() - 90);
        loader = new FXMLLoader(GUIManager.class.getResource("../FXML/TopMenu.fxml"));
        mainPane.setTop(loader.load());
        menu = loader.getController();

        mainStage = stage;
        mainStage.setScene(new Scene(mainPane));
        swapScene("LoginAndRegistration");
        mainStage.getScene().getStylesheets().add("File:src/main/resources/styles/Base.css");
        mainStage.getScene().getStylesheets().add("File:src/main/resources/styles/Default.css");

        FileInputStream icon = new FileInputStream("src/main/resources/LogoIcon.png");
        mainStage.getIcons().add(new Image(icon));
        icon.close();

        mainStage.setMaximized(true);
        mainStage.show();

        new DBM(/*userName, password*/);    //establish connection to the DB
        //DBM.dropSchema();                 //deletes database, useful to reset sometimes
        DBM.firstTimeSetup();               //setup database and dummy data if needed, better after window loads so user feels feedback faster
    }

    @Override
    public void stop() {
        DBM.close();        //closes the database connection when mainStage is closed
    }
}
