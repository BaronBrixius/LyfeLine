package controllers;

import database.DBM;
import database.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Stack;

public class GUIManager extends Application {

    //currently logged in user, null if no log in
    public static User loggedInUser;
    public static Stage mainStage;
    public static TopMenu menu;
    public static VBox main;
    public static FXMLLoader loader;
    public static Stack<Node> pastPages = new Stack<>();

    public static void main(String[] args) {
        launch(args);
    }

    public static <T> T swapScene(String fxml) throws IOException {
        pastPages.add(main.getChildren().get(1));
        loader = new FXMLLoader(GUIManager.class.getResource("../FXML/" + fxml + ".fxml"));
        main.getChildren().set(1, loader.load());
        return loader.getController();
    }

    public static void previousPage() {
        main.getChildren().set(1, pastPages.pop());
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

        loader = new FXMLLoader(getClass().getResource("../FXML/TopMenu.fxml"));


        main.getChildren().addAll(loader.load(), new Pane());

        menu = loader.getController();
        menu.updateLoggedInStatus();

        mainStage = primaryStage;
        mainStage.setScene(new Scene(main));

        swapScene("Welcome");
        applyStyle("DefaultStyle");

        mainStage.show();
    }

}