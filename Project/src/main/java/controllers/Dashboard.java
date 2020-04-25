package controllers;

import database.*;
import javafx.fxml.FXML;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Dashboard {

	@FXML private Button eventEditorButton;
    @FXML private Button adminGUI;
	@FXML private Button btnDelete;
	@FXML private Button btnEdit;
	@FXML private Button btnCreate;
	@FXML private TextFlow displayInfo;
	@FXML private ListView<Timeline> list;
	@FXML private TextField searchInput;
	@FXML private CheckBox cbOnlyViewPersonalLines;
	@FXML private ComboBox<String> sortBy;
	@FXML private GridPane gridButtons;
	@FXML private Text titleText;

	
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
			updateDisplays();
		});

		titleText.setText("Select a Timeline.");
	}

	@FXML
	public void adminScreen() throws IOException {
		GUIManager.swapScene("AdminRoleManager");
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
		GUIManager.swapScene("TimelineEditor");

	}

	@FXML
	public void editTimeline(ActionEvent event) throws IOException {
		if (activeTimeline != null) {
			TimelineEditor editor = GUIManager.swapScene("TimelineEditor");
			editor.setActiveTimeline(this.activeTimeline);
			editor.populateDisplay();
		}


	}
	
	@FXML
	public void openEventEditor(ActionEvent event) { // created by Jan for meeting with teacher Thursday
		try {
			GUIManager.swapScene("TimelineView");
		} catch (IOException e) {
			e.printStackTrace();
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

		FXMLLoader popupDeletion = new FXMLLoader(GUIManager.class.getResource("../FXML/Popup.fxml"));
		delConfirm.setScene(new Scene(popupDeletion.load()));

		Popup deletionPopup = popupDeletion.getController();
		deletionPopup.setMode(1);
		if (list.getSelectionModel().getSelectedItem() != null && list.getSelectionModel().getSelectedItem().getTimelineOwnerID() == GUIManager.loggedInUser.getUserID()) {
			titleText.setText("");
			deletionPopup.setList(list);
			deletionPopup.setDisplayTxt(
					"Are you sure you want to delete " + list.getSelectionModel().getSelectedItem().getName() + "?");
			delConfirm.show();

		}
	}

	@FXML
	private void updateDisplays() {
		if (list.getSelectionModel().getSelectedItem() != null)
		{
			if (list.getSelectionModel().getSelectedItem().getTimelineOwnerID() == GUIManager.loggedInUser.getUserID())
			{
				btnDelete.setDisable(false);
				btnEdit.setDisable(false);
			}
			else
			{
				btnDelete.setDisable(true);
				btnEdit.setDisable(true);
			}

			int year = list.getSelectionModel().getSelectedItem().getDateCreated().getYear();
			int month = list.getSelectionModel().getSelectedItem().getDateCreated().getMonth();
			int day = list.getSelectionModel().getSelectedItem().getDateCreated().getDay();

			titleText.setText("Title: " + list.getSelectionModel().getSelectedItem().getName()
			+ "\nDescription: " + list.getSelectionModel().getSelectedItem().getTimelineDescription()
			+ "\nDate Created: " + year + "/" + month + "/" + day);

		}
		else
		{
			btnDelete.setDisable(true);
			btnEdit.setDisable(true);
			titleText.setText("Select a Timeline.");
		}
	}
}
