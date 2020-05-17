package database;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

//Convenience class that gathers all relevant information about a timeline for easy JSON export/import
public class JSONTimeline {
    private final Timeline timeline;            //Timelines hold their events in their own list, no need to duplicate here
    private final String timelineImage;
    private List<String> eventImages;
    private List<Rating> ratings;
    private User owner;

    public JSONTimeline(Timeline timeline) {
        this.timeline = timeline;
        this.timelineImage = toBase64(timeline.getImagePath());
        makeEventImages();
        makeRatings();
        makeOwner();
    }

    //////////////////////////EXPORT METHODS//////////////////////////

    private void makeEventImages() {
        eventImages = new ArrayList<>(timeline.getEventList().size());

        String imageContent;
        for (int i = 0; i < timeline.getEventList().size(); i++) {
            imageContent = timeline.getEventList().get(i).getImagePath();       //for each event, get their image
            eventImages.add(toBase64(imageContent));                            //convert to base64, and add that to the list
        }
    }

    private String toBase64(String filePath) {          //read a file from its path and convert it to a Base 64 string
        if (filePath == null)
            return null;

        try {
            File imageFile = new File(filePath);
            byte[] imageFileContent = FileUtils.readFileToByteArray(imageFile);
            return Base64.getEncoder().encodeToString(imageFileContent);
        } catch (IOException e) {
            System.err.println("Could not find image at:" + filePath);
            return null;
        }
    }

    private void makeRatings() {                        //grab all ratings for this timeline from DB and store in List
        try (PreparedStatement stmt = DBM.conn.prepareStatement("SELECT u.UserEmail, r.Rating FROM ratings r " +
                "INNER JOIN users u ON r.UserID = u.UserID " +
                "WHERE TimelineID = ?")) {
            stmt.setInt(1, timeline.getID());
            ratings = DBM.getFromDB(stmt, rs -> new Rating(rs.getString("UserEmail"), rs.getInt("Rating")));
        } catch (SQLException e) {
            System.err.println("Could not read ratings from database.");
        }
    }

    private void makeOwner() {                          //grab this timeline's owner
        try (PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM users WHERE UserID = ?")) {
            stmt.setInt(1, timeline.getOwnerID());
            owner = DBM.getFromDB(stmt, new User()).get(0);
        } catch (SQLException e) {
            System.err.println("Could not read ratings from database.");
        }
    }

    //////////////////////////IMPORT METHODS//////////////////////////

    //After importing a JSON file, use this to insert its contents into the DB
    public void importToDB() {
        int ownerID = matchOwnerInDB();         //check if owner is already in DB
        if (ownerID > 0)                        //if he is, pass local owner's ID to other objects
            setOwnership(ownerID);
        else {                                  //otherwise add them to DB and pass newly generated ID to other objects
            importOwner();
            setOwnership(owner.getUserID());
        }

        importTimeline();
        importEventList();
        importRatings();
    }

    private void importTimeline() {
        String filePath = importImage(timelineImage);           //save image and give its new filepath to the timeline
        timeline.setImage(filePath);
        try {
            DBM.insertIntoDB(timeline);                         //no dupe checking, if they're at this point the user may want a dupe timeline
        } catch (SQLException e) {
            System.err.println("Could not access timeline database");
        }
    }

    private void importEventList() {
        Event eventToImport;
        int eventID;
        for (int i = 0; i < timeline.getEventList().size(); i++) {
            eventToImport = timeline.getEventList().get(i);
            eventID = matchEventInDB(eventToImport);
            if (eventID > 0)                                        //if identical event is in DB, pass its ID to this event and call them equal
                eventToImport.setID(eventID);
            else {                                                  //otherwise add event to DB and pass newly generated ID to this event
                String filePath = importImage(eventImages.get(i));  //save image located in same index of imagesList, and give its new filepath to the timeline
                eventToImport.setImage(filePath);
                importEvent(eventToImport);
            }

            try {                                                   //add event to the new timeline on junction table
                eventToImport.addToTimeline(timeline.getID());
            } catch (SQLException e) {
                System.err.println("Could not access timelineevents database.");
            }
        }
    }

    private void importEvent(Event eventToImport) {
        try {
            DBM.insertIntoDB(eventToImport);
        } catch (SQLException e) {
            System.err.println("Could not access users database");
        }
    }

    private int matchEventInDB(Event eventToImport) {                              //checks if event is in DB, returns event's ID if it is
        try (PreparedStatement stmt = DBM.conn.prepareStatement("SELECT e.EventID FROM events e " +
                "INNER JOIN users u ON e.EventOwner = u.UserID " +
                "WHERE u.UserEmail = ? AND e.EventName = ? AND e.EventDescription = ? " +
                "AND e.StartYear = ? AND StartMonth = ? AND  `StartDay` = ? AND  `StartHour` = ? AND  `StartMinute` = ? AND  " +
                "`StartSecond` = ? AND  `StartMillisecond` = ? AND    `EndYear` = ? AND  `EndMonth` = ? AND  `EndDay` = ? AND  `EndHour` = ? AND  `EndMinute` = ? AND  `EndSecond` = ? AND  `EndMillisecond` = ? ")) {
            stmt.setString(1, owner.getUserEmail());
            stmt.setString(2, eventToImport.getName());
            stmt.setString(3, eventToImport.getDescription());
            stmt.setInt(4, eventToImport.getStartDate().getYear());
            stmt.setInt(5, eventToImport.getStartDate().getMonth());
            stmt.setInt(6, eventToImport.getStartDate().getDay());
            stmt.setInt(7, eventToImport.getStartDate().getHour());
            stmt.setInt(8, eventToImport.getStartDate().getMinute());
            stmt.setInt(9, eventToImport.getStartDate().getSecond());
            stmt.setInt(10, eventToImport.getStartDate().getMillisecond());
            stmt.setInt(11, eventToImport.getEndDate().getYear());
            stmt.setInt(12, eventToImport.getEndDate().getMonth());
            stmt.setInt(13, eventToImport.getEndDate().getDay());
            stmt.setInt(14, eventToImport.getEndDate().getHour());
            stmt.setInt(15, eventToImport.getEndDate().getMinute());
            stmt.setInt(16, eventToImport.getEndDate().getSecond());
            stmt.setInt(17, eventToImport.getEndDate().getMillisecond());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Could not access events database");
        }
        return 0;
    }

    private String importImage(String imageContents) {          //saves an image locally and returns its filepath
        if (imageContents == null)
            return null;
        //try {
        byte[] imageFileContent = Base64.getDecoder().decode(imageContents);
        String filePath = null;         //TODO get Halli to either move his image methods to a util class, or copy them over here
        //File outFile = new File(filePath);
        //FileUtils.writeByteArrayToFile(outFile, imageFileContent);
        return filePath;
        /*} catch (IOException e) {
            System.err.println("Could not create file.");
            return null;
        }*/
    }

    private void importOwner() {
        try {
            DBM.insertIntoDB(owner);
        } catch (SQLException e) {
            System.err.println("Could not access users database");
        }
    }

    private int matchOwnerInDB() {                              //checks if owner is in DB, returns owner's ID if they are
        try (PreparedStatement stmt = DBM.conn.prepareStatement("SELECT UserID FROM users WHERE `UserEmail` = ?")) {
            stmt.setString(1, owner.getUserEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Could not access users database");
        }
        return 0;
    }

    public void importRatings() {
        try (PreparedStatement stmt = DBM.conn.prepareStatement("SELECT UserID, UserEmail FROM users")) {
            ResultSet rs = stmt.executeQuery();

            for (Rating rating : ratings) {     //Iterating through the resultset turned out slightly faster than creating a Map object for quick lookup
                while (rs.next()) {
                    if (rating.userEmail.equals(rs.getString("UserEmail"))) {
                        timeline.addRating(rating.rating, rs.getInt("UserID"));
                        break;
                    }
                }
                rs.beforeFirst();
            }
            rs.close();

            //TODO run some tests to see if creating a Map makes things much faster; large amounts of ratings becomes a massive slowdown, but preminary testing shows very extremely minor differences
            //TODO probably smarter to just calculate all the valid ratings in a batch and then insert them all at once, DB interactions are slow.
            //TODO maybe do above in the actual DBM.insertIntoDB method? It'd be a benefit everywhere, but would take some mild refactoring
            /*
            Map<String, Integer> emailList = new TreeMap<>();
            while (rs.next())                       //adds each user's email address alongside user's ID
                emailList.put(rs.getString("UserEmail"), rs.getInt("UserID"));

            for (Rating rating : ratings) {
                Integer userIDForRating = emailList.get(rating.userEmail);
                if (userIDForRating == null)        //if user with that email is not found in local DB, don't add their rating
                    continue;
                timeline.addRating(rating.rating, userIDForRating);
            }*/
        } catch (SQLException e) {                  //if we can't access the database to match users to local IDs, no point trying to add ratings
            //System.err.println("Could not access users database");
            e.printStackTrace();
        }
    }

    private void setOwnership(int ownerID) {                //sets ownerID of all objects to the inserted value
        owner.setID(ownerID);
        timeline.setOwnerID(ownerID);
        for (int i = 0; i < timeline.getEventList().size(); i++) {
            timeline.getEventList().get(i).setOwnerID(ownerID);
        }
    }

    //Tiny private class to hold a rating with who rated it, email as identifier
    private static class Rating {
        private final String userEmail;
        private final int rating;

        private Rating(String userEmail, int rating) {
            this.userEmail = userEmail;
            this.rating = rating;
        }
    }
}
