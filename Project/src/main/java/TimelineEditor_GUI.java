import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class TimelineEditor_GUI {

    @FXML private TextArea titleInput;
    @FXML private TextArea descriptionInput;
    @FXML private TextArea keywordsInput;
    @FXML private DatePicker startDateInput;
    @FXML private DatePicker endDateInput;
    @FXML private ComboBox timeInput;


    public TimelineEditor_GUI() throws IOException {
        //this(new Timeline());
    }

    @FXML
    public void initialize() {
        //populateFields();
    }


    public TimelineEditor_GUI(Timeline timeline) {
        //GUIManager.swapScene("TimelineEditor_GUI");

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

        startDateInput = new DatePicker();    //timeline.getStartDate.toLocaleDate() in constructor

        endDateInput = new DatePicker();  //timeline.getEndDate.toLocaleDate() in constructor

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
