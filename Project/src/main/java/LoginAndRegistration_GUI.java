import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginAndRegistration_GUI extends VBox {


    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;
    @FXML private Text errorMessage;
    @FXML private TextField emailInput;
    @FXML public PasswordField confirmPasswordInput;



    public LoginAndRegistration_GUI() {

    }

    @FXML
    private void loginScreen() throws IOException {
        //This is the Stage for the Login Window
        Stage loginStage = new Stage();
        loginStage.setTitle("Login Screen");
        loginStage.initOwner(OldGUIManager.mainStage);                 //These two lines make sure you can't click back to the Start Window,

        loginStage.initModality(Modality.WINDOW_MODAL);     //so you can't have 10 Login Windows open at once.

        Parent root = FXMLLoader.load(GUIManager.class.getResource("fxml/Login_Screen.fxml"));
        loginStage.setScene(new Scene(root));
        loginStage.getScene().getStylesheets().add("File:src/main/resources/styles/DefaultStyle.css");
        loginStage.show();
    }

    @FXML
    private void registerScreen() throws IOException {
        //This is the Stage for the Register Window
        Stage registerStage = new Stage();
        registerStage.setTitle("Register Screen");

        registerStage.initOwner(OldGUIManager.mainStage);              //These are the same as before, prevents the window from losing focus until closed.
        registerStage.initModality(Modality.WINDOW_MODAL);  //I don't actually know what Modality is, Google just said this works and it does.

        Parent root = FXMLLoader.load(GUIManager.class.getResource("fxml/Register_Screen.fxml"));
        registerStage.setScene(new Scene(root));
        registerStage.getScene().getStylesheets().add("File:src/main/resources/styles/DefaultStyle.css");
        registerStage.show();
    }

    @FXML
    public void close(MouseEvent mouseEvent) {
        ((Node) (mouseEvent.getSource())).getScene().getWindow().hide();
    }

    @FXML
    public void registerUser(MouseEvent event) {

        //Reset the error message if the input fields match after getting the error
        errorMessage.setText("");
        try {

            // Check if the email is valid (unique)
            if (!User.validateUnique(emailInput.getText())) {
                errorMessage.setText("Email already in use");

                //If the passwordInput's text does not equal the confirmPasswordInput's text
            } else if (!passwordInput.getText().equals(confirmPasswordInput.getText())) {
                errorMessage.setText("Error: the inputted passwords do not match.");

                // Check if the Username field is not empty
            } else if (usernameInput.getText().equals("")) {
                errorMessage.setText("Please enter a Username");


                // If everything checks out, create a new user
            } else {

                DBM.insertIntoDB(new User(usernameInput.getText(), emailInput.getText(), passwordInput.getText()));
                // close the window once successful, and switch to the dashboard
                ((Node) (event.getSource())).getScene().getWindow().hide();
                //GUIManager.swapScene(new Dashboard_GUI());
            }
        } catch (IllegalArgumentException | SQLException e) {
            errorMessage.setText(e.getMessage());
        }

    }

    @FXML
    public void loginUser(MouseEvent event) {
        //To be implemented later.
        System.out.println("The \"Login\" button has been pressed.");
    }

    @FXML
    public void timelineScreen() throws IOException, SQLException {
        GUIManager.swapScene("Timeline_Editor_Screen");
    }



}
