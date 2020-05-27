import controllers.GUIManager;
import database.DBM;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new DBM();                          //establish connection to the DB
        GUIManager.start(primaryStage);     //open the application
        DBM.dropSchema();                 //deletes database, useful to reset sometimes   //TODO delete for final version
        DBM.firstTimeSetup();               //setup database and dummy data if needed
    }

    @Override
    public void stop() {
        DBM.close();        //closes the database connection when mainStage is closed
    }
}