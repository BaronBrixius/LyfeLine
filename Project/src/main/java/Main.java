import controllers.GUIManager;
import database.DBM;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Used to establish connection to the DB.
        try {
            new DBM();//
            DBM.setupSchema();
        } catch (SQLException | ClassNotFoundException | FileNotFoundException e) {
            e.printStackTrace();
        }

        GUIManager.start(primaryStage);
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

