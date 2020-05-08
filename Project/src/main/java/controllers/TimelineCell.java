package controllers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import database.DBM;
import database.Timeline;
import database.User;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.shape.*;

public class TimelineCell {

	@FXML
	private GridPane pane;
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

	public void update(double width) {
		if (timeline != null) {
			title.setText(timeline.getName());
			author.setText("By "+user.getUserName());
			setBGImage(width);
			//stuff for rating here
		}
	}

	public Timeline getTimeline() {
		return timeline;
	}

	public void setTimeline(Timeline timeline,double width) {
		this.timeline = timeline;
		try {
			PreparedStatement stat = DBM.conn.prepareStatement("SELECT * FROM Users WHERE UserID=?");
			stat.setInt(1, timeline.getOwnerID());
			user = DBM.getFromDB(stat, new User()).get(0);
		} catch (SQLException e) {}
		pane.setPrefWidth(width);
		this.update(width);
	}
	
	public void setBGImage(double width) {
		String imageURL = "'file:src/main/resources/images/image4.png'";
		pane.setStyle("-fx-background-image: url("+imageURL+"); -fx-background-size: "+((int)(width+0.5))+"px;");
	}
	
}
