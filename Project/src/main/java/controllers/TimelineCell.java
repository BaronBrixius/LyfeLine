package controllers;

import database.DBM;
import database.Timeline;
import database.User;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimelineCell {

    public HBox ratingBox;
    public Timeline timeline;
    public User user;
    List<Polygon> ratingButtons;
    @FXML
    private GridPane pane;
    @FXML
    private Text title;
    @FXML
    private Text author;

    public void initialize() {
        //Ratings
        ratingButtons = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            ratingButtons.add((Polygon) ratingBox.getChildren().get(i));    //grab each polygon from the HBox and set it up as a pseudo-button
            setupRatingButton(ratingButtons.get(i), i);
        }

        ratingBox.setOnMouseMoved(e -> {
            colorStarsByRating((int) Math.ceil(e.getX() * 5 / ratingBox.getWidth()));
        });    //highlight current star and ones to the left
        ratingBox.setOnMouseExited(e -> colorStarsByRating((int) Math.ceil(timeline.getRating())));                    //return highlighting to normal
    }


    private void setupRatingButton(Polygon button, int index) {
        double starSize = 30;
        int numPoints = 5;

        button.getPoints().clear();
        double angle = 0;
        double distance;
        //calculate position of each point of star, starting from top and going clockwise
        for (int i = 0; i < numPoints * 2; i++) {
            if (i % 2 == 0)
                distance = starSize;        //tips stick out further
            else
                distance = starSize / 2;    //intersections don't stick out as much, increase number to increase how "sharp" the star is

            button.getPoints().addAll(Math.sin(angle) * distance,           //trig to find point position, easier to adjust than manual point placement
                    Math.cos(angle) * distance * -1);

            angle += Math.PI / numPoints;       //simplified 2*PI / numPoints*2, rotates angle for next tip/intersection
        }

        button.setOnMouseClicked(e -> timeline.addRating(GUIManager.loggedInUser.getUserID(), index + 1));    //click a star to submit a rating
    }

    private void colorStarsByRating(int rating) {
        for (int i = 0; i < 5; i++) {
            if (i < rating)
                ratingButtons.get(i).setFill(Color.YELLOW);        //yellow fill for lower stars
            else
                ratingButtons.get(i).setFill(Color.GREY);        //grey fill for stars above timeline's rank
        }
    }

    public void update(double width) {
        if (timeline != null) {
            title.setText(timeline.getName());
            author.setText("By " + user.getUserName());
            setBGImage(width);
            colorStarsByRating((int) Math.ceil(timeline.getRating()));
        }
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline, double width) {
        this.timeline = timeline;
        try {
            PreparedStatement stat = DBM.conn.prepareStatement("SELECT * FROM Users WHERE UserID=?");
            stat.setInt(1, timeline.getOwnerID());
            user = DBM.getFromDB(stat, new User()).get(0);
        } catch (SQLException e) {
        }
        pane.setPrefWidth(width);
        this.update(width);
    }

    public void setBGImage(double width) {
        String imageURL = "'file:src/main/resources/images/image4.png'";
        pane.setStyle("-fx-background-image: url(" + imageURL + "); -fx-background-size: " + ((int) (width + 0.5)) + "px;");
    }
}
