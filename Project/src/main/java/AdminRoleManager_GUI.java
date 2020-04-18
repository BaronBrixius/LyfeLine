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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public abstract class AdminRoleManager_GUI extends Application {
	static ListView<User> userListView;

	public static Scene AdminRoleManager(){
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// headline
		final Text headLine = new Text("Role Management");
		Font thirty = new Font("Serif", 40);
		headLine.setFont(thirty);
		headLine.setTranslateX(130);
		GridPane.setColumnSpan(headLine, 4);

		// custom toggleswitch
		AdminToggleSwitch toggle = new AdminToggleSwitch(userList);
		GridPane.setColumnSpan(toggle, 3);
		toggle.setTranslateX(40);
		toggle.setTranslateY(40);

		// informative text for toggle function
		Text textToggle = new Text("Toggle admin");
		textToggle.setFont(Font.font(12));
		textToggle.setFill(Color.BLACK);
		textToggle.setTranslateX(57);
		textToggle.setTranslateY(77);

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

		// displays status of user (User/ admin)
		Text textStatus = new Text();
		textStatus.setFont(Font.font(18));
		textStatus.setFill(Color.BLACK);
		textStatus.setTranslateX(40);
		textStatus.setTranslateY(50);
		textStatus.textProperty()
				.bind(Bindings.when(toggle.switchedOn).then("Status: ADMIN").otherwise("Status: USER"));

		// default sort order
		userList.sort(Comparator.comparing(User::getUserName));

		// user information
		Text textUser = new Text("User: " + userList.get(0).getUserEmail());
		textUser.setFont(Font.font(18));
		textUser.setFill(Color.BLACK);
		textUser.setTranslateX(40);
		textUser.setTranslateY(50);

		// list display of timelines
		userListView = new ListView<>();
		userListView.setEditable(false);

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
		userListView.setTranslateY(50);
		userListView.setPrefWidth(300);
		userListView.getSelectionModel().select(0);

		GridPane.setRowSpan(userListView, 3);

		// layout of left column
		VBox listOptions = new VBox();
		listOptions.setSpacing(10);
		listOptions.setTranslateY(50);
		// search field
		TextField searchInput = new TextField("search here");
		searchInput.focusedProperty().addListener(ov -> {
			if (searchInput.isFocused())
				searchInput.setText("");
		});

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

		pane.add(bg, 0, 2);
		pane.add(headLine, 0, 0);
		pane.add(textUser, 0, 2);
		pane.add(listOptions, 4, 2);
		pane.add(userListView, 4, 3);
		pane.add(toggle, 0, 4);
		pane.add(textToggle, 0, 4);
		pane.add(textStatus, 0, 3);
		pane.add(btnBack, 0, 5);

		return new Scene(pane);
	}

	static class AdminToggleSwitch extends ToggleSwitch {

		private final BooleanProperty switchedOn = new SimpleBooleanProperty(false);

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
