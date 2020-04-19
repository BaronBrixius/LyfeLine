import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class MainController extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    static Stage stage;
    static String style = "DefaultStyle";

    //default window set up
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Used to establish connection to the DB.
		/*try {
			new DBM();
			DBM.setupSchema();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}*/

        stage = primaryStage;
        stage.setResizable(false);

        //swapScene("WelcomeScreen.fxml"); 	//default scene
        swapScene("FXML/EventEditor.fxml"); 	//GUI element currently being worked on, delete for final version
        changeStyle(style);
    }

    public static void swapScene(String fxml) throws IOException {
        Parent root = FXMLLoader.load(MainController.class.getResource(fxml));
        stage.setScene(new Scene(root));
        //changeStyle(style);
        stage.show();
    }

    public static void changeStyle(String styleName) {
        style = styleName;
        stage.getScene().getStylesheets().add("File:src/main/resources/"+ style +".css");
    }

}