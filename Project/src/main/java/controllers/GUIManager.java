package controllers;

import database.DBM;
import database.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class GUIManager extends Application {

    //currently logged in user, null if no log in
    public static User loggedInUser;
    public static Stage mainStage;
    public static TopMenu menu;
    public static BorderPane main;
    public static FXMLLoader loader;
    //public static Stack<Node> pastPages = new Stack<>(); //Unused, might be handy later, caused problems now.

    public static void main(String[] args) {
        launch(args);
    }

    public static <T> T swapScene(String fxml) throws IOException {
        //pastPages.add(main.getCenter());
        loader = new FXMLLoader(GUIManager.class.getResource("../FXML/" + fxml + ".fxml"));
        main.setCenter(loader.load());
        return loader.getController();
    }

    //public static void previousPage() {
    //    main.getChildren().set(1, pastPages.pop());
    //}

    public static void applyStyle(String style) {
        mainStage.getScene().getStylesheets().remove(0);
        mainStage.getScene().getStylesheets().add("File:src/main/resources/styles/" + style + ".css");
    }

    //default window set up
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Used to establish connection to the DB.
        try {
            new DBM();//
            DBM.setupSchema();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //loggedInUser = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM users"), new User()).get(0);  //delete when merging to dev
        main = new BorderPane();
        loader = new FXMLLoader(getClass().getResource("../FXML/TopMenu.fxml"));
        main.setTop(loader.load());
        menu = loader.getController();

        mainStage = primaryStage;
        mainStage.setScene(new Scene(main));
        swapScene("Welcome");
        //TimelineView systemUnderDevelopment = swapScene("TimelineView");        //delete when merging to dev
        //systemUnderDevelopment.setActiveTimeline(1);

        mainStage.getScene().getStylesheets().add("File:src/main/resources/styles/GeneralConfig.css");
        mainStage.getScene().getStylesheets().add("File:src/main/resources/styles/DarkTheme.css");
        mainStage.show();
    }

    @Override
    public void stop() {
        try {
            DBM.close();        //closes the database connection when mainStage is closed
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
