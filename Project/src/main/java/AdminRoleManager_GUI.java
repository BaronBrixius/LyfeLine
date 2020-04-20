import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public abstract class AdminRoleManager_GUI extends Application {
	static ListView<User> userListView;

	public static Scene AdminRoleManager() {
		GridPane pane = new GridPane();

		pane.setVgap(5);
		pane.setHgap(5);
		pane.setPadding(new Insets(10, 10, 10, 10));

		final ObservableList<User> userList = FXCollections.observableArrayList(); 
		try {
			List<User> usersFromDB = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM users "), new User());
			for(User u : usersFromDB) {
				userList.add(u);
			}
		} catch (SQLException e) {
			
		}

		// custom toggleswitch
		AdminToggleSwitch toggle = new AdminToggleSwitch(userList);
		GridPane.setColumnSpan(toggle, 3);


		// informative text for toggle function
		Text textToggle = new Text("Toggle admin");



		/*
		// background for user information & toggle function
		Rectangle bg = new Rectangle(300, 240);
		bg.setFill(Color.WHITE);
		bg.setStroke(Color.LIGHTBLUE);
		bg.setStrokeWidth(2);
		bg.setTranslateX(18);
		bg.setTranslateY(50);
		bg.setArcWidth(7);
		bg.setArcHeight(7);
		GridPane.setRowSpan(bg, 3);
		*/

		// displays status of user (User/ admin)
		Text textStatus = new Text();
		textStatus.textProperty()
				.bind(Bindings.when(toggle.switchedOn).then("Status: ADMIN").otherwise("Status: USER"));

		// default sort order
		userList.sort(Comparator.comparing(User::getUserName));

		// user information
		Text textUser = new Text("User: " + userList.get(0).getUserEmail());



		// list display of timelines
		userListView = new ListView<>();

		// approach adapted from https://stackoverflow.com/a/36657553
		userListView.setCellFactory(param -> new ListCell<User>() {
			@Override
			protected void updateItem(User item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null || item.getUserEmail() == null) {
					setText(null);
				} else {
					setText("ID: " + item.getUserID() + " - " + item.getUserEmail());
				}
			}
		});

		userListView.setItems(userList);
		userListView.setPrefWidth(300);
		userListView.getSelectionModel().select(0);

		GridPane.setRowSpan(userListView, 3);

		// search field
		TextField searchInput = new TextField("search here");
		searchInput.focusedProperty().addListener(ov -> {
			if (searchInput.isFocused())
				searchInput.setText("");
		});

		// sort order selection
		ComboBox<String> sortBy = new ComboBox<String>();
		sortBy.setPrefWidth(300);
		sortBy.setValue("Sort By");
		ObservableList<String> sortOptions = FXCollections.observableArrayList();
		sortOptions.add("Alphabetically");
		sortOptions.add("Reverse-Alphabetically");
		sortOptions.add("User ID");
		sortOptions.add("Reverse User ID");
		sortBy.setItems(sortOptions);
		

		// back button
		Button btnBack = new Button("Back");
		btnBack.getStyleClass().add("smallButton");
		btnBack.setOnAction(event -> {
			GUIManager.swapScene(Dashboard_GUI.DashboardScreen());
			GUIManager.mainStage.setTitle("Dashboard");
		});

		// sort order selection events
		sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> {
			switch (sortBy.getSelectionModel().getSelectedIndex()) {
			case 0:
				userList.sort((t1, t2) -> (t1.getUserName().compareTo(t2.getUserName())));
				break;
			case 1:
				userList.sort((t1, t2) -> (t2.getUserName().compareTo(t1.getUserName())));
				break;
			case 2:
				userList.sort((t1, t2) -> (Integer.compare(t1.getUserID(), t2.getUserID())));
				break;
			case 3:
				userList.sort((t1, t2) -> (Integer.compare(t2.getUserID(), t1.getUserID())));
				break;
			}
		});

		userListView.getSelectionModel().selectedIndexProperty().addListener(ov -> {

			if (userListView.getSelectionModel().getSelectedIndex() >= 0) {
				textUser.setText(
						"User: " + userList.get(userListView.getSelectionModel().getSelectedIndex()).getUserEmail());

				toggle.switchedOn.set(userList.get(userListView.getSelectionModel().getSelectedIndex()).getAdmin());
			}
		});

		Region spacer = new Region();
		
		VBox userInfo = new VBox();
		userInfo.setMinWidth(300);
		userInfo.setSpacing(10);
		userInfo.getChildren().addAll(textUser,textStatus,toggle,textToggle,spacer,btnBack);
		userInfo.setBorder(new Border(new BorderStroke(Color.web("#1aa9cd"),BorderStrokeStyle.SOLID, new CornerRadii(3.0), new BorderWidths(2))));
		userInfo.setPadding(new Insets(10, 10, 10, 10));
		VBox.setVgrow(spacer, Priority.SOMETIMES);
		
		
		
		textUser.getStyleClass().add("mediumText");
		textStatus.getStyleClass().add("mediumText");
		textToggle.getStyleClass().add("mediumText");
		
		VBox userListBox = new VBox();
		userListBox.setSpacing(10);
		userListBox.setPadding(new Insets(10, 10, 10, 10));
		userListBox.getChildren().addAll(sortBy,searchInput,userListView);
		
		pane.add(userInfo, 0, 0);
		pane.add(userListBox, 1, 0);
		
		
		VBox everything = LoginAndRegistration_GUI.dropDownMenus();
		everything.getChildren().add(pane);
		
		Scene scene = new Scene(everything);

		return scene;
	}

	static class AdminToggleSwitch extends ToggleSwitch {

		private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

		public AdminToggleSwitch(ObservableList<User> userList) {
			switchedOn.setValue(userList.get(0).getAdmin());

			Rectangle background = new Rectangle(100, 50);
			background.setArcWidth(50);
			background.setArcHeight(50);
			background.setFill(Color.WHITE);
			background.setStroke(Color.LIGHTGRAY);

			Circle trigger = new Circle(25, 25, 25);
			trigger.setFill(Color.WHITE);
			trigger.setStroke(Color.LIGHTGRAY);
			trigger.setEffect(new DropShadow(2, Color.valueOf("0x000000ff")));

			TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
			translateAnimation.setNode(trigger);

			FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));
			fillAnimation.setShape(background);

			ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

			getChildren().addAll(background, trigger);

			trigger.setTranslateX(switchedOn.get() ? 100 - 50 : 0);
			background.setFill(switchedOn.get() ? Color.LIGHTGREEN : Color.WHITE);
			trigger.setFill(switchedOn.get() ? Color.WHITE : Color.DARKRED);

			switchedOn.addListener((obs, oldState, newState) -> {
				setDisable(true);

				translateAnimation.setToX(newState ? 100 - 50 : 0);
				fillAnimation.setFromValue(newState ? Color.WHITE : Color.LIGHTGREEN);
				fillAnimation.setToValue(newState ? Color.LIGHTGREEN : Color.WHITE);
				trigger.setFill(newState ? Color.WHITE : Color.DARKRED);

				animation.play();
				animation.setOnFinished(e -> setDisable(false));
			});

			setOnMouseClicked(event -> { // add functionality here
				switchedOn.set(!switchedOn.get());
				try {
					userList.get(userListView.getSelectionModel().getSelectedIndex()).toggleAdmin();
					DBM.updateInDB(userList.get(userListView.getSelectionModel().getSelectedIndex()));
				} catch (SQLException ignored) {
				}
			});

		}
	}
}
