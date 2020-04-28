package controllers;

import database.*;
import javafx.scene.control.*;
import utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class OldTimelineEditor {

	@FXML
	private TextArea titleInput;
	@FXML
	private TextArea descriptionInput;
	@FXML
	private DatePicker startDateInput;
	@FXML
	private DatePicker endDateInput;
	@FXML
	private ComboBox<String> timeInput;
	@FXML
	private ListView<String> listView;
	@FXML
	private TextField keywordInput;
	@FXML
	private Text feedbackText;
	@FXML
	private Button removeButton;
	
	private boolean isNewTimeline;
	
	private ObservableList<String> keywords = FXCollections.observableArrayList();

	private Timeline activeTimeline;
	PreparedStatement stmt;

	public OldTimelineEditor() {
		GUIManager.mainStage.setTitle("Timeline Editor");
	}

	@FXML
	private void initialize() throws SQLException {
		//set listview to track keyword observable list
		listView.setItems(keywords);	}

	@FXML
	public void save(ActionEvent event) {
		// This part is for editing an existing timeline

		if (!titleInput.getText().trim().isBlank() && startDateInput.getValue() != null && endDateInput.getValue() != null) {
			activeTimeline.setTimelineName(titleInput.getText());
			activeTimeline.setTimelineDescription(descriptionInput.getText());
			activeTimeline.setScale((timeInput.getSelectionModel().getSelectedIndex()) + 1);
			LocalDate start = startDateInput.getValue();
			activeTimeline.setStartDate(new Date(start.getYear(), start.getMonthValue(), start.getDayOfMonth()));
			LocalDate end = endDateInput.getValue();
			activeTimeline.setEndDate(new Date(end.getYear(), end.getMonthValue(), end.getDayOfMonth()));
			activeTimeline.setKeywords(keywords);

			// update in db if required fields aren't empty/null
			try {
				if(isNewTimeline) {
					DBM.insertIntoDB(activeTimeline);
					feedbackText.setText("Timeline \""+activeTimeline.getTimelineName()+"\" saved successfully");
					isNewTimeline=false;
				}else {
					DBM.updateInDB(activeTimeline);
					feedbackText.setText("Timeline \""+activeTimeline.getTimelineName()+"\" updated successfully");
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else {
			if(titleInput.getText().trim().isBlank()) feedbackText.setText("Title cannot be empty!");
			else if(descriptionInput.getText().trim().isBlank()) feedbackText.setText("Description title cannot be empty!");
			else if(startDateInput.getValue()==null) feedbackText.setText("Start Date cannot be empty!");
			else if(endDateInput.getValue()==null) feedbackText.setText("End Date cannot be empty!");
			
		}
	}

	@FXML
	public void cancel() throws IOException {
		GUIManager.swapScene("Dashboard");
		activeTimeline = null;
	}

	public void setActiveTimeline(Timeline a) {
		this.activeTimeline = a;
	}

	public void populateDisplay() {
		if (activeTimeline == null) {
			// create a new timeline object to work with.
			activeTimeline = new Timeline("", "", 0, "", null, null, false,null);
			isNewTimeline=true;
		} else {
			// Get a timeline from DB. Such ineficient, much sad!
			titleInput.setText(activeTimeline.getTimelineName());
			descriptionInput.setText(activeTimeline.getTimelineDescription());
			startDateInput.setValue(LocalDate.of(activeTimeline.getStartDate().getYear(),
					activeTimeline.getStartDate().getMonth(), activeTimeline.getStartDate().getDay()));
			endDateInput.setValue(LocalDate.of(activeTimeline.getEndDate().getYear(),
					activeTimeline.getEndDate().getMonth(), activeTimeline.getEndDate().getDay()));
			timeInput.getSelectionModel().select(activeTimeline.getScale() - 1);
			for(String s : activeTimeline.getKeywords()) {
				keywords.add(s);
			}
			keywords.sort((s1,s2)->s1.compareTo(s2));
			isNewTimeline=false;
		}
	}
	
	public boolean isUniqueKeyword(String k) {
		for(String s:keywords) {
			if(k.equalsIgnoreCase(s)) return false;
			
		}
		return true;
	}
	
	@FXML
	public void addKeyword(ActionEvent event) {
		String inputWord = keywordInput.getText();
		inputWord = inputWord.replace(",", " ");
		if(inputWord.isBlank()) {
			feedbackText.setText("Keyword cannot be empty!");
		}
		else {
			if(!isUniqueKeyword(inputWord)) {
				feedbackText.setText("Keyword already exists!");
			}
			else {
				keywords.add(inputWord);
				feedbackText.setText("Keyword "+inputWord+" added");
				keywords.sort((s1,s2)->s1.compareTo(s2));
				keywordInput.setText("");
			}
		}
	}
	@FXML
	public void removeKeyword(ActionEvent event) {
		if(listView.getSelectionModel().getSelectedIndex()<0) {
			feedbackText.setText("No keyword selected!");
		}
		else {
			String removedWord=listView.getSelectionModel().getSelectedItem();
			keywords.remove(listView.getSelectionModel().getSelectedIndex());
			feedbackText.setText("Keyword "+removedWord+" removed!");
			listView.getSelectionModel().select(-1);
		}
	}

}
