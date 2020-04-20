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

public class LoginAndRegistration_GUI extends VBox {

/*
	public static Scene welcomeScreen() {
		// This is the Start Window
		GUIManager.mainStage.setTitle("Welcome Screen");

		// This is the Stage for the Login Window
		Stage loginStage = new Stage();
		loginStage.setTitle("Login Screen");
		loginStage.initOwner(GUIManager.mainStage); // These two lines make sure you can't click back to the Start
													// Window,
		loginStage.initModality(Modality.WINDOW_MODAL); // so you can't have 10 Login Windows open at once.

		// This is the Stage for the Register Window
		Stage registerStage = new Stage();
		registerStage.setTitle("Register Screen");
		registerStage.initOwner(GUIManager.mainStage); // These are the same as before, prevents the window from losing
														// focus until closed.
		registerStage.initModality(Modality.WINDOW_MODAL); // I don't actually know what Modality is, Google just said
															// this works and it does.

		// This HBox holds the three buttons: Login, Register, and Continue as guest
		HBox menuOptions = new HBox(30);
		menuOptions.setPadding(new Insets(50));
		menuOptions.setAlignment(Pos.TOP_CENTER);

		// This button when clicked opens the Login Window in a new pop-up
		Button login = new Button("Login");
		login.setOnAction(event -> {
			loginStage.setScene(loginScreen());
			loginStage.getScene().getStylesheets().add("File:src/main/resources/" + GUIManager.mainStyle + ".css");
			loginStage.show();
		});

		// This button when clicked opens the Register Window in a new pop-up
		Button register = new Button("Register");
		register.setOnAction(event -> {
			registerStage.setScene(registerScreen());
			registerStage.getScene().getStylesheets().add("File:src/main/resources/" + GUIManager.mainStyle + ".css");
			registerStage.show();
		});

		menuOptions.getChildren().addAll(login, register);

		// This is a picture of the temporary logo. When a permanent logo is settled on,
		// just name it Logo.png, and put it in the resources folder
		ImageView logo = new ImageView(new Image("File:src/main/resources/Logo.png"));
		logo.setScaleX(.75);
		logo.setScaleY(.75);

		// This VBox holds the HBox that holds the buttons, the VBox that holds the the
		// dropdown menus, and the logo
		VBox everything = new VBox(20);
		everything.getChildren().addAll(dropDownMenus(), menuOptions, logo);
		everything.setAlignment(Pos.TOP_CENTER);

		return new Scene(everything, 1300, 750);

	}

	private static Scene registerScreen() {
		// This GridPane holds all text on the left, all input fields on the right, and
		// the HBox that holds the buttons under the input fields.
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10);
		pane.setVgap(50);

		// These are the texts in order from top to bottom
		Text username = new Text("Username");
		pane.add(username, 0, 1);

		Text password = new Text("Password");
		pane.add(password, 0, 2);

		Text confirmPassword = new Text("Confirm Password");
		pane.add(confirmPassword, 0, 3);

		Text email = new Text("Email Address");
		pane.add(email, 0, 0);

		// This text alerts the user if their inputted information is wrong in any way
		Text errorMessage = new Text();
		errorMessage.setWrappingWidth(190);
		errorMessage.getStyleClass().add("smallText");
		pane.add(errorMessage, 0, 4);

		// These are the input fields in order from top to bottom
		final TextField emailInput = new TextField();
		pane.add(emailInput, 1, 0);

		final TextField usernameInput = new TextField();
		pane.add(usernameInput, 1, 1);

		final PasswordField passwordInput = new PasswordField();
		pane.add(passwordInput, 1, 2);

		final PasswordField confirmPasswordInput = new PasswordField();
		pane.add(confirmPasswordInput, 1, 3);

		// This button only checks if the passwordInput and confirmPasswordInput fields
		// are the same right now.
		// Will eventually create a User from the inputted data.
		Button register = new Button("Register");
		register.getStyleClass().add("smallButton");
		register.setOnAction(event -> {

			// Reset the error message if the input fields match after getting the error
			errorMessage.setText("");

			try {

				// Check if the email is valid (unique)
				if (!User.validateUnique(emailInput.getText())) {
					errorMessage.setText("Email already in use");

					// If the passwordInput's text does not equal the confirmPasswordInput's text
				} else if (!passwordInput.getText().equals(confirmPasswordInput.getText())) {
					errorMessage.setText("Error: the inputted passwords do not match.");

					// Check if the Username field is not empty
				} else if (usernameInput.getText().equals("")) {
					errorMessage.setText("Please enter a Username");

					// If everything checks out, create a new user
				} else {

					DBM.insertIntoDB(new User(usernameInput.getText(), emailInput.getText(), passwordInput.getText()));
					// close the window once successful, and switch do the dashboard
					((Node) (event.getSource())).getScene().getWindow().hide();
					GUIManager.swapScene(LoginAndRegistration_GUI.welcomeScreen());
				}
			} catch (IllegalArgumentException | SQLException e) {
				errorMessage.setText(e.getMessage());
			}

		});

		// This button closes the Registration window
		Button cancel = new Button("Cancel");
		cancel.getStyleClass().add("smallButton");
		cancel.setOnAction(event -> ((Node) (event.getSource())).getScene().getWindow().hide()); // This is the line
																									// that actually
																									// closes the window

		// This HBox holds the buttons Register and Cancel
		HBox buttons = new HBox(20);
		buttons.getChildren().addAll(register, cancel);
		pane.add(buttons, 1, 4);

		return new Scene(pane, 650, 450);
	}

	private static Scene loginScreen() {
		// This GridPane holds all text on the left, all input fields on the right, and
		// the HBox that holds the buttons under the input fields.
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(20);
		pane.setVgap(50);

		// These are the texts in order from top to bottom
		Text username = new Text("Email");
		pane.add(username, 0, 0);

		Text password = new Text("Password");
		pane.add(password, 0, 1);

		// This text alerts the user if their inputted information is wrong in any way
		Text errorMessage = new Text();
		errorMessage.setWrappingWidth(100);
		errorMessage.getStyleClass().add("smallText");
		pane.add(errorMessage, 0, 2);

		// These are the input fields in order from top to bottom
		final TextField usernameInput = new TextField();
		pane.add(usernameInput, 1, 0);

		final PasswordField passwordInput = new PasswordField();
		pane.add(passwordInput, 1, 1);

		// log in button
		Button login = new Button("Login");
		login.setOnAction(event -> {

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
							((Node) (event.getSource())).getScene().getWindow().hide();
							GUIManager.swapScene(Dashboard_GUI.DashboardScreen());
						}
					}

				}
			} catch (SQLException e) {
				errorMessage.setText(e.getMessage());
			}
		});
		login.getStyleClass().add("smallButton");

		// This button closes the Login window
		Button cancel = new Button("Cancel");
		cancel.setOnAction(event -> ((Node) (event.getSource())).getScene().getWindow().hide());
		cancel.getStyleClass().add("smallButton");

		// This HBox holds the buttons Login and Cancel
		HBox buttons = new HBox(20);
		buttons.getChildren().addAll(login, cancel);
		pane.add(buttons, 1, 2);

		return new Scene(pane, 600, 300);
	}

	// This method creates the dropdown menus in the top right of most windows
	public static VBox dropDownMenus() {
		HBox menus = new HBox();

		// These are the items in the File dropdown menu
		MenuItem save = new MenuItem("Save");
		save.setOnAction(e -> System.out.println("The \"Save\" menu button has been pressed."));

		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(e -> System.out.println("The \"Delete\" menu button has been pressed."));

		MenuItem zoom = new MenuItem("Zoom");
		zoom.setOnAction(e -> System.out.println("The \"Zoom\" menu button has been pressed."));

		// This is the File dropdown menu in the top left
		Menu menuFile = new Menu("File");
		menuFile.getItems().addAll(save, delete, zoom);

		// This is the only item in the Edit dropdown menu
		MenuItem editMode = new MenuItem("Edit Mode");
		editMode.setOnAction(e -> System.out.println("The \"Edit Mode\" menu button has been pressed."));

		// This is the Edit dropdown menu in the top left
		Menu menuEdit = new Menu("Edit");
		menuEdit.getItems().addAll(editMode);

		// This is the only item in the View dropdown menu
		MenuItem viewMode = new MenuItem("View Mode");
		viewMode.setOnAction(e -> System.out.println("The \"View Mode\" menu button has been pressed."));

		// This is the View dropdown menu in the top left
		Menu menuView = new Menu("View");
		menuView.getItems().addAll(viewMode);

		// This is the bar that holds the dropdown menus in the top left
		MenuBar bar = new MenuBar();
		bar.getMenus().addAll(menuFile, menuEdit, menuView);

		// spacer approach adapted from https://stackoverflow.com/a/39282816
		// spacer to push the loggedInText to the right of the screen
		Region spacer = new Region();
		spacer.getStyleClass().add("menu-bar");
		HBox.setHgrow(spacer, Priority.SOMETIMES);

		// This is the bar that holds the logged-in-info
		MenuBar loggedInBar = new MenuBar();

		// set text depending on login session
		Menu loggedInText = new Menu();
		if (null == GUIManager.loggedInUser) {
			loggedInText.setText("Not logged in");
			loggedInText.setDisable(true);
		} else {
			loggedInText.setText("Logged in as: " + GUIManager.loggedInUser.getUserEmail());
			loggedInText.setDisable(false);
		}

		MenuItem logout = new MenuItem("Logout");
		logout.setOnAction(event -> {
			GUIManager.loggedInUser=null;
			//GUIManager.swapScene(LoginAndRegistration_GUI.welcomeScreen());
		});
		loggedInText.getItems().addAll(logout);


		loggedInBar.getMenus().add(loggedInText);

		menus.getChildren().addAll(bar, spacer, loggedInBar);

		VBox vbox = new VBox();
		vbox.getChildren().addAll(menus);

		return vbox;
	}

*/
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
        loginStage.initOwner(GUIManager.mainStage);                 //These two lines make sure you can't click back to the Start Window,

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

        registerStage.initOwner(GUIManager.mainStage);              //These are the same as before, prevents the window from losing focus until closed.
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
                        ((Node) (event.getSource())).getScene().getWindow().hide();
                        GUIManager.swapScene("EventEditor");
                    }
                }
            }
        } catch (SQLException | IOException e) {
            errorMessage.setText(e.getMessage());
        }
    }


    @FXML
    public void timelineScreen() throws IOException {
        GUIManager.swapScene("Timeline_Editor_Screen");
    }


}
