import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class GUIManager extends Application {

	//currently logged in user, null if no log in
	public static User loggedInUser;
	public static Stage mainStage;
    public static Scene mainScene;

    public static void main(String[] args) {
        launch(args);
    }

    public static void swapScene(String fxml) throws IOException {
        mainScene.setRoot(FXMLLoader.load(GUIManager.class.getResource("FXML/" + fxml + ".fxml")));
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
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

        mainStage = primaryStage;
        mainScene = new Scene(FXMLLoader.load(GUIManager.class.getResource("FXML/Welcome_Screen.fxml")));     //default page
        mainStage.setScene(mainScene);
        applyStyle("DefaultStyle");
        mainStage.show();
    }

}
