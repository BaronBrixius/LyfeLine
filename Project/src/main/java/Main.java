import controllers.GUIManager;
import database.DBM;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        new DBM();                          //establish connection to the DB
        Application.launch(GUIManager.class);     //open the application
    }
}
