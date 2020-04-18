import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController  extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    Stage stage;
    Scene scene;
    String style;

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
        //mainStage.setScene(LoginAndRegistration_GUI.welcomeScreen()); 	//default scene
        stage.setScene(new Scene(new EventEditor_GUI())); 	//GUI currently being worked on, delete for final version
        stage.setResizable(false);
        changeStyle("DefaultStyle");
        stage.show();
    }

    //is used when swapping scenes inside classes. use the static classes that return scenes
    public void swapScene(Scene scene) {
        stage.setScene(scene);
        changeStyle(style);
    }

    public void changeStyle(String styleName) {
        style = styleName;
        stage.getScene().getStylesheets().add("File:src/main/resources/"+ style +".css");
    }

}