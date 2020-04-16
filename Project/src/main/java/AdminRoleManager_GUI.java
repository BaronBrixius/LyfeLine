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

public abstract class AdminRoleManager_GUI extends Application {
	static ListView<User> userListView;

	public static Scene AdminRoleManager() throws SQLException {
		GridPane pane = new GridPane();

		pane.setVgap(5);
		pane.setHgap(5);
		pane.setPadding(new Insets(10, 10, 10, 10));

		final ObservableList<User> userList = FXCollections
				.observableArrayList(DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM users "), new User()));

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
				.bind(Bindings.when(toggle.switchedOnProperty()).then("Status: ADMIN").otherwise("Status: USER"));

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
				textUser.setText("User: " + userListView.getSelectionModel().getSelectedItem().getUserEmail());
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

		return new Scene(pane);
	}

	static class AdminToggleSwitch extends ToggleSwitch {

		private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

		private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
		private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));

		private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

		public BooleanProperty switchedOnProperty() {
			return switchedOn;
		}

		public AdminToggleSwitch(ObservableList<User> userList) {
			Rectangle background = new Rectangle(100, 50);
			background.setArcWidth(50);
			background.setArcHeight(50);
			background.setFill(Color.WHITE);
			background.setStroke(Color.LIGHTGRAY);

			Circle trigger = new Circle(25);
			trigger.setCenterX(25);
			trigger.setCenterY(25);
			trigger.setFill(Color.DARKRED);
			trigger.setStroke(Color.LIGHTGRAY);

			DropShadow shadow = new DropShadow();
			shadow.setRadius(2);
			trigger.setEffect(shadow);

			translateAnimation.setNode(trigger);
			fillAnimation.setShape(background);

			getChildren().addAll(background, trigger);

			switchedOn.addListener((obs, oldState, newState) -> {
				boolean isOn = newState;

				translateAnimation.setToX(isOn ? 100 - 50 : 0);
				fillAnimation.setFromValue(isOn ? Color.WHITE : Color.LIGHTGREEN);
				fillAnimation.setToValue(isOn ? Color.LIGHTGREEN : Color.WHITE);

				trigger.setFill(isOn ? Color.WHITE : Color.DARKRED);

				animation.play();
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
