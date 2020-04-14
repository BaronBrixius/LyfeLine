import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIManager extends Application{

	public static void main(String[] args) {
		launch(args);
	}
	
	public static Stage myStage;
	
	//default window set up
	@Override
	public void start(Stage primaryStage) throws Exception {
		myStage = primaryStage;
		myStage.setResizable(false);
		myStage.setScene(AdminRoleManager_GUI.AdminRoleManager()); //will be welcome screen as default - needs to be changed
		myStage.show();
	}
	
	//is used when swapping scenes inside classes. use the static classes that return scenes
	public static void swapScene(Scene scene) {
		myStage.setScene(scene);
	}
}
