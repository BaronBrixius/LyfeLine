import javafx.fxml.FXML;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Dashboard_GUI {

	@FXML
	private Button adminGUI;
	@FXML
	private Button btnDelete;
	@FXML
	private Button btnEdit;
	@FXML
	private Button btnCreate;
	@FXML
	private TextFlow displayInfo;
	@FXML
	private ListView<Timeline> list;
	@FXML
	private TextField searchInput;
	@FXML
	private CheckBox cbOnlyViewPersonalLines;
	@FXML
	private ComboBox sortBy;
	@FXML
	private GridPane gridButtons;
	private Timeline activeTimeline;

	public void initialize() {
		gridButtons.setVisible(GUIManager.loggedInUser.getAdmin());
		gridButtons.setDisable(!GUIManager.loggedInUser.getAdmin());

		// Fill ListView with the timelines
		try {
			PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
			list.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
		} catch (SQLException e) {
			System.err.println("Could not get timelines from database.");
		}

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

		// Add sorting options
		ObservableList<String> sortOptions = FXCollections.observableArrayList();
		sortOptions.add("Alphabetically");
		sortOptions.add("Reverse-Alphabetically");
		sortOptions.add("Most Recent");
		sortOptions.add("Oldest");
		sortBy.setItems(sortOptions);

		// Sort order selection events
		sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> {
			switch (sortBy.getSelectionModel().getSelectedIndex()) {
			case 0:
				list.getItems().sort((t1, t2) -> (t1.getName().compareTo(t2.getName())));
				break;
			case 1:
				list.getItems().sort((t1, t2) -> (t2.getName().compareTo(t1.getName())));
				break;
			case 2:
				list.getItems().sort((t1, t2) -> (t2.getDateCreated().compareTo(t1.getDateCreated())));
				break;
			case 3:
				list.getItems().sort((t1, t2) -> (t1.getDateCreated().compareTo(t2.getDateCreated())));
				break;
			}
		});

		// Initialised sorting
		list.getItems().sort((t1, t2) -> (t1.getName().compareTo(t2.getName())));

		// Search field
		searchInput.focusedProperty().addListener(ov -> {
			if (searchInput.isFocused())
				searchInput.setText("");
		});

		list.getSelectionModel().selectedIndexProperty().addListener(e -> {
			activeTimeline = list.getSelectionModel().getSelectedItem();
		});
	}

	@FXML
	public void adminScreen(ActionEvent event) throws IOException {
		GUIManager.swapScene("AdminRoleManager");
	}

	@FXML
	public void deleteConfirm(ActionEvent actionEvent) {
		((Node) (actionEvent.getSource())).getScene().getWindow().hide();
		System.out.println("Deleted");
	}

	@FXML
	public void deleteCancel(ActionEvent actionEvent) {
		((Node) (actionEvent.getSource())).getScene().getWindow().hide();
		System.out.println("Cancelled");
	}

	@FXML
	public void onlyUserTimelines() {

		if (cbOnlyViewPersonalLines.isSelected()) {
			try {
				PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineOwner = ?");
				stmt.setInt(1, GUIManager.loggedInUser.getUserID()); // GUIManager.loggedInUser.getUserID() uncomment
																		// this for real version
				list.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
			} catch (SQLException e) {
				System.err.println("Could not get timelines from database.");
			}
		} else {
			try {
				PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
				list.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
				sortBy.getSelectionModel().select(0);
			} catch (SQLException e) {
				System.err.println("Could not get timelines from database.");
			}
		}

	}

	@FXML
	public void createTimeline(ActionEvent event) throws IOException {
		GUIManager.swapScene("Timeline_Editor_Screen");

	}

	@FXML
	public void editTimeline(ActionEvent event) throws IOException {
		if (activeTimeline != null) {
			TimelineEditor_GUI editor = GUIManager.swapScene("Timeline_Editor_Screen");
			editor.setActiveTimeline(activeTimeline);
			System.out.println(activeTimeline.getName());
			System.out.println(activeTimeline.getTimelineDescription());
			System.out.println(activeTimeline.getTimelineID());
		}

	}

	// open DeletePopUp
	@FXML
	public void deleteConfirmation(ActionEvent event) throws IOException {

		Stage delConfirm = new Stage();
		delConfirm.setTitle("Confirm Deletion");
		delConfirm.initOwner(GUIManager.mainStage);

		delConfirm.initModality(Modality.WINDOW_MODAL);
		delConfirm.setResizable(false);

		FXMLLoader popupDeletion = new FXMLLoader(GUIManager.class.getResource("fxml/DeletePopup.fxml"));
		delConfirm.setScene(new Scene(popupDeletion.load()));

		Popup deletionPopup = popupDeletion.getController();
		if (list.getSelectionModel().getSelectedItem() != null && list.getSelectionModel().getSelectedItem()
				.getTimelineOwnerID() == GUIManager.loggedInUser.getUserID()) {
			displayInfo.getChildren().clear();
			deletionPopup.setDisplayTxt(
					"Are you sure you want to delete " + list.getSelectionModel().getSelectedItem().getName() + "?");
			deletionPopup.setList(list);
			delConfirm.show();
		} else if (list.getSelectionModel().getSelectedItem() == null) {
			displayInfo.getChildren().clear();
			Text error = new Text("No timeline selected.");
			error.setFill(Color.RED);
			displayInfo.getChildren().add(error);
		} else if (list.getSelectionModel().getSelectedItem().getTimelineOwnerID() != GUIManager.loggedInUser
				.getUserID()) {
			displayInfo.getChildren().clear();
			Text error = new Text("You are not the owner of this timeline.");
			error.setFill(Color.RED);
			displayInfo.getChildren().add(error);
		}
	}
}
