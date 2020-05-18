package controllers;

import com.google.gson.Gson;
import database.DBM;
import database.JSONTimeline;
import database.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TopMenu {

    private List<List<String>> timelineNames;
    public Menu fileMenu;
    MenuItem export = new MenuItem("Export");
    @FXML
    MenuItem saveButton = new MenuItem();
    @FXML
    Menu loggedInStatus = new Menu();

    public void initialize() {
        updateLoggedInStatus();
        showExportMenu(false);
    }

    @FXML
    public void saveFile(ActionEvent actionEvent) {
        System.out.println("Save");
    }
    
    @FXML
    public void styleDefaultPressed() {
    	GUIManager.applyStyle("DefaultStyle");
    }
    
    @FXML
    public void styleNonePressed() {
    	GUIManager.applyStyle("None");
    }

    void showExportMenu(boolean show){
        if (fileMenu.getItems().contains(export) == show)        //check if file menu already contains export button
            return;

        if (show)
            fileMenu.getItems().add(export);
        else
            fileMenu.getItems().remove(export);
    }

    @FXML
    public void updateLoggedInStatus() {
        if (GUIManager.loggedInUser == null) {
            loggedInStatus.setText("Not logged in");
            loggedInStatus.setDisable(true);
        } else {
            loggedInStatus.setText("Logged in as: " + GUIManager.loggedInUser.getUserEmail());
            loggedInStatus.setDisable(false);
        }
    }

    @FXML
    public void logOutPressed() {
        GUIManager.loggedInUser = null;
        updateLoggedInStatus();
        try {
            GUIManager.swapScene("Welcome");
        } catch (IOException e) {

        }
    }
    boolean isduplicate(String name) throws SQLException {
        timelineNames = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM timelines"),
                rs -> Arrays.asList(rs.getString("TimelineName")));
        for (int i = 0; i < timelineNames.size(); i++) {
            if(timelineNames.get(i).contains(name)){
                return true;
            }
        }
        return false;
    }
    @FXML
    void importFromJSON() throws FileNotFoundException, SQLException {
     /*   timelineNames = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM timelines"),
                rs -> Arrays.asList(rs.getString("TimelineName")));*/
        String name = "Bronze Age Collapse";
        if (isduplicate(name) == false){
            Gson gson = new Gson();
            File file = new File("D:\\Java\\java_courses\\1Dv508\\Project\\jsonTest.json");
            Scanner inFile = new Scanner(file);
            JSONTimeline readJson = gson.fromJson(inFile.nextLine(), JSONTimeline.class);
            readJson.importToDB();
            inFile.close();
        }else
            System.out.println("Error");
    }

    void exportToJSON(Timeline timelineToExport) throws FileNotFoundException {
        Gson gson = new Gson();
        JSONTimeline exportable = new JSONTimeline(timelineToExport);
        String out = gson.toJson(exportable);
        System.out.println(out + "\n");
        String nameOfTimeline = timelineToExport.getName();
        File file = new File(nameOfTimeline + ".json");
        PrintWriter outFile = new PrintWriter(file);
        outFile.println(out);
        outFile.close();
        System.out.println();
        System.out.println("Exported successfully");
    }
}
