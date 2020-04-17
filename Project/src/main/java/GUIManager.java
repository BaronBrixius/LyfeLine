import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class GUIManager extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public static Stage mainStage;
	public static String mainStyle;



	//default window set up
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Used to establish connection to the DB.
		try {
			new DBM();
			DBM.setupSchema();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}




		mainStage = primaryStage;
		mainStyle = "DefaultStyle";
		swapScene(new LoginAndRegistration_GUI());//default scene

		//mainStage.setResizable(false);
		mainStage.show();
	}

	//is used when swapping scenes inside classes. use the static classes that return scenes
	public static void swapScene(GUI_Interface node) {
		Scene scene = new Scene((Parent) node, 1300,  750);
		mainStage.setScene(scene);
		mainStage.setTitle(node.getWindowName());
		changeStyle(mainStyle);
	}

	public static void changeStyle(String styleName) {
		mainStyle = styleName;
		mainStage.getScene().getStylesheets().add("File:src/main/resources/"+ mainStyle +".css");
	}


}
