package controllers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import database.DBM;
import database.Timeline;
import database.User;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.shape.*;

public class TimelineCell {

	@FXML
	private Region imageRegion;
	@FXML
	private Text title;
	@FXML
	private Text author;
	@FXML
	private Polygon star1;
	@FXML
	private Polygon star2;
	@FXML
	private Polygon star3;
	@FXML
	private Polygon star4;
	@FXML
	private Polygon star5;

	public Timeline timeline;
	public User user;

	public void initialize() {
		
	}

	public void update() {
		if (timeline != null) {
			title.setText(timeline.getName());
			author.setText("By "+user.getUserName());
			setBGImage();
			//stuff for rating here
			
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
