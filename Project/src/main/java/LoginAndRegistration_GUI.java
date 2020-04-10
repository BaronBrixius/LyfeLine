import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginAndRegistration_GUI extends Application {


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        //This is the Stage for the Login Window
        Stage loginStage = new Stage();
        loginStage.setScene(loginScreen());
        loginStage.setTitle("Login Screen");
        loginStage.initOwner(primaryStage);                 //These two lines make sure you can't click back to the Start Window,
        loginStage.initModality(Modality.WINDOW_MODAL);     //so you can't have 10 Login Windows open at once.

        //This is the Stage for the Register Window
        Stage registerStage = new Stage();
        registerStage.setScene(registerScreen());
        registerStage.setTitle("Register Screen");
        registerStage.initOwner(primaryStage);              //These are the same as before, prevents the window from losing focus until closed.
        registerStage.initModality(Modality.WINDOW_MODAL);  //I don't actually know what Modality is, Google just said this works and it does.


        //This is the Start Window
        primaryStage.setTitle("Welcome Screen");

        //This HBox holds the three buttons: Login, Register, and Continue as guest
        HBox menuOptions = new HBox(30);
        menuOptions.setPadding(new Insets(50));
        menuOptions.setAlignment(Pos.TOP_CENTER);

        //This button when clicked opens the Login Window in a new pop-up
        Button login = new Button();
        login.setText("Login");
        login.setOnAction(event -> loginStage.show());
        login.setPrefWidth(250);
        login.setPrefHeight(100);
        login.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-font-size: 2em; -fx-font-weight: bold");

        //This button when clicked opens the Register Window in a new pop-up
        Button register = new Button();
        register.setText("Register");
        register.setOnAction(event -> registerStage.show());
        register.setPrefWidth(250);
        register.setPrefHeight(100);
        register.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-font-size: 2em; -fx-font-weight: bold");

        //This button does nothing right now. Will eventually let people look at timelines without logging in.
        Button guest = new Button();
        guest.setText("Continue as guest");
        guest.setOnAction(event -> System.out.println("The \"Continue as guest\" button has been pressed."));
        guest.setPrefWidth(250);
        guest.setPrefHeight(100);
        guest.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-font-size: 2em; -fx-font-weight: bold");

        menuOptions.getChildren().addAll(login, register, guest);

        //This VBox holds the HBox that holds the buttons as well as the logo
        VBox everything = new VBox(5);
        everything.getChildren().addAll(dropDownMenus(), menuOptions, logo());
        everything.setAlignment(Pos.TOP_CENTER);
        everything.setStyle("-fx-background-color: #9a9a9a;");  //This changes the background color of the whole window.


        Scene scene = new Scene(everything, 1300, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Scene registerScreen() {
        //This GridPane holds all text on the left, all input fields on the right, and the HBox that holds the buttons under the input fields.
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        pane.setVgap(50);

        //These are the texts in order from top to bottom
        Text username = new Text("Username");
        username.setStyle("-fx-font-size: 2em;");
        pane.add(username, 0, 0);

        Text password = new Text("Password");
        password.setStyle("-fx-font-size: 2em;");
        pane.add(password, 0, 1);

        Text confirmPassword = new Text("Confirm Password");
        confirmPassword.setStyle("-fx-font-size: 2em;");
        pane.add(confirmPassword, 0, 2);

        //These are the input fields in order from top to bottom
        final TextField usernameInput = new TextField();
        usernameInput.setPrefHeight(30);
        pane.add(usernameInput, 1, 0);

        final TextField passwordInput = new TextField();
        passwordInput.setPrefHeight(30);
        pane.add(passwordInput, 1, 1);

        final TextField confirmPasswordInput = new TextField();
        confirmPasswordInput.setPrefHeight(30);
        pane.add(confirmPasswordInput, 1, 2);

        //This button does nothing right now. Will eventually create a User from the inputted data.
        Button register = new Button();
        register.setText("Register");
        register.setOnAction(event -> System.out.println("The \"Register\" button has been pressed."));
        register.setPrefWidth(150);
        register.setPrefHeight(50);
        register.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-font-size: 1.5em;");

        //This button closes the Registration window
        Button cancel = new Button();
        cancel.setText("Cancel");
        cancel.setOnAction(event -> ((Node)(event.getSource())).getScene().getWindow().hide());
        cancel.setPrefWidth(150);
        cancel.setPrefHeight(50);
        cancel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-font-size: 1.5em;");

        //This HBox holds the buttons Register and Cancel
        HBox buttons = new HBox(20);
        buttons.getChildren().addAll(register, cancel);
        pane.add(buttons, 1, 3);

        pane.setStyle("-fx-background-color: #9a9a9a;");  //This changes the background color of the whole window.
        return new Scene(pane, 600, 350);
    }

    private static Scene loginScreen() {
        //This GridPane holds all text on the left, all input fields on the right, and the HBox that holds the buttons under the input fields.
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        pane.setVgap(50);

        //These are the texts in order from top to bottom
        Text username = new Text("Username");
        username.setStyle("-fx-font-size: 2em;");
        pane.add(username, 0, 0);

        Text password = new Text("Password");
        password.setStyle("-fx-font-size: 2em;");
        pane.add(password, 0, 1);

        //These are the input fields in order from top to bottom
        final TextField usernameInput = new TextField();
        usernameInput.setPrefHeight(30);
        pane.add(usernameInput, 1, 0);

        final TextField passwordInput = new TextField();
        passwordInput.setPrefHeight(30);
        pane.add(passwordInput, 1, 1);

        //This button does nothing right now. Will eventually connect the User to their account.
        Button login = new Button();
        login.setText("Login");
        login.setOnAction(event -> System.out.println("The \"Login\" button has been pressed."));
        login.setPrefWidth(150);
        login.setPrefHeight(50);
        login.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-font-size: 1.5em;");

        //This button closes the Login window
        Button cancel = new Button();
        cancel.setText("Cancel");
        cancel.setOnAction(event -> ((Node)(event.getSource())).getScene().getWindow().hide());
        cancel.setPrefWidth(150);
        cancel.setPrefHeight(50);
        cancel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-font-size: 1.5em;");

        //This HBox holds the buttons Login and Cancel
        HBox buttons = new HBox(20);
        buttons.getChildren().addAll(login, cancel);
        pane.add(buttons, 1, 2);

        pane.setStyle("-fx-background-color: #9a9a9a;");  //This changes the background color of the whole window.
        return new Scene(pane, 600, 300);
    }

    //This method creates the dropdown menus in the top right of most windows
    private static VBox dropDownMenus() {
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
        //The border color stuff at the end of the next line creates a box around the menu options, then colors the bottom line of that box black
        //The other lines are set to the background color, and this is the best way I've found to put a line under the menu options
        bar.setStyle("-fx-background-color: #9a9a9a;  -fx-font-size: 1.1em; -fx-font-weight: bold; -fx-border-color: #9a9a9a #9a9a9a black #9a9a9a; ");

        
        VBox menus = new VBox();
        menus.getChildren().addAll(bar);
        return menus;
    }


    //This method creates the logo from Shapes and a Text box.
    //Almost certainly acting as placeholder art and should be replaced eventually.
    private static Node logo() {
        Rectangle outline = new Rectangle(0, 0, 600, 300);
        outline.setFill(null);
        outline.setStroke(Color.BLACK);

        Rectangle logoBar = new Rectangle(2, 149, 598, 15);
        logoBar.setFill(Color.WHITE);
        logoBar.setStroke(Color.WHITE);

        Line lineOne = new Line(0, 0, 600, 300);
        Line lineTwo = new Line(600, 0, 0, 300);
        Shape crossedLines = Shape.union(lineOne, lineTwo);

        Text text = new Text("LOGO");

        StackPane pane = new StackPane();
        pane.getChildren().addAll(outline, crossedLines, logoBar, text);
        return pane;
    }

}
