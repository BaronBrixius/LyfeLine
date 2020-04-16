import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginAndRegistration_GUI {

    public static Scene welcomeScreen() {
        //This is the Start Window
        GUIManager.mainStage.setTitle("Welcome Screen");

        //This is the Stage for the Login Window
        Stage loginStage = new Stage();
        loginStage.setTitle("Login Screen");
        loginStage.initOwner(GUIManager.mainStage);                 //These two lines make sure you can't click back to the Start Window,
        loginStage.initModality(Modality.WINDOW_MODAL);     //so you can't have 10 Login Windows open at once.

        //This is the Stage for the Register Window
        Stage registerStage = new Stage();
        registerStage.setTitle("Register Screen");
        registerStage.initOwner(GUIManager.mainStage);              //These are the same as before, prevents the window from losing focus until closed.
        registerStage.initModality(Modality.WINDOW_MODAL);  //I don't actually know what Modality is, Google just said this works and it does.


        //This HBox holds the three buttons: Login, Register, and Continue as guest
        HBox menuOptions = new HBox(30);
        menuOptions.setPadding(new Insets(50));
        menuOptions.setAlignment(Pos.TOP_CENTER);

        //This button when clicked opens the Login Window in a new pop-up
        Button login = new Button("Login");
        login.setOnAction(event -> {
            loginStage.setScene(loginScreen());
            loginStage.getScene().getStylesheets().add("File:src/main/resources/"+ GUIManager.mainStyle +".css");
            loginStage.show();
        });

        //This button when clicked opens the Register Window in a new pop-up
        Button register = new Button("Register");
        register.setOnAction(event -> {
            registerStage.setScene(registerScreen());
            registerStage.getScene().getStylesheets().add("File:src/main/resources/"+ GUIManager.mainStyle +".css");
            registerStage.show();
        });

        //This button opens the Dashboard Scene in the same window.
        Button guest = new Button("Continue as guest");
        guest.setOnAction(event -> {
            GUIManager.swapScene(Dashboard_GUI.DashboardScreen());
            GUIManager.mainStage.setTitle("Dashboard");
        });


        menuOptions.getChildren().addAll(login, register, guest);

        //This is a picture of the temporary logo. When a permanent logo is settled on, just name it Logo.png, and put it in the resources folder
        ImageView logo = new ImageView(new Image("File:src/main/resources/Logo.png"));
        logo.setScaleX(.75);
        logo.setScaleY(.75);


        //This VBox holds the HBox that holds the buttons, the VBox that holds the the dropdown menus, and the logo
        VBox everything = new VBox(20);
        everything.getChildren().addAll(dropDownMenus(), menuOptions, logo);
        everything.setAlignment(Pos.TOP_CENTER);


       return new Scene(everything, 1300, 750);

    }

    private static Scene registerScreen() {
        //This GridPane holds all text on the left, all input fields on the right, and the HBox that holds the buttons under the input fields.
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(50);

        //These are the texts in order from top to bottom
        Text username = new Text("Username");
        pane.add(username, 0, 1);

        Text password = new Text("Password");
        pane.add(password, 0, 2);

        Text confirmPassword = new Text("Confirm Password");
        pane.add(confirmPassword, 0, 3);

        Text email = new Text("Email Address");
        pane.add(email, 0, 0);

        //This text alerts the user if their inputted information is wrong in any way
        Text errorMessage = new Text();
        errorMessage.setWrappingWidth(190);
        errorMessage.getStyleClass().add("smallText");
        pane.add(errorMessage, 0, 4);

        //These are the input fields in order from top to bottom
        final TextField emailInput = new TextField();
        pane.add(emailInput, 1, 0);

        final TextField usernameInput = new TextField();
        pane.add(usernameInput, 1, 1);

        final TextField passwordInput = new TextField();
        pane.add(passwordInput, 1, 2);

        final TextField confirmPasswordInput = new TextField();
        pane.add(confirmPasswordInput, 1, 3);


        //This button only checks if the passwordInput and confirmPasswordInput fields are the same right now.
        //Will eventually create a User from the inputted data.
        Button register = new Button("Register");
        register.getStyleClass().add("smallButton");
        register.setOnAction(event -> {

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
                    // close the window once successful, and switch do the dashboard
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                    GUIManager.swapScene(Dashboard_GUI.DashboardScreen());
                    GUIManager.mainStage.setTitle("Dashboard");
                }
            } catch (IllegalArgumentException | SQLException e) {
                errorMessage.setText(e.getMessage());
            }

        });


        //This button closes the Registration window
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("smallButton");
        cancel.setOnAction(event -> ((Node)(event.getSource())).getScene().getWindow().hide()); //This is the line that actually closes the window

        //This HBox holds the buttons Register and Cancel
        HBox buttons = new HBox(20);
        buttons.getChildren().addAll(register, cancel);
        pane.add(buttons, 1, 4);


        return new Scene(pane, 650, 450);
    }

    private static Scene loginScreen() {
        //This GridPane holds all text on the left, all input fields on the right, and the HBox that holds the buttons under the input fields.
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        pane.setVgap(50);

        //These are the texts in order from top to bottom
        Text username = new Text("Username");
        pane.add(username, 0, 0);

        Text password = new Text("Password");
        pane.add(password, 0, 1);

        //These are the input fields in order from top to bottom
        final TextField usernameInput = new TextField();
        pane.add(usernameInput, 1, 0);

        final TextField passwordInput = new TextField();
        pane.add(passwordInput, 1, 1);

        //This button does nothing right now. Will eventually connect the User to their account.
        Button login = new Button("Login");
        login.setOnAction(event -> System.out.println("The \"Login\" button has been pressed."));
        login.getStyleClass().add("smallButton");

        //This button closes the Login window
        Button cancel = new Button("Cancel");
        cancel.setOnAction(event -> ((Node)(event.getSource())).getScene().getWindow().hide());
        cancel.getStyleClass().add("smallButton");

        //This HBox holds the buttons Login and Cancel
        HBox buttons = new HBox(20);
        buttons.getChildren().addAll(login, cancel);
        pane.add(buttons, 1, 2);


        return new Scene(pane, 600, 300);
    }

    //This method creates the dropdown menus in the top right of most windows
    public static VBox dropDownMenus() {
        //These are the items in the File dropdown menu
        MenuItem save = new MenuItem("Save");
        save.setOnAction(e -> System.out.println("The \"Save\" menu button has been pressed."));

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> System.out.println("The \"Delete\" menu button has been pressed."));

        MenuItem zoom = new MenuItem("Zoom");
        zoom.setOnAction(e -> System.out.println("The \"Zoom\" menu button has been pressed."));

        //This is the File dropdown menu in the top left
        Menu menuFile = new Menu("File");
        menuFile.getItems().addAll(save, delete, zoom);

        //This is the only item in the Edit dropdown menu
        MenuItem editMode = new MenuItem("Edit Mode");
        editMode.setOnAction(e -> System.out.println("The \"Edit Mode\" menu button has been pressed."));

        //This is the Edit dropdown menu in the top left
        Menu menuEdit = new Menu("Edit");
        menuEdit.getItems().addAll(editMode);

        //This is the only item in the View dropdown menu
        MenuItem viewMode = new MenuItem("View Mode");
        viewMode.setOnAction(e -> System.out.println("The \"View Mode\" menu button has been pressed."));

        //This is the View dropdown menu in the top left
        Menu menuView = new Menu("View");
        menuView.getItems().addAll(viewMode);

        //This is the bar that holds the dropdown menus in the top left
        MenuBar bar = new MenuBar();
        bar.getMenus().addAll(menuFile, menuEdit, menuView);
        
        VBox menus = new VBox();
        menus.getChildren().addAll(bar);
        return menus;
    }

}
