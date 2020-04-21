import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class AdminRoleManager_GUI{

	@FXML
	private Text userText;
	@FXML
	private Text userSatus;
	@FXML
	private ListView<User> listView;
	@FXML
	private CheckBox toggle;
	@FXML
	private ComboBox <String> sortBy;
	
	
	final ObservableList<User> userList = FXCollections.observableArrayList();

	public AdminRoleManager_GUI() {

	}

	@FXML
	public void initialize() {
		// ComboBox items (observable list)
		final ObservableList<String> sortOptions = FXCollections.observableArrayList();
		sortOptions.add("Alphabetically");
		sortOptions.add("Reverse-Alphabetically");
		sortOptions.add("User ID");
		sortOptions.add("Reverse User ID");
		sortBy.setItems(sortOptions);
		
		// approach adapted from https://stackoverflow.com/a/36657553
		listView.setCellFactory(param -> new ListCell<User>() {
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

		fillListView();
		updateCheckBox();

		listView.getSelectionModel().selectedIndexProperty().addListener(ov -> {

			if (listView.getSelectionModel().getSelectedIndex() >= 0) {
				userText.setText(userList.get(listView.getSelectionModel().getSelectedIndex()).getUserEmail());
				updateCheckBox();
			}
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


	}

	public void fillListView() {
		try {
			List<User> usersFromDB = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM users "), new User());
			for (User u : usersFromDB) {
				userList.add(u);
			}
		} catch (SQLException e) {

		}
		listView.setItems(userList);
		listView.getSelectionModel().select(0);
	}

	public void updateCheckBox() {
		boolean admin = listView.getSelectionModel().getSelectedItem().getAdmin(); // get admin status from selected
																					// item

		toggle.setSelected(admin);
	}

	@FXML
	public void toggleClicked() {
		listView.getSelectionModel().getSelectedItem().toggleAdmin();

		try {
			DBM.updateInDB(listView.getSelectionModel().getSelectedItem());
		} catch (SQLException e) {

		}
	}

	@FXML
	public void back() {
		try {
			
			GUIManager.swapScene("Dashboard");
		} catch (IOException e) {
		}
	}

}
