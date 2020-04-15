import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIManager extends Application{

	public static void main(String[] args) {
		launch(args);
	}
	
	public static Stage mainStage;
	
	//default window set up
	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		mainStage.setScene(LoginAndRegistration_GUI.welcomeScreen()); //default scene
		mainStage.setResizable(false);
		mainStage.show();
	}
	
	//is used when swapping scenes inside classes. use the static classes that return scenes
	public static void swapScene(Scene scene) {
		mainStage.setScene(scene);
	}
}
