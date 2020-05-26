import com.google.gson.Gson;
import controllers.GUIManager;
import database.DBM;
import database.JSONTimeline;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new DBM();                          //establish connection to the DB
        GUIManager.start(primaryStage);     //open the application
        firstTimeSetup();                   //setup database and dummy data if needed
    }

    @Override
    public void stop() {
        DBM.close();        //closes the database connection when mainStage is closed
    }
    private void firstTimeSetup() throws IOException, SQLException {    //check if tables exist in DB, if not then create them and import dummy data
        DatabaseMetaData schemaCheck = DBM.conn.getMetaData();

        try (ResultSet tableList = schemaCheck.getTables(null, null, "timelines", null)) {
            if (tableList.next() && (tableList.getString("TABLE_NAME").equals("timelines")))
                return;
        }
        System.out.println("Beginning first time setup...");
        DBM.setupSchema();
        System.out.println("\nTip: default admin login is Admin@gmail.com using password 'Passw0rd!' Will not show after first time setup.");

        Gson gson = JSONTimeline.getGson();
        String inJSON;

        File directory = new File("src/main/resources/dummy_data/");
        if (directory.listFiles() == null)
            return;
        for (File f : directory.listFiles()) {
            try {
                inJSON = FileUtils.readFileToString(f, (Charset) null);             //import Json from file
                gson.fromJson(inJSON, JSONTimeline.class).importToDB();             //parse Json with GSON object and import it to the DB
            } catch (IOException ignore) {                                          //if one fails to read, skip it
            }
        }
    }
}

