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

import java.sql.PreparedStatement;

import java.io.IOException;

import java.sql.SQLException;
import java.util.List;

public class LoginAndRegistration_GUI {

    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;
    @FXML private Text errorMessage;
    @FXML private TextField emailInput;
    @FXML public PasswordField confirmPasswordInput;


    public LoginAndRegistration_GUI() {
        GUIManager.mainStage.setTitle("Welcome Screen");
    }

    @FXML
    private void loginScreen() throws IOException {
        //This is the Stage for the Login Window
        Stage loginStage = new Stage();
        loginStage.setTitle("Login Screen");
        loginStage.initOwner(GUIManager.mainStage);         //These two lines make sure you can't click back to the Start Window,
        loginStage.initModality(Modality.WINDOW_MODAL);     //so you can't have 10 Login Windows open at once.

        Parent root = FXMLLoader.load(GUIManager.class.getResource("FXML/Login_Screen.fxml"));
        loginStage.setScene(new Scene(root));
        loginStage.getScene().getStylesheets().add("File:src/main/resources/styles/DefaultStyle.css");
        loginStage.show();
    }

    @FXML
    private void registerScreen() throws IOException {
        //This is the Stage for the Register Window
        Stage registerStage = new Stage();
        registerStage.setTitle("Register Screen");
        registerStage.initOwner(GUIManager.mainStage);      //These are the same as before, prevents the window from losing focus until closed.
        registerStage.initModality(Modality.WINDOW_MODAL);  //I don't actually know what Modality is, Google just said this works and it does.

        registerStage.setScene(new Scene(FXMLLoader.load(GUIManager.class.getResource("FXML/Register_Screen.fxml"))));
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
                // close the window once successful
                ((Node) (event.getSource())).getScene().getWindow().hide();
            }
        } catch (IllegalArgumentException | SQLException e) {
            errorMessage.setText(e.getMessage());
        }

    }

    @FXML
    public void loginUser(MouseEvent event) {
// Reset the error message if the input fields match after getting the error
        errorMessage.setText("");

        try {
            if (usernameInput.getText().trim().equals("") || passwordInput.getText().trim().equals("")) { // invalid
                // inputs
                errorMessage.setText("Username or password invalid!");
            } else { // valid inputs
                PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM users WHERE userEmail = ?");
                stmt.setString(1, usernameInput.getText());
                // list of users that match the input email (hopefully length 1)
                List<User> dbResult = DBM.getFromDB(stmt, new User());
                if (dbResult.size() > 1)
                    throw new SQLException(
                            "Multiple users found, something went horribly wrong, contact tech support!");
                else if (dbResult.size() == 0) { // no user found
                    errorMessage.setText("Email not found in database!");
                } else { // user found, time for password check
                    User user = dbResult.get(0);

                    boolean isValid = user.verifyPass(passwordInput.getText(), user.getEncrypted(), user.getSalt());

                    if (!isValid) {
                        errorMessage.setText("Invalid password!");
                    } else { // log in!!!
                        GUIManager.loggedInUser = user;

                        //update menubar text for loggedin status and enable menu item
                        GUIManager.menu.updateLoggedInStatus();

                        //hide login window
                        ((Node) (event.getSource())).getScene().getWindow().hide();
                        GUIManager.swapScene("Dashboard");

                    }
                }
            }
        } catch (SQLException | IOException e) {
            errorMessage.setText(e.getMessage());
        }
    }

}
