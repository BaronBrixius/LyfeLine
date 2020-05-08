package controllers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBM;
import database.Timeline;
import database.User;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.shape.*;

public class TimelineCell {

	public HBox ratingBox;
	@FXML
	private Region imageRegion;
	@FXML
	private Text title;
	@FXML
	private Text author;
	List<Polygon> ratingButtons;

	public Timeline timeline;
	public User user;

	public void initialize() {
		//Ratings
		ratingButtons = new ArrayList<>(5);
		for (int i = 0; i < 5; i++) {
			ratingButtons.add((Polygon) ratingBox.getChildren().get(i));
			setupRatingButton(ratingButtons.get(i), i);
		}

		ratingBox.setOnMouseMoved(e ->
				colorStarsByRating((int) Math.floor(e.getX() * 5 / ratingBox.getWidth())));
		ratingBox.setOnMouseExited(e -> colorStarsByRating((int) Math.ceil(timeline.getRating())));
	}


	private void setupRatingButton(Polygon button, int index) {
		double starSize = 40;
		int numPoints = 5;

		button.getPoints().clear();
		double angle = 0;
		double distance;
		for (int i = 0; i < numPoints * 2; i++) {
			if (i % 2 == 0)
				distance = starSize;
			else
				distance = starSize / 2;

			button.getPoints().addAll(Math.sin(angle) * distance,           //easier to implement/adjust than manual point placement
					Math.cos(angle) * distance * -1);

			angle += Math.PI / numPoints;       //simplified 2*PI / numPoints*2
		}

		button.setOnMouseClicked(e -> timeline.addRating(GUIManager.loggedInUser.getUserID(), index + 1));
	}

	private void colorStarsByRating(int rating) {
		for (int i = 0; i < 5; i++) {
			if (i <= rating)
				ratingButtons.get(i).setFill(Color.YELLOW);
			else
				ratingButtons.get(i).setFill(Color.GREY);
		}
	}

	public void update() {
		if (timeline != null) {
			title.setText(timeline.getName());
			author.setText("By "+user.getUserName());
			setBGImage();
			colorStarsByRating((int) Math.ceil(timeline.getRating()));
		}
	}

	public Timeline getTimeline() {
		return timeline;
	}

	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
		try {
			PreparedStatement stat = DBM.conn.prepareStatement("SELECT * FROM Users WHERE UserID=?");
			stat.setInt(1, timeline.getOwnerID());
			user = DBM.getFromDB(stat, new User()).get(0);
		} catch (SQLException e) {}
		this.update();
	}
	
	public void setBGImage() {
		String imageURL = "'file:src/main/resources/images/image5.png'";
		imageRegion.setStyle("-fx-background-image: url("+imageURL+")");
	}
}
