package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import utils.Date;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Dashboard {

	@FXML protected Button eventEditorButton;
	@FXML protected Button adminGUI;
	@FXML protected Button btnDelete;
	@FXML protected Button btnEdit;
	@FXML protected Button btnCreate;
	@FXML protected Button searchButton;
	@FXML protected TextFlow displayInfo;
	@FXML protected ListView<Timeline> list;
	@FXML protected TextField searchInput;
	@FXML protected TextField searchTimelineName;
	@FXML protected TextField searchCreator;
	@FXML protected TextField searchKeywords;
	@FXML protected ComboBox<String> searchRating;
	@FXML protected Button clearButton;
	@FXML protected CheckBox cbOnlyViewPersonalLines;
	@FXML protected ComboBox<String> sortBy;
	@FXML protected GridPane gridButtons;
	@FXML protected GridPane advancedSearchView;
	@FXML protected GridPane startHHMMSS;
	@FXML protected GridPane endHHMMSS;
	@FXML protected GridPane startYYMODD;
	@FXML protected GridPane endYYMODD;
	@FXML protected GridPane topLabels;
	@FXML protected GridPane bottomLabels;
	@FXML protected Text titleText;
	@FXML protected Hyperlink AdvancedSearch;
	@FXML protected Hyperlink toggleHHMMSS;
	@FXML protected Button timelineViewButton;
	private List<Timeline> timelines;
	private List<Timeline> userTimelines;
	private Timeline activeTimeline;

	public void initialize() {
		// TODO fix this to be cleaner, I did it as a last second thing because it used
		// to prevent nonadmins from even viewing anything
		//
		btnCreate.setVisible(GUIManager.loggedInUser.getAdmin());
		btnCreate.setDisable(!GUIManager.loggedInUser.getAdmin());
		btnEdit.setVisible(GUIManager.loggedInUser.getAdmin());
		btnEdit.setDisable(list.getSelectionModel().isEmpty()
				|| list.getSelectionModel().getSelectedItem().getOwnerID() != GUIManager.loggedInUser.getUserID());
		btnDelete.setDisable(list.getSelectionModel().isEmpty()
				|| list.getSelectionModel().getSelectedItem().getOwnerID() != GUIManager.loggedInUser.getUserID());
		btnDelete.setVisible(GUIManager.loggedInUser.getAdmin());
		adminGUI.setVisible(GUIManager.loggedInUser.getAdmin());
		adminGUI.setDisable(!GUIManager.loggedInUser.getAdmin());
		timelineViewButton.setDisable(true);

		toggleHHMMSS.setTooltip(new Tooltip("Toggles more precise view to set hours, minutes and seconds for range."));
		AdvancedSearch.setTooltip(new Tooltip(
				"Toggles the Advanced Search view, allowing to search a timeline by it's name, keywords, creator, range or rating"));
		searchButton.setTooltip(new Tooltip("Searches for a timeline corresponding to the advanced search criteria."));
		searchInput.setTooltip(new Tooltip("Instant search for keywords."));
		clearButton.setTooltip(new Tooltip("Clear the Advanced Search view and search results."));

		// Fill ListView with the timelines
		try {
			PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
			timelines = DBM.getFromDB(stmt, new Timeline());
			list.setItems(FXCollections.observableArrayList(timelines));
		} catch (SQLException e) {
			System.err.println("Could not get timelines from database.");
		}

		// approach adapted from https://stackoverflow.com/a/36657553
		list.setCellFactory(param -> new ListCell<>() {
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

		// login.setOnAction(e -> browse.setVisible(true));

		// Add sorting options
		ObservableList<String> sortOptions = FXCollections.observableArrayList();
		sortOptions.add("Alphabetically");
		sortOptions.add("Reverse-Alphabetically");
		sortOptions.add("Most Recent");
		sortOptions.add("Oldest");
		sortBy.setItems(sortOptions);

		// Sort order selection events
		sortBy.getSelectionModel().selectedIndexProperty().addListener(ov -> sortTimelines());

		// Initialised sorting
		sortBy.getSelectionModel().select(0);

		// Search field
		searchInput.focusedProperty().addListener(ov -> {
			searchTimelines();

		});

		list.getSelectionModel().selectedIndexProperty().addListener(e -> {
			activeTimeline = list.getSelectionModel().getSelectedItem();
			updateDisplays();
		});

		titleText.setText("Select a Timeline.");
		
		ObservableList<String> ratings = FXCollections.observableArrayList();
		ratings.addAll("0", "1", "2", "3", "4", "5"); 
		searchRating.setItems(ratings);
	}


	public void sortTimelines() {
		switch (sortBy.getSelectionModel().getSelectedIndex()) {
		case 0:
			list.getItems().sort((t1, t2) -> (t1.getName().compareToIgnoreCase(t2.getName())));
			break;
		case 1:
			list.getItems().sort((t1, t2) -> (t2.getName().compareToIgnoreCase(t1.getName())));
			break;
		case 2:
			list.getItems().sort((t1, t2) -> (t2.getCreationDate().compareTo(t1.getCreationDate())));
			break;
		case 3:
			list.getItems().sort(Comparator.comparing(Timeline::getCreationDate));
			break;
		}
	}

	@FXML
	public void searchTimelines() {
		searchInput.setOnKeyReleased(keyEvent -> {// Each time new key is pressed
			String[] inputs = searchInput.getText().trim().split("\\s++"); // String is updated by the newest textfield
																			// read, if spaces the strings are split up
																			// into "string keywords" for search l
			List<Timeline> templist = new ArrayList<>(); // List of timelines that fullfill the textfield input string -
															// used to fill the ListView of timelines

			//only the logged in user timelines
			if (cbOnlyViewPersonalLines.isSelected()) {
				onlyUserTimelines(); // If only search user's timelines
				for (int i = 0; i < userTimelines.size(); i++) { // go trough all the current user's timelines in the
																	// database
					for (int j = 0; j < inputs.length; j++) {// No check all the search words used if they are to be
																// found anywhere as keywords
						String toFind = inputs[j]; // while a keyword is just one letter i.e. "f" if a keyword in
													// timeline has that letter then it will be shown (instant search
													// feature)
						List<String> allThisTimelineKeywords = timelines.get(i).getKeywords();
						List<String> possibleKeywords = new ArrayList<>();
                        String[]  timlineNames = timelines.get(i).getName().trim().split("\\s++");
						for (int k = 0; k < allThisTimelineKeywords.size(); k++) {
							if (allThisTimelineKeywords.get(k).length() >= toFind.length()) {
								possibleKeywords.add(allThisTimelineKeywords.get(k));
							}
						}
						boolean keyWordfound = Arrays.asList(possibleKeywords.toArray()).stream().anyMatch(s -> s.toString().substring(0, toFind.length()).equalsIgnoreCase(toFind));
						boolean namefound = Arrays.asList(timlineNames).stream().anyMatch(s -> s.toLowerCase().equalsIgnoreCase(toFind));

						if (keyWordfound || namefound) {
							if (!templist.contains(userTimelines.get(i))) // if the timline has not already been
																			// associated with this search then add it// to the temporary timelinelist
								templist.add(userTimelines.get(i));
						}
					}
					list.setItems(FXCollections.observableArrayList(templist)); // populate the ListView with the
																				// timelines that fulfill the search
																				// criteria at given point in
																				// time(instant)
					if (searchInput.getText().equalsIgnoreCase("")) // When everything is erased from search box, return
																	// all the user's timelines back to the ListView
						list.setItems(FXCollections.observableArrayList(userTimelines));
				}
			}
			// Search all timelines
			else {
				for (int i = 0; i < timelines.size(); i++) { // go trough all the current timelines in the database
					for (int j = 0; j < inputs.length; j++) {// No check all the search words used if they are to be
																// found anywhere as keywords
						String toFind = inputs[j]; // while a keyword is just one letter i.e. "f" if a keyword in
													// timeline has that letter then it will be shown (instant search
													// feature)
						List<String> allThisTimelineKeywords = timelines.get(i).getKeywords();
						List<String> possibleKeywords = new ArrayList<>();
						String[]  timlineNames = timelines.get(i).getName().trim().split("\\s++");
						for (int k = 0; k < allThisTimelineKeywords.size(); k++) {
							if (allThisTimelineKeywords.get(k).length() >= toFind.length()) {
								possibleKeywords.add(allThisTimelineKeywords.get(k));
							}
						}


						boolean keyWordfound = Arrays.asList(possibleKeywords.toArray()).stream().anyMatch(s -> s.toString().substring(0, toFind.length()).equalsIgnoreCase(toFind));

						boolean namefound = Arrays.asList(timlineNames).stream().anyMatch(s -> s.equalsIgnoreCase(toFind));

						if (keyWordfound || namefound) {
							if (!templist.contains(timelines.get(i))) // if the timline has not already been associated
																		// with this search then add it to the temporary
																		// timelinelist
								templist.add(timelines.get(i));
						}
					}
					list.setItems(FXCollections.observableArrayList(templist)); // populate the ListView with the
																				// timelines that fulfill the search
																				// criteria at given point in
																				// time(instant)
					if (searchInput.getText().equalsIgnoreCase("")) // When everything is erased from search box, return
																	// all the timelines back to the ListView
						list.setItems(FXCollections.observableArrayList(timelines));
				}
			}
		});
	}

	@FXML
	public void toggleAdvancedSearch() {
		if (advancedSearchView.isVisible() == false)
			advancedSearchView.setVisible(true);
		else {
			clearAdvancedSearch();
			advancedSearchView.setVisible(false);
		}
	}

	@FXML
	public void clearAdvancedSearch() {
		searchTimelineName.clear();
		searchCreator.clear();
		searchKeywords.clear();
		if (cbOnlyViewPersonalLines.isSelected()) {
			onlyUserTimelines();}
		else
			this.list.setItems(FXCollections.observableArrayList(timelines));
	}

	@FXML
	public void toggleHHMMSS() {
		startHHMMSS.setVisible(!startHHMMSS.isVisible());
		endHHMMSS.setVisible(!endHHMMSS.isVisible());
		topLabels.setVisible(!topLabels.isVisible());
		bottomLabels.setVisible(!bottomLabels.isVisible());
	}

	@FXML
	public void onlyUserTimelines() {

		if (cbOnlyViewPersonalLines.isSelected()) {
			try {
				PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineOwner = ?");
				stmt.setInt(1, GUIManager.loggedInUser.getUserID()); // GUIManager.loggedInUser.getUserID() uncomment
				// this for real version
				this.userTimelines = DBM.getFromDB(stmt, new Timeline());
				list.setItems(FXCollections.observableArrayList(userTimelines));
			} catch (SQLException e) {
				System.err.println("Could not get timelines from database.");
			}
		} else {
			try {
				PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines");
				list.setItems(FXCollections.observableArrayList(DBM.getFromDB(stmt, new Timeline())));
				sortTimelines();
			} catch (SQLException e) {
				System.err.println("Could not get timelines from database.");
			}
		}

	}

	// Date start = null; Date end = null;
	public void advancedSearch() throws SQLException {

		Date startDateSpinner = null;
		Date endDateSpinner = null;
		if(searchRating.getValue() == null)
			searchRating.setValue("0");
		String[] keywords = null;
		StringBuilder dynamicParameter = new StringBuilder();
		if (searchKeywords.getText() != null) {
			keywords = searchKeywords.getText().split(" ");

			for (int i = 1; i < keywords.length; i++) {
				System.out.println(keywords[i]);
				dynamicParameter.append("OR  CONCAT(',', `Keywords`, ',') LIKE CONCAT('%,', COALESCE(?, '%'), ',%')");
			}
		}
		PreparedStatement stmt3 = DBM.conn.prepareStatement(
				"SELECT * FROM `timelines` LEFT JOIN `users` ON users.UserID = timelines.TimelineOwner WHERE "
						+ " CONCAT(' ', `TimelineName`, ' ') LIKE CONCAT('% ', COALESCE(?, '%'), ' %') AND `UserName` = COALESCE(NULLIF(?, ''), `UserName`) AND `Rating` = COALESCE(NULLIF(?, ''), `Rating`)  AND (CONCAT(',', `Keywords`, ',') LIKE CONCAT('%,', COALESCE(?, '%'), ',%') "
						+ dynamicParameter + ")  ;");
		if (searchTimelineName.getText().isEmpty())
			stmt3.setString(1, "%");
		else
			stmt3.setString(1, searchTimelineName.getText());
		stmt3.setString(2, searchCreator.getText());
		stmt3.setInt(3, Integer.parseInt(searchRating.getValue())); // For now untill the Rating combobox provides something
		if (keywords != null)
			for (int i = 4; i < keywords.length + 4; i++) {
				stmt3.setString(i, keywords[i - 4]);
				System.out.println(keywords[i - 4]);
			}
		else
			stmt3.setString(4, searchKeywords.getText());

		// EXAMPLE OF RETURNING THE TIMELINES THAT FULFILL THE SEARCH AS TIMELINE OBJECT
		System.out.println();
		System.out.println("======SEARCH RESULTS as objects - THE TIMELINES NAMES==========");
		System.out.println(stmt3);
		List<Timeline> list = DBM.getFromDB(stmt3, new Timeline());
		List<Timeline> tempAllList;
		List<Timeline> rightTimelines = list; // Currently the right list unless we need to update it with spinner // search

		//==================SQL search is finished, here below starts Java date search of the spinners - different combinations, depending on if it has to take SQL search into account or not and if start only, end only or both
		// If only searching with Range and nothing else
		if (list.isEmpty() & (startDateSpinner != null || endDateSpinner != null)) {
			rightTimelines = new ArrayList<>();
			PreparedStatement out = DBM.conn.prepareStatement("SELECT * FROM timelines");
			tempAllList = DBM.getFromDB(out, new Timeline());
			// If range is defined in both end
		if (startDateSpinner != null & endDateSpinner != null) {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
				//Going from most narrowed restriction to the widest to catch cases in order
				if(start.getYear()==0 & end.getYear() ==0 & start.getMonth()==0 & end.getMonth() ==0 & start.getDay()==0 & end.getDay() ==0 & start.getHour()==0 & end.getHour() ==0 & start.getMinute()==0 & end.getMinute() ==0 & start.getSecond()==0 & end.getSecond() ==0)
				for (int i = 0; i < tempAllList.size(); i++) {
					if (tempAllList.get(i).getStartDate().getMillisecond()>=(start.getMillisecond())
							& tempAllList.get(i).getEndDate().getMillisecond()<=(end.getMillisecond()))
						rightTimelines.add(tempAllList.get(i));
				}
				else if(start.getYear()==0 & end.getYear() ==0 & start.getMonth()==0 & end.getMonth() ==0 & start.getDay()==0 & end.getDay() ==0 & start.getHour()==0 & end.getHour() ==0 & start.getMinute()==0 & end.getMinute() ==0)
					for (int i = 0; i < tempAllList.size(); i++) {
						if ((tempAllList.get(i).getStartDate().getSecond()>=(start.getSecond())
								& tempAllList.get(i).getEndDate().getSecond()<=(end.getSecond())))
							rightTimelines.add(tempAllList.get(i));
					}
				else if(start.getYear()==0 & end.getYear() ==0 & start.getMonth()==0 & end.getMonth() ==0 & start.getDay()==0 & end.getDay() ==0 & start.getHour()==0 & end.getHour() ==0 )
						for (int i = 0; i < tempAllList.size(); i++) {
							if ((tempAllList.get(i).getStartDate().getMinute()>=(start.getMinute())
									& tempAllList.get(i).getEndDate().getMinute()<=(end.getMinute())))
								rightTimelines.add(tempAllList.get(i));
						}
					else	if(start.getYear()==0 & end.getYear() ==0 & start.getMonth()==0 & end.getMonth() ==0 & start.getDay()==0 & end.getDay() ==0)
							for (int i = 0; i < tempAllList.size(); i++) {
								if ((tempAllList.get(i).getStartDate().getHour()>=(start.getHour())
										& tempAllList.get(i).getEndDate().getHour()<=(end.getHour())))
									rightTimelines.add(tempAllList.get(i));
							}
						else	if(start.getYear()==0 & end.getYear() ==0 & start.getMonth()==0 & end.getMonth() ==0)
								for (int i = 0; i < tempAllList.size(); i++) {
									if ((tempAllList.get(i).getStartDate().getDay()>=(start.getDay())
											& tempAllList.get(i).getEndDate().getDay()<=(end.getDay())))
										rightTimelines.add(tempAllList.get(i));
								}
							else	if(start.getYear()==0 & end.getYear() ==0)
									for (int i = 0; i < tempAllList.size(); i++) {
										if ((tempAllList.get(i).getStartDate().getMonth()>=(start.getMonth())
												& tempAllList.get(i).getEndDate().getMonth()<=(end.getMonth())))
											rightTimelines.add(tempAllList.get(i));
									}
				else //Just compare whole date
					for (int i = 0; i < tempAllList.size(); i++) {
										if (tempAllList.get(i).getStartDate().compareTo(start) != -1
												|| tempAllList.get(i).getEndDate().compareTo(end) != 1)
											rightTimelines.add(tempAllList.get(i));
									}


			}
			// If range is defined in start
			else if (startDateSpinner != null) {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
			//Going from most narrowed restriction to the widest to catch cases in order
			if(start.getYear()==0  & start.getMonth()==0  & start.getDay()==0  & start.getHour()==0  & start.getMinute()==0 & start.getSecond()==0 )
				for (int i = 0; i < tempAllList.size(); i++) {
					if (tempAllList.get(i).getStartDate().getMillisecond()>=(start.getMillisecond()))
						rightTimelines.add(tempAllList.get(i));
				}
			else if(start.getYear()==0 & start.getMonth()==0 & start.getDay()==0  & start.getHour()==0 & start.getMinute()==0 )
				for (int i = 0; i < tempAllList.size(); i++) {
					if ((tempAllList.get(i).getStartDate().getSecond()>=(start.getSecond())))
						rightTimelines.add(tempAllList.get(i));
				}
			else if(start.getYear()==0  & start.getMonth()==0 & start.getDay()==0 & start.getHour()==0)
				for (int i = 0; i < tempAllList.size(); i++) {
					if ((tempAllList.get(i).getStartDate().getMinute()>=(start.getMinute())))
						rightTimelines.add(tempAllList.get(i));
				}
			else	if(start.getYear()==0 & start.getMonth()==0  & start.getDay()==0 )
				for (int i = 0; i < tempAllList.size(); i++) {
					if ((tempAllList.get(i).getStartDate().getHour()>=(start.getHour())))
						rightTimelines.add(tempAllList.get(i));
				}
			else	if(start.getYear()==0  & start.getMonth()==0 )
				for (int i = 0; i < tempAllList.size(); i++) {
					if ((tempAllList.get(i).getStartDate().getDay()>=(start.getDay())))
						rightTimelines.add(tempAllList.get(i));
				}
			else	if(start.getYear()==0)
				for (int i = 0; i < tempAllList.size(); i++) {
					if ((tempAllList.get(i).getStartDate().getMonth()>=(start.getMonth())))
						rightTimelines.add(tempAllList.get(i));
				}
			else //Just compare whole date
				for (int i = 0; i < tempAllList.size(); i++) {
					if (tempAllList.get(i).getStartDate().compareTo(start) != -1
							|| tempAllList.get(i).getEndDate().compareTo(end) != 1)
						rightTimelines.add(tempAllList.get(i));
				}

		}
			// If range is defined in end
			else {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
			//Going from most narrowed restriction to the widest to catch cases in order
			if(end.getYear() ==0 & end.getMonth() ==0 & end.getDay() ==0  & end.getHour() ==0 & end.getMinute() ==0  & end.getSecond() ==0)
				for (int i = 0; i < tempAllList.size(); i++) {
					if (tempAllList.get(i).getEndDate().getMillisecond()<=(end.getMillisecond()))
						rightTimelines.add(tempAllList.get(i));
				}
			else if( end.getYear() ==0 & end.getMonth() ==0 & end.getDay() ==0  & end.getHour() ==0 & end.getMinute() ==0)
				for (int i = 0; i < tempAllList.size(); i++) {
					if ( tempAllList.get(i).getEndDate().getSecond()<=(end.getSecond()))
						rightTimelines.add(tempAllList.get(i));
				}
			else if( end.getYear() ==0 & end.getMonth() ==0 & end.getDay() ==0 & end.getHour() ==0 )
				for (int i = 0; i < tempAllList.size(); i++) {
					if ((tempAllList.get(i).getEndDate().getMinute()<=(end.getMinute())))
						rightTimelines.add(tempAllList.get(i));
				}
			else	if( end.getYear() ==0 & end.getMonth() ==0  & end.getDay() ==0)
				for (int i = 0; i < tempAllList.size(); i++) {
					if ((tempAllList.get(i).getEndDate().getHour()<=(end.getHour())))
						rightTimelines.add(tempAllList.get(i));
				}
			else	if( end.getYear() ==0 & end.getMonth() ==0)
				for (int i = 0; i < tempAllList.size(); i++) {
					if ((tempAllList.get(i).getEndDate().getDay()<=(end.getDay())))
						rightTimelines.add(tempAllList.get(i));
				}
			else	if(end.getYear() ==0)
				for (int i = 0; i < tempAllList.size(); i++) {
					if (( tempAllList.get(i).getEndDate().getMonth()<=(end.getMonth())))
						rightTimelines.add(tempAllList.get(i));
				}
			else //Just compare whole date
				for (int i = 0; i < tempAllList.size(); i++) {
					if (tempAllList.get(i).getStartDate().compareTo(start) != -1
							|| tempAllList.get(i).getEndDate().compareTo(end) != 1)
						rightTimelines.add(tempAllList.get(i));
				}

		}
		}

		// If searching with Range amongst else
		if (!list.isEmpty() & (startDateSpinner != null || endDateSpinner != null)) {
			PreparedStatement out = DBM.conn.prepareStatement("SELECT * FROM timelines");
			rightTimelines = new ArrayList<>();
			// If range is defined in both ends
			if (startDateSpinner != null & endDateSpinner != null) {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
				//Going from most narrowed restriction to the widest to catch cases in order
				if(start.getYear()==0 & end.getYear() ==0 & start.getMonth()==0 & end.getMonth() ==0 & start.getDay()==0 & end.getDay() ==0 & start.getHour()==0 & end.getHour() ==0 & start.getMinute()==0 & end.getMinute() ==0 & start.getSecond()==0 & end.getSecond() ==0)
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getStartDate().getMillisecond()>=(start.getMillisecond())
								& list.get(i).getEndDate().getMillisecond()<=(end.getMillisecond()))
							rightTimelines.add(list.get(i));
					}
				else if(start.getYear()==0 & end.getYear() ==0 & start.getMonth()==0 & end.getMonth() ==0 & start.getDay()==0 & end.getDay() ==0 & start.getHour()==0 & end.getHour() ==0 & start.getMinute()==0 & end.getMinute() ==0)
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getStartDate().getSecond()>=(start.getSecond())
								& list.get(i).getEndDate().getSecond()<=(end.getSecond())))
							rightTimelines.add(list.get(i));
					}
				else if(start.getYear()==0 & end.getYear() ==0 & start.getMonth()==0 & end.getMonth() ==0 & start.getDay()==0 & end.getDay() ==0 & start.getHour()==0 & end.getHour() ==0 )
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getStartDate().getMinute()>=(start.getMinute())
								& list.get(i).getEndDate().getMinute()<=(end.getMinute())))
							rightTimelines.add(list.get(i));
					}
				else	if(start.getYear()==0 & end.getYear() ==0 & start.getMonth()==0 & end.getMonth() ==0 & start.getDay()==0 & end.getDay() ==0)
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getStartDate().getHour()>=(start.getHour())
								& list.get(i).getEndDate().getHour()<=(end.getHour())))
							rightTimelines.add(list.get(i));
					}
				else	if(start.getYear()==0 & end.getYear() ==0 & start.getMonth()==0 & end.getMonth() ==0)
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getStartDate().getDay()>=(start.getDay())
								& list.get(i).getEndDate().getDay()<=(end.getDay())))
							rightTimelines.add(list.get(i));
					}
				else	if(start.getYear()==0 & end.getYear() ==0)
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getStartDate().getMonth()>=(start.getMonth())
								& list.get(i).getEndDate().getMonth()<=(end.getMonth())))
							rightTimelines.add(list.get(i));
					}
				else //Just compare whole date
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getStartDate().compareTo(start) != -1
								|| list.get(i).getEndDate().compareTo(end) != 1)
							rightTimelines.add(list.get(i));
					}

			}
			// If range is defined in start
			else if (startDateSpinner != null) {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
				if(start.getYear()==0  & start.getMonth()==0  & start.getDay()==0  & start.getHour()==0  & start.getMinute()==0  & start.getSecond()==0 )
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getStartDate().getMillisecond()>=(start.getMillisecond()))
							rightTimelines.add(list.get(i));
					}
				else if(start.getYear()==0  & start.getMonth()==0  & start.getDay()==0 & start.getHour()==0  & start.getMinute()==0 )
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getStartDate().getSecond()>=(start.getSecond())))
							rightTimelines.add(list.get(i));
					}
				else if(start.getYear()==0 & start.getMonth()==0  & start.getDay()==0 & start.getHour()==0  )
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getStartDate().getMinute()>=(start.getMinute())))
							rightTimelines.add(list.get(i));
					}
				else	if(start.getYear()==0  & start.getMonth()==0 & start.getDay()==0 )
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getStartDate().getHour()>=(start.getHour())))
							rightTimelines.add(list.get(i));
					}
				else	if(start.getYear()==0 & start.getMonth()==0 )
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getStartDate().getDay()>=(start.getDay())))
							rightTimelines.add(list.get(i));
					}
				else	if(start.getYear()==0 )
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getStartDate().getMonth()>=(start.getMonth())))
							rightTimelines.add(list.get(i));
					}
				else //Just compare whole date
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getStartDate().compareTo(start) != -1
								|| list.get(i).getEndDate().compareTo(end) != 1)
							rightTimelines.add(list.get(i));
					}
			}
			// If range is defined in end
			else {
				Date start = startDateSpinner;
				Date end = endDateSpinner;
				if( end.getYear() ==0  & end.getMonth() ==0  & end.getDay() ==0 & end.getHour() ==0  & end.getMinute() ==0  & end.getSecond() ==0)
					for (int i = 0; i < list.size(); i++) {
						if ( list.get(i).getEndDate().getMillisecond()<=(end.getMillisecond()))
							rightTimelines.add(list.get(i));
					}
				else if( end.getYear() ==0  & end.getMonth() ==0  & end.getDay() ==0  & end.getHour() ==0  & end.getMinute() ==0)
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getEndDate().getSecond()<=(end.getSecond()))
							rightTimelines.add(list.get(i));
					}
				else if( end.getYear() ==0  & end.getMonth() ==0 & end.getDay() ==0  & end.getHour() ==0 )
					for (int i = 0; i < list.size(); i++) {
						if (( list.get(i).getEndDate().getMinute()<=(end.getMinute())))
							rightTimelines.add(list.get(i));
					}
				else	if(end.getYear() ==0  & end.getMonth() ==0  & end.getDay() ==0)
					for (int i = 0; i < list.size(); i++) {
						if (( list.get(i).getEndDate().getHour()<=(end.getHour())))
							rightTimelines.add(list.get(i));
					}
				else	if( end.getYear() ==0  & end.getMonth() ==0)
					for (int i = 0; i < list.size(); i++) {
						if (( list.get(i).getEndDate().getDay()<=(end.getDay())))
							rightTimelines.add(list.get(i));
					}
				else	if( end.getYear() ==0)
					for (int i = 0; i < list.size(); i++) {
						if ((list.get(i).getEndDate().getMonth()<=(end.getMonth())))
							rightTimelines.add(list.get(i));
					}
				else //Just compare whole date
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getStartDate().compareTo(start) != -1
								|| list.get(i).getEndDate().compareTo(end) != 1)
							rightTimelines.add(list.get(i));
					}
			}
		}

		for (int i = 0; i < rightTimelines.size(); i++)
			System.out.println(list.get(i).getName());


		//====================DATE COMPARISON FINISHED====================================================

		//Now showing timelines search results depending on if they are for only logged in user or all timelines

		if (cbOnlyViewPersonalLines.isSelected()) {

			List<Timeline> userline = new ArrayList<>();
			for (int i = 0; i < rightTimelines.size(); i++) {
				for (int j = 0; j < userTimelines.size(); j++) {
					if (userTimelines.get(j).getID() == rightTimelines.get(i).getID())
						userline.add(rightTimelines.get(i));
				}
			}
			this.list.setItems(FXCollections.observableArrayList(userline));
		} else
			this.list.setItems(FXCollections.observableArrayList(rightTimelines));
	}

    @FXML
    public void adminScreen() throws IOException {
        GUIManager.swapScene("AdminRoleManager");
    }

    @FXML
    public TimelineView createTimeline() {
        Timeline t = new Timeline();
        t.setOwnerID(GUIManager.loggedInUser.getUserID());
        try {
            TimelineView timelineView = GUIManager.swapScene("TimelineView");
            timelineView.setActiveTimeline(t);
            timelineView.timelineEditorController.toggleEditable(true);
            return timelineView;
        } catch (IOException e) {e.printStackTrace(); return null;}
    }

    @FXML
    public TimelineView editTimeline() {
        return(openTimelineView(this.activeTimeline));
    }

    @FXML
    public TimelineView openTimeline() {
        return(openTimelineView(list.getSelectionModel().getSelectedItem()));
    }

    private TimelineView openTimelineView(Timeline newActiveTimeline) {
        try {
            TimelineView timelineView = GUIManager.swapScene("TimelineView");
            timelineView.setActiveTimeline(newActiveTimeline);
            return timelineView;
        } catch (IOException e) {e.printStackTrace(); return null;}
    }

    // open DeletePopUp
    @FXML
    public boolean deleteConfirmation() throws IOException {

        Alert confirmDeleteTimeline = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDeleteTimeline.setTitle("Confirm Deletion");
        confirmDeleteTimeline.setHeaderText("Are you sure you want to delete " + list.getSelectionModel().getSelectedItem().getName() + "?");
        confirmDeleteTimeline.setContentText("This can not be undone.");

        Optional<ButtonType> result = confirmDeleteTimeline.showAndWait();

        if (result.get() == ButtonType.CANCEL)
            return false;
        else
        {
            try {deleteOrphans(list.getSelectionModel().getSelectedItem());
                DBM.deleteFromDB(list.getSelectionModel().getSelectedItem());
            } catch (SQLException e) {e.printStackTrace();}

            list.getItems().remove(list.getSelectionModel().getSelectedIndex());
            list.getSelectionModel().select(null);
            return true;
        }
    }

    public void deleteOrphans(Timeline timeline) throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("SELECT e.* FROM `timelines` t " +
                "LEFT JOIN timelineevents te " +
                "ON t.TimelineID = te.TimelineID " +            //destroys orphaned events (i.e. events where there are no
                "LEFT JOIN events e " +                            //junction table records for them with a different TimelineID
                "ON te.EventID = e.EventID AND e.EventID NOT IN (SELECT EventID FROM timelineevents WHERE TimelineID != ?) " +
                "WHERE t.TimelineID = ? ");

        out.setInt(1, timeline.getID());
        out.setInt(2, timeline.getID());

        DBM.deleteFromDB(DBM.getFromDB(out, new Event()));
    }

    @FXML
    private void updateDisplays() {
        if (list.getSelectionModel().getSelectedItem() != null) {
            if (list.getSelectionModel().getSelectedItem().getOwnerID() == GUIManager.loggedInUser.getUserID()) {
                btnDelete.setDisable(false);
                btnEdit.setDisable(false);
            } else {
                btnDelete.setDisable(true);
                btnEdit.setDisable(true);
            }
            timelineViewButton.setDisable(false);

            Timeline timeline = list.getSelectionModel().getSelectedItem();

            int year = timeline.getCreationDate().getYear();
            int month = timeline.getCreationDate().getMonth();
            int day = timeline.getCreationDate().getDay();

            StringBuilder keyWords = new StringBuilder();
            for (String s : timeline.getKeywords())
                keyWords.append(s + ", ");
            keyWords.delete(keyWords.length() - 2, keyWords.length());

            titleText.setText("Title: " + timeline.getName()
                    + "\nDescription: " + timeline.getDescription()
                    + "\nDate Created: " + year + "/" + month + "/" + day
                    + "\nKeywords: " + keyWords);

        } else {
            timelineViewButton.setDisable(true);
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
            titleText.setText("Select a Timeline.");
        }
    }
}
