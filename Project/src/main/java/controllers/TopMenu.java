package controllers;

import com.google.gson.Gson;
import database.DBM;
import database.JSONTimeline;
import database.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TopMenu {

    @FXML
    Menu fileMenu;
    @FXML
    MenuItem saveButton = new MenuItem();
    @FXML
    Menu loggedInStatus = new Menu();
    MenuItem export = new MenuItem("Export");
    private File FileChosen;

    public void initialize() {
        updateLoggedInStatus();
        showExportMenu(false);
    }

    @FXML
    void saveFile(ActionEvent actionEvent) {
        System.out.println("Save");
    }

    @FXML
    void styleDefaultPressed() {
        GUIManager.applyStyle("DefaultStyle");
    }

    @FXML
    void styleNonePressed() {
        GUIManager.applyStyle("None");
    }

    void showExportMenu(boolean show) {
        if (fileMenu.getItems().contains(export) == show)        //check if file menu already contains export button
            return;

        if (show)
            fileMenu.getItems().add(export);
        else
            fileMenu.getItems().remove(export);
    }

    @FXML
    void updateLoggedInStatus() {
        if (GUIManager.loggedInUser == null) {
            loggedInStatus.setText("Not logged in");
            loggedInStatus.setDisable(true);
        } else {
            loggedInStatus.setText("Logged in as: " + GUIManager.loggedInUser.getUserEmail());
            loggedInStatus.setDisable(false);
        }
    }

    @FXML
    void logOutPressed() {
        GUIManager.loggedInUser = null;
        updateLoggedInStatus();
        try {
            GUIManager.swapScene("Welcome");
        } catch (IOException e) {

        }
    }

    /*boolean isDuplicate(String name) throws SQLException {        //no need to prevent admins from uploading a duplicate timeline, they can do as they like
        List<String> timelineNames = DBM.getFromDB(DBM.conn.prepareStatement("SELECT TimelineName FROM timelines"),
                rs -> rs.getString("TimelineName"));
        return timelineNames.contains(name);
    }*/

    @FXML
    void importFromJSON() throws FileNotFoundException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"));
        FileChosen = chooser.showOpenDialog(GUIManager.mainStage);
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Look, Your file now in Database");
            alert.setContentText("You can use it now :)");
            alert.showAndWait();
        }catch (Exception e){

        }
        Gson gson = new Gson();
        File file = new File(String.valueOf(FileChosen));
        Scanner inFile = new Scanner(file);
        JSONTimeline readJson = gson.fromJson(inFile.nextLine(), JSONTimeline.class);
        readJson.importToDB();
        inFile.close();


    }

    void exportToJSON(Timeline timelineToExport) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save TimeLine as JSON");
        chooser.setInitialFileName(timelineToExport.getName());
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = chooser.showSaveDialog(GUIManager.mainStage);

        if ( file != null ) {
            try {
                File dir = file.getParentFile();
                chooser.setInitialDirectory(dir);
                JSONTimeline exportable = new JSONTimeline(timelineToExport);       //gather all relevant information about a timeline into one object
                String out = new Gson().toJson(exportable);                         //convert that to JSON-formatted String
                System.out.println(out + "\n");
                chooser.setInitialDirectory(dir);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Look, Your file now in the path!");
                alert.setContentText("You check it right now in :"+"\n"+dir);
                alert.showAndWait();
                try (PrintWriter outFile = new PrintWriter(file)) {                 //write JSON-formatted info to file
                    outFile.println(out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();                //TODO better exception handling once dev work is done
                }
                System.out.println("\nExported successfully");
            }
            catch (Exception e) {
                e.printStackTrace();
            }


        }




    }


}
