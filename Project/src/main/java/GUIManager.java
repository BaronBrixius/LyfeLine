import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class GUIManager extends Application {

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
        //stage.setResizable(false);

        //swapScene("FXML/WelcomeScreen.fxml"); 	//default scene
        swapScene("EventEditor.fxml"); 	//GUI element currently being worked on, delete for final version
        changeStyle(style);
    }

    public static void swapScene(String fxml) throws IOException {
        stage.setScene(new Scene(FXMLLoader.load(GUIManager.class.getResource("fxml/"+fxml))));
        //changeStyle(style);
        stage.show();
    }

    public static void changeStyle(String styleName) {
        style = styleName;
        stage.getScene().getStylesheets().add("File:src/main/resources/"+ style +".css");
    }

}