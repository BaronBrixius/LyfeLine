package database;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class JSONTimeline {
    private final Timeline timeline;
    private String timelineImage;
    private List<String> eventImages;
    private List<Rating> ratings;
    private User owner;

    public JSONTimeline(Timeline timeline) {
        this.timeline = timeline;
        setTimelineImage();
        setEventImages();
        setRatings();
        setOwner();
    }

    private void setTimelineImage() {
        this.timelineImage = toBase64(timeline.getImagePath());
    }

    private void setEventImages() {
        List<Event> eventList = timeline.getEventList();
        List<String> eventImages = new ArrayList<>(eventList.size());
        String imageContent;
        for (Event e : eventList) {
            imageContent = e.getImagePath();
            eventImages.add(toBase64(imageContent));
        }
        this.eventImages = eventImages;
    }

    private String toBase64(String filePath) {          //read a file from its path and convert it to Base 64 string
        try {
            File imageFile = new File(filePath);
            byte[] imageFileContent = FileUtils.readFileToByteArray(imageFile);
            return Base64.getEncoder().encodeToString(imageFileContent);
        } catch (IOException e) {
            System.err.println("Could not find image at:" + filePath);
            return null;
        }
    }

    private void setRatings() {
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT UserID, Rating FROM ratings WHERE TimelineID = ?");
            stmt.setInt(1, timeline.getID());
            ratings = DBM.getFromDB(stmt, rs -> new Rating(rs.getInt("UserID"), rs.getInt("Rating")));
        } catch (SQLException e) {
            System.err.println("Could not read ratings from database.");
        }
    }

    private void setOwner() {
        try {
            PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM users WHERE UserID = ?");
            stmt.setInt(1, timeline.getOwnerID());
            owner = DBM.getFromDB(stmt, new User()).get(0);
        } catch (SQLException e) {
            System.err.println("Could not read ratings from database.");
        }
    }

    private static class Rating {
        private int userID;
        private int rating;

        private Rating(int userID, int rating) {
            this.userID = userID;
            this.rating = rating;
        }
    }
}
