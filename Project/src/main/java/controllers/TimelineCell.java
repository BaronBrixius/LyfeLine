package controllers;

import database.Timeline;
import database.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import java.util.ArrayList;
import java.util.List;

public class TimelineCell {

    @FXML
    GridPane pane;
    @FXML
    HBox ratingBox;
    @FXML
    Label title;
    @FXML
    Label description;
    @FXML
    Label keywords;
    @FXML
    Label author;
    List<Polygon> ratingButtons;
    Timeline timeline;
    User user;
    boolean focused = false;

    public void initialize() {
        // Ratings
        ratingButtons = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            ratingButtons.add((Polygon) ratingBox.getChildren().get(i));    // grab each polygon from the HBox and set it up as a pseudo-button
            setupRatingButton(ratingButtons.get(i), i);
        }

        ratingBox.setOnMouseEntered(e -> ratingBox.setOpacity(1));
        ratingBox.setOnMouseMoved(e -> colorStarsByRating((int) Math.ceil(e.getX() * 5 / ratingBox.getWidth())));   //highlight current star and ones to the left
        ratingBox.setOnMouseExited(e -> {
            colorStarsByRating((int) Math.ceil(timeline.getRating()));  //return highlighting to normal
            ratingBox.setOpacity((timeline.getRating() > 1) ? 1 : 0);
        });
    }

    private void setupRatingButton(Polygon button, int index) {
        double starSize = 15;
        int numPoints = 5;

        button.getPoints().clear();
        double angle = 0;
        double distance;

        for (int i = 0; i < numPoints * 2; i++) {   // calculate position of each point on star, starting from top and going clockwise
            if (i % 2 == 0)
                distance = starSize;                //tips stick out further
            else
                distance = starSize / 2;            //intersections don't stick out as much, increase number to increase how "sharp" the star is

            button.getPoints().addAll(Math.sin(angle) * distance, // trig to find point position, easier to adjust than manual point placement
                    Math.cos(angle) * distance * -1);

            angle += Math.PI / numPoints;            // simplified (2*PI / numPoints*2), rotates angle to calculate next tip/intersection
        }

        button.setOnMouseClicked(e -> {
            timeline.rateTimeline(index + 1);       //click a star to submit a rating and update its display value
            timeline.updateRatingFromDB();
            colorStarsByRating((int) Math.ceil(timeline.getRating()));
        });
    }

    private void colorStarsByRating(int rating) {
        for (int i = 0; i < 5; i++) {
            Color starColor = i < rating ? Color.YELLOW : Color.GREY;   // yellow fill for lower stars
            ratingButtons.get(i).setFill(starColor);                    // grey fill for stars above timeline's rank
        }
    }

    public void update() {
        if (timeline != null) {
            populateTimelineDetails();
            setBGImage();
            colorStarsByRating((int) Math.ceil(timeline.getRating()));
            ratingBox.setOpacity((timeline.getRating() > 1) ? 1 : 0);
        }
    }

    public void populateTimelineDetails() {
        title.setText("Title: " + timeline.getName());
        author.setText("By: " + user.getUserName());
        description.setText("Description: " + timeline.getDescription());
        //TODO start and end date here

        StringBuilder keyWords = new StringBuilder();
        keyWords.append("Keywords: ");
        for (String s : timeline.getKeywords())
            keyWords.append(s).append(", ");
        if (keyWords.length() >= 12)
            keyWords.delete(keyWords.length() - 2, keyWords.length());
        keywords.setText(keyWords.toString());

        if (focused) {
            if (!pane.getChildren().contains(description)) {    //If the cell is focused and doesn't show the description
                pane.add(description, 0, 1);
                pane.add(keywords, 0, 2);
            }
        } else if (pane.getChildren().contains(description))    //If the cell is not focused and is still showing the description
            pane.getChildren().removeAll(description, keywords);
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
        user = timeline.getOwner();
        this.update();
    }

    public void setBGImage() {
        String imageURL = timeline.getImagePath() != null ? "url(file:" + timeline.getImagePath() + ")" : null;
        int height = focused ? 400 : 80;
        pane.setStyle(" -fx-background-image: " + imageURL + "; -fx-pref-height: " + height + "px; -fx-background-size: 1271px, stretch;");
    }

}
