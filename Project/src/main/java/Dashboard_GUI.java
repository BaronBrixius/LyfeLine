import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Dashboard_GUI extends GridPane {

	@FXML private Button adminGUI;
	@FXML private Button btnDelete;
	@FXML private Button btnEdit;
	@FXML private Button btnCreate;
	@FXML private TextFlow displayInfo;
	@FXML private ListView<Timeline> list;
	@FXML private TextField searchInput;
	@FXML private CheckBox cbOnlyViewPersonalLines;
	@FXML private ComboBox sortBy;


	public Dashboard_GUI() {
		ObservableList<Event> events = FXCollections.observableArrayList();
		List<Event> eventsFromDB = null;

		try {
			PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM events WHERE EventOwner = ?");
			stmt.setInt(1,GUIManager.loggedInUser.getUserID());
			eventsFromDB = DBM.getFromDB(stmt, new Event());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Event e : eventsFromDB) {
			events.add(e);
		}

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
		list = new ListView<Timeline>(timelines);

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

		list.getSelectionModel().select(0);


		// layout of dashboard options / only for scene switch purposes for now





		// search field
		TextField searchInput = new TextField("search here... not yet implemented");
		searchInput.focusedProperty().addListener(ov -> {
			if (searchInput.isFocused())
				searchInput.setText("");
		});


		// sort order selection
		sortBy = new ComboBox<String>();
		sortBy.setValue("Sort By");
		ObservableList<String> sortOptions = FXCollections.observableArrayList();
		sortOptions.add("Alphabetically");
		sortOptions.add("Reverse-Alphabetically");
		sortOptions.add("Most Recent");
		sortOptions.add("Oldest");
		sortBy.setItems(sortOptions);





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





	}

	@FXML
	private void intialize() {


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

	public void createTimeline(ActionEvent event) throws IOException {
		GUIManager.swapScene("Timeline_Editor_Screen");
	}

	public void editTimeline(ActionEvent event) throws IOException {
		GUIManager.swapScene("Timeline_Editor_Screen");
	}

	public void deleteConfirmation(ActionEvent event) throws IOException {
		Stage delConfirm = new Stage();
		delConfirm.setTitle("Confirm Deletion");
		delConfirm.initOwner(GUIManager.mainStage);
		delConfirm.initModality(Modality.WINDOW_MODAL);
		delConfirm.setResizable(false);


		delConfirm.setScene(FXMLLoader.load(GUIManager.class.getResource("FXML/Login_Screen.fxml")));
		delConfirm.getScene().getStylesheets().add("File:src/main/resources/styles/DefaultStyle.css");
		delConfirm.show();

	}

	public void adminScreen(ActionEvent event) {
		OldGUIManager.swapScene(new AdminRoleManager_GUI());
		OldGUIManager.mainStage.setTitle("Admin Manager");
	}
}
