package controllers;

import database.DBM;
import database.Event;
import database.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;


import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class DashboardTest {
    static private int testCount = 0;
    private final String SCHEMA = "test";
    Dashboard sut;


    //helper methods control who is logged in
    static void setAdminLoggedIn(boolean admin) {
        GUIManager.loggedInUser.setAdmin(admin);
    }

    static void setUserIDLoggedIn(int ownerID) {
        GUIManager.loggedInUser.setID(ownerID);
    }

    @Start
    public void start(Stage stage) throws Exception {
        GUIManager.loggedInUser = new User();
        new DBM(SCHEMA);
        DBM.setupSchema();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../classes/FXML/Dashboard.fxml"));
        stage.setScene(new Scene(loader.load(), 300, 300));
        sut = loader.getController();
        stage.show();
    }

    @BeforeEach
    void setUp() throws Exception {
        System.out.println("Test " + ++testCount);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testInitialize() {
    }

    @Test
    void testAdminScreen() {
    }

    @Test
    void testOnlyUserTimelines() {
    }

    @Test
    void testCreateTimeline() {
    }

    @Test
    void testEditTimeline() {
    }

    @Test
    void testOpenTimeline() {
    }

    @Test
    void testDeleteConfirmation() {
    }
}