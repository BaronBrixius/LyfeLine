import javafx.fxml.FXML;

import java.awt.event.MouseEvent;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;

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

	public void initialize() {

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

		sorting();

		// Initialised sorting
		list.getItems().sort((t1, t2) -> (t1.getName().compareTo(t2.getName())));
	}

	public void sorting() {
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
	}

	@FXML
	public void onlyUserTimelines() {

		if (cbOnlyViewPersonalLines.isSelected()) {
			try {
				PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineOwner = ?");
				stmt.setInt(1, /* GUIManager.loggedInUser.getUserID() */ 1); // uncomment this for real version
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
	public void createTimeline(ActionEvent event) {
	}

	@FXML
	public void editTimeline(ActionEvent event) {
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
		deletionPopup.setDisplayTxt(
				"Are you sure you want to delete " + list.getSelectionModel().getSelectedItem().getName() + "?");
		deletionPopup.setList(list);
		delConfirm.show();

	}

	@FXML
	public void adminScreen(ActionEvent event) {
	}

}
