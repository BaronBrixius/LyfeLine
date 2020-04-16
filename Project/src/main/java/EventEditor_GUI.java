import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class EventEditor_GUI extends GridPane {
    private Event event;

    private TextField titleInput = new TextField("Title");
    private TextField descInput = new TextField("Description");
    private RadioButton durationInput = new RadioButton();           //does event have a duration?


    public EventEditor_GUI(Event event) {
        this.event = event;
        updateDisplay();

        this.setVgap(5);
        this.setHgap(5);
        this.setPadding(new Insets(0, 0, 10, 10));

        this.add(new Text("Test Test"), 0, 0);
        //this.add( = new TextField("search here");)

        this.add(titleInput, 0, 1);
        this.add(descInput, 0, 2);
        this.add(durationInput, 0, 3);

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