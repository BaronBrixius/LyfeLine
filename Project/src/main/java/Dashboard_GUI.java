import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class Dashboard_GUI {

	public static Scene DashboardScreen() {

		// main layout
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(5);
		pane.setPadding(new Insets(10, 10, 10, 10));

		// holds timelines from DB
		ObservableList<Timeline> timelines = FXCollections.observableArrayList();

		// temporary example timelines until import is working
		timelines.add(new Timeline("WW2", 3));
		timelines.add(new Timeline("Z", 4));
		timelines.add(new Timeline("CVID", 6));

		// default sort order
		timelines.sort((t1, t2) -> (t1.getName().compareTo(t2.getName())));

		// list display of timelines
		ListView<Timeline> list = new ListView<Timeline>();
		list.setItems(timelines);
		list.setMinWidth(200);
		list.getSelectionModel().select(0);
		pane.add(list, 2, 0);

		//layout of dashboard options / only for scene switch purposes for now
		VBox dashboardOptions = new VBox();
		dashboardOptions.setSpacing(10);
		Button adminGUI = new Button("Admin Manager");
		adminGUI.setMinWidth(150);
		dashboardOptions.getChildren().add(adminGUI);
		adminGUI.setOnAction(event->{
			System.out.println("switch to admin manager gui");
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
				timelines.sort((t1, t2) -> (Integer.compare(t1.getDate(), t2.getDate())));
				break;
			case 3:
				timelines.sort((t1, t2) -> (Integer.compare(t2.getDate(), t1.getDate())));
				break;
			}
		});

		// finalizes and returns scene
		Scene scene = new Scene(pane, 500, 400);
		return scene;

	}

}
