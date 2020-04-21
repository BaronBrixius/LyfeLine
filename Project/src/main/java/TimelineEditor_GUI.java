import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;

public class TimelineEditor_GUI {

    @FXML private TextArea titleInput;
    @FXML private TextArea descriptionInput;
    @FXML private TextArea keywordsInput;
    @FXML private DatePicker startDateInput;
    @FXML private DatePicker endDateInput;
    @FXML private ComboBox<String> timeInput;


    public TimelineEditor_GUI() {

    }

    @FXML
    private void initialize() throws SQLException {
        //Timeline here just to test field populating. Replace replace it with the proper timeline, or with blank timeline if creating.
        Timeline timeline = new Timeline(0, "My timeline", "Very cool timeline", "Month", "pink", new Date(1,0,0,0,0,0,0), new Date(2,0,0,0,0,0,0),  new Date(2,0,0,0,0,0,0), 10, false);

        titleInput.setText(timeline.getName());

        descriptionInput.setText(""); //put timeline.getTimelineDescription, or however it's called, into the constructor.

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
        keywordsInput.setText(keywordsList.toString());

        //startDateInput.setValue();    //put timeline.getStartDate.toLocaleDate() as parameter

        //endDateInput.setValue();  //put timeline.getEndDate.toLocaleDate() as parameter

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
