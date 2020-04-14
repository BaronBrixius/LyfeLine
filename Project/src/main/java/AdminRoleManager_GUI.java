import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public abstract class AdminRoleManager_GUI extends Application {

	public static Scene AdminRoleManager() throws Exception {
		GridPane pane = new GridPane();
		
		pane.setVgap(5);
		pane.setHgap(5);
		pane.setPadding(new Insets(10, 10, 10, 10));
		
		//headline
		final Text headLine = new Text("Role Management");
		Font thirty = new Font("Serif", 40);
		headLine.setFont(thirty);
		headLine.setTranslateX(130);
		GridPane.setColumnSpan(headLine, 4);
		
		//custom toggleswitch
		ToggleSwitch toggle = new ToggleSwitch();
		GridPane.setColumnSpan(toggle, 3);
		toggle.setTranslateX(40);
		toggle.setTranslateY(40);
		
		// informative text for toggle function
		Text textToggle = new Text("Toggle admin");
		textToggle.setFont(Font.font(12));
		textToggle.setFill(Color.BLACK);
		textToggle.setTranslateX(57);
		textToggle.setTranslateY(77);
		
		
		//background for user information & toggle function
		Rectangle bg = new Rectangle(170, 240);
		bg.setFill(Color.WHITE);
		bg.setStroke(Color.LIGHTGREEN);
		bg.setTranslateX(18);
		bg.setTranslateY(50);
		bg.setArcWidth(50);
        bg.setArcHeight(50);
        GridPane.setRowSpan(bg, 3);
        
        // displays status of user (User/ admin)
		Text textStatus = new Text();
		textStatus.setFont(Font.font(18));
		textStatus.setFill(Color.BLACK);
		textStatus.setTranslateX(40);
		textStatus.setTranslateY(50);
		textStatus.textProperty().bind(Bindings.when(toggle.switchedOnProperty()).then("Status: ADMIN").otherwise("Status: USER"));

		// holds users from DB
		ObservableList<User> users = FXCollections.observableArrayList();
		users.add(new User("Jan", "Jan@gmail.com", "12345"));
		users.add(new User("Lorenz", "Lorenz@gmail.com", "12345"));
		users.add(new User("Lasse", "Lasse@gmail.com", "12345"));
		users.add(new User("Haraldur", "Haraldur@gmail.com", "12345"));
		users.add(new User("Tim", "Tim@gmail.com", "12345"));
		users.add(new User("Ben", "Ben@gmail.com", "12345"));
		users.add(new User("Chris", "Chris@gmail.com", "12345"));
		users.add(new User("Dillon", "Dilon@gmail.com", "12345"));
		users.add(new User("Firas", "Firas@gmail.com", "12345"));
		users.add(new User("Max", "Max@gmail.com", "12345"));
		users.add(new User("Vytautas", "Vytautas@gmail.com", "12345"));

		// default sort order
		users.sort((t1, t2) -> (t1.getUserName().compareTo(t2.getUserName())));
		
		// user information
		Text textUser = new Text("User: " + users.get(0).getUserName());
		textUser.setFont(Font.font(18));
		textUser.setFill(Color.BLACK);
		textUser.setTranslateX(40);
		textUser.setTranslateY(50);
		
		

		// list display of timelines
		ListView<User> userList = new ListView<User>();
		userList.setEditable(false);
		userList.setItems(users);
		
		
		
		GridPane.setRowSpan(userList, 3);

		// layout of left column
		VBox listOptions = new VBox();
		listOptions.setSpacing(10);

		// search field
		TextField searchInput = new TextField("search here");
		

		// sort order selection
		ComboBox<String> sortBy = new ComboBox<String>();
		sortBy.setValue("Sort By");
		ObservableList<String> sortOptions = FXCollections.observableArrayList();
		sortOptions.add("Alphabetically");
		sortOptions.add("Reverse-Alphabetically");
		sortOptions.add("User ID");
		sortOptions.add("Reverse User ID");
		sortBy.setItems(sortOptions);
		listOptions.getChildren().addAll(sortBy, searchInput);

		try {
			// sort order selection events
			sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> {
				switch (sortBy.getSelectionModel().getSelectedIndex()) {
				case 0:
					users.sort((t1, t2) -> (t1.getUserName().compareTo(t2.getUserName())));
					break;
				case 1:
					users.sort((t1, t2) -> (t2.getUserName().compareTo(t1.getUserName())));
					break;
				case 2:
					users.sort((t1, t2) -> (Integer.compare(t1.getUserID(), t2.getUserID())));
					break;
				case 3:
					users.sort((t1, t2) -> (Integer.compare(t2.getUserID(), t1.getUserID())));
					break;
				}
			});

			userList.getSelectionModel().selectedIndexProperty().addListener(ov -> {
				textUser.setText("User: " + users.get(userList.getSelectionModel().getSelectedIndex()).getUserName());
			});

		} catch (IndexOutOfBoundsException ex) {
			
		}
		
		pane.add(bg, 0, 2);
		pane.add(headLine, 0, 0);
		pane.add(textUser, 0, 2);
		pane.add(listOptions, 4, 2);
		pane.add(userList, 4, 3);
		pane.add(toggle, 0, 4);
		pane.add(textToggle, 0, 4);
		pane.add(textStatus, 0, 3);
		
		
		return new Scene(pane);
	}
}