import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class EventEditor_GUI extends GridPane {
    private Event event;

    TextField title = new TextField("Title");
    TextField description = new TextField("Description");
    TextField endDate = new TextField("End Date");          //only a text field for skeleton, will figure best way to enter info later
    CheckBox hasDuration = new CheckBox();        //does event have a duration?
    Button saveButton = new Button("Save");

    public EventEditor_GUI(Event event) {
        this.event = event;

        this.setVgap(5);
        this.setHgap(5);
        this.setPadding(new Insets(10, 10, 10, 10));

        setHasDuration(false);

        this.addColumn(0, new Text("Test Test"), title, description, endDate, saveButton);
        this.add(hasDuration, 1, 3);

        hasDuration.setOnAction(e ->
            setHasDuration(hasDuration.isSelected()));


        updateDisplay();
    }

    private void setHasDuration(boolean set){
        endDate.setVisible(set);
        endDate.setManaged(set);
    }

    public boolean changeEvent(int eventID) {
        /*Event newEvent = logic to find Event in database and get its info
        if (newEvent != null)
            return changeEvent(newEvent);*/

        return false;
    }

    public boolean changeEvent(Event event) {
        this.event = event;
        return updateDisplay();
    }

    private boolean updateDisplay() {
        return false;
    }

    public boolean saveToDB() {
        //setters to update each field of this.event, based on the current info in the text fields

        /*try {
         if (event.getEventID = 0)
            DBM.addToDB(event);
        else
            DBM.updateInDB(event);
         return true;
        } catch (SQLException e){
            return false;
        }*/

        return false;
    }
}