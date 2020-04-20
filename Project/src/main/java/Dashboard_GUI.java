import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Dashboard_GUI {

	public static Scene DashboardScreen() {
		//everything including menu bar
		VBox everything = new VBox();
		// main layout
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(5);
		pane.setPadding(new Insets(10, 10, 10, 10));

		// holds timelines from DB
		ObservableList<Timeline> timelines = FXCollections.observableArrayList();
		List<Timeline> timelinesFromDB = null;

		try {
			PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
			timelinesFromDB = DBM.getFromDB(stmt, new Timeline());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Timeline t : timelinesFromDB) {
			timelines.add(t);
		}

		// default sort order
		timelines.sort((t1, t2) -> (t1.getName().compareTo(t2.getName())));

		// list display of timelines
		ListView<Timeline> list = new ListView<Timeline>(timelines);

		// approach adapted from https://stackoverflow.com/a/36657553
		list.setCellFactory(param -> new ListCell<Timeline>() {
			@Override
			protected void updateItem(Timeline item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null || item.getName() == null) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});

		list.setMinWidth(200);
		list.getSelectionModel().select(0);
		pane.add(list, 2, 0);

		// layout of dashboard options / only for scene switch purposes for now
		VBox dashboardOptions = new VBox();
		dashboardOptions.setSpacing(10);
		Button adminGUI = new Button("Admin Manager");
		adminGUI.getStyleClass().add("smallButton");
		adminGUI.setMinWidth(150);
		dashboardOptions.getChildren().add(adminGUI);
		adminGUI.setOnAction(event -> {
			GUIManager.swapScene(AdminRoleManager_GUI.AdminRoleManager());
			GUIManager.mainStage.setTitle("Admin Manager");

		});
		pane.add(dashboardOptions, 0, 0);

		// layout of column to the left of the listview
		VBox listOptions = new VBox();
		listOptions.setSpacing(10);

		// search field
		TextField searchInput = new TextField("search here... not yet implemented");
		searchInput.focusedProperty().addListener(ov -> {
			if (searchInput.isFocused())
				searchInput.setText("");
		});
		listOptions.getChildren().add(searchInput);

		// sort order selection
		ComboBox<String> sortBy = new ComboBox<String>();
		sortBy.setValue("Sort By");
		ObservableList<String> sortOptions = FXCollections.observableArrayList();
		sortOptions.add("Alphabetically");
		sortOptions.add("Reverse-Alphabetically");
		sortOptions.add("Most Recent");
		sortOptions.add("Oldest");
		sortBy.setItems(sortOptions);
		listOptions.getChildren().add(sortBy);

		Button btnLogOut = new Button("Log Out");
		btnLogOut.getStyleClass().add("smallButton");
		btnLogOut.getStyleClass().add("logOutButton");
		pane.add(btnLogOut, 2, 2);

		btnLogOut.setOnAction(event -> {
			GUIManager.loggedInUser=null;
			GUIManager.swapScene(LoginAndRegistration_GUI.welcomeScreen());
		});

		pane.add(listOptions, 1, 0);

		// sort order selection events
		sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> {
			switch (sortBy.getSelectionModel().getSelectedIndex()) {
			case 0:
				timelines.sort((t1, t2) -> (t1.getName().compareTo(t2.getName())));
				break;
			case 1:
				timelines.sort((t1, t2) -> (t2.getName().compareTo(t1.getName())));
				break;
			case 2:
				timelines.sort((t1, t2) -> (t2.getDateCreated().compareTo(t1.getDateCreated())));
				break;
			case 3:
				timelines.sort((t1, t2) -> (t1.getDateCreated().compareTo(t2.getDateCreated())));
				break;
			}
		});

		pane.setAlignment(Pos.CENTER);
		
		everything.getChildren().addAll(LoginAndRegistration_GUI.dropDownMenus(),pane);

		// Delete timeline button
		Button btnDelete = new Button("Delete");
		btnDelete.getStyleClass().add("smallButton");
		btnDelete.getStyleClass().add("logOutButton");
		pane.add(btnDelete, 2, 2);

		// Popup confirmation
		Stage delConfirm = new Stage();
		delConfirm.setTitle("Confirm Deletion");
		delConfirm.initOwner(GUIManager.mainStage);
		delConfirm.initModality(Modality.WINDOW_MODAL);
		delConfirm.setResizable(false);

		btnDelete.setOnAction(event -> {
			delConfirm.setScene(deletePopup(list.getSelectionModel().getSelectedItem().getName()));
			delConfirm.getScene().getStylesheets().add("File:src/main/resources/" + GUIManager.mainStyle + ".css");
			delConfirm.show();
		});

		// Log out, returns to main menu
		Button btnLogOut = new Button("Log Out");
		btnLogOut.getStyleClass().add("smallButton");
		btnLogOut.getStyleClass().add("logOutButton");
		pane.add(btnLogOut, 0, 2);

		btnLogOut.setOnAction(event -> {
			GUIManager.swapScene(LoginAndRegistration_GUI.welcomeScreen());
		});

		// finalizes and returns scene
		Scene scene = new Scene(everything, 600, 400);
		return scene;

	}

	private static Scene deletePopup(String timelineName) {
		// Row 1 - Info Text
		Text displayTxt = new Text("Delete Timeline " + timelineName + "?");

		
		// Row 2 - Buttons Hbox
		Button btnConfirm = new Button("Confirm");
		btnConfirm.getStyleClass().add("popupButton");
		btnConfirm.getStyleClass().add("hoverRed");
		btnConfirm.setOnAction(event -> ((Node) (event.getSource())).getScene().getWindow().hide());

		Button btnCancel = new Button("Cancel");
		btnCancel.getStyleClass().add("popupButton");
		btnCancel.setOnAction(event -> ((Node) (event.getSource())).getScene().getWindow().hide());

		HBox hboxButtons = new HBox();
		hboxButtons.setSpacing(75);
		hboxButtons.setAlignment(Pos.CENTER);
		hboxButtons.getChildren().addAll(btnConfirm, btnCancel);

		
		// Extra scene params
		VBox layout = new VBox();
		layout.setPadding(new Insets(20, 20, 20, 20));
		layout.setSpacing(35);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(displayTxt, hboxButtons);

		
		return new Scene(layout);
	}

}
