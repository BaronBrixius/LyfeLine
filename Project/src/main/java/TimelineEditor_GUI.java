import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class TimelineEditor_GUI {

    @FXML private TextArea titleInput;
    @FXML private TextArea descriptionInput;
    @FXML private TextArea keywordsInput;
    @FXML private DatePicker startDateInput;
    @FXML private DatePicker endDateInput;
    @FXML private ComboBox timeInput;


    public TimelineEditor_GUI() {

    }


    @FXML
    public void initialize() throws IOException {
        Timeline timeline = new Timeline(); //If editing instead of creating, make this equal to the Timeline to be edited.

        titleInput = new TextArea(timeline.getName());

        descriptionInput = new TextArea();  //put timeline.getTimelineDescription, or however it's called, into the constructor.


        StringBuilder keywordsList = new StringBuilder();
        /*
        This is how I'm assuming the keywords get into the TextArea when they are implemented.
        Store them as a String Array, and append them one by one to a StringBuilder.
        Append a comma and a space for everyone of them except the last one, then append that one manually.

        for (int i = 0; i < timeline.getKeywords - 1; i++) {
            keywordsList.append(timeline.getKeywords.get(i) + ", ");
        }
        keywordsList.append(timeline.getKeywords.get(timeline.getKeywords.size - 1));
        */
        keywordsInput = new TextArea(keywordsList.toString());

        startDateInput = new DatePicker();    //put timeline.getStartDate.toLocaleDate() in constructor

        endDateInput = new DatePicker();  //put timeline.getEndDate.toLocaleDate() in constructor

        ObservableList<String> timeUnits = FXCollections.observableArrayList();
        timeUnits.addAll("Seconds", "Minutes", "Hours", "Days", "Years");
        timeInput = new ComboBox<>(timeUnits);

    }

    @FXML
    public void save(MouseEvent event) {
        System.out.println("The Save button has been pushed.");
    }

    @FXML
    public void cancel() throws IOException {
        GUIManager.swapScene("Welcome_Screen");
    }


}
