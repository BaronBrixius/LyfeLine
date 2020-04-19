import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIManager extends Application {

    static Stage stage;
    static Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    public static void swapScene(String fxml) throws IOException {
        scene.setRoot(FXMLLoader.load(GUIManager.class.getResource("fxml/" + fxml + ".fxml")));
    }

    public static void applyStyle(String style) {
        stage.getScene().getStylesheets().add("File:src/main/resources/styles/" + style + ".css");
    }

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
        //scene = new Scene(FXMLLoader.load(GUIManager.class.getResource("fxml/WelcomeScreen.fxml")));     //default page
        scene = new Scene(FXMLLoader.load(GUIManager.class.getResource("fxml/EventEditor.fxml")));   //GUI element currently being worked on, delete for final version
        stage.setScene(scene);
        applyStyle("DefaultStyle");
        stage.show();
    }

}
