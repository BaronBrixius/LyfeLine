import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;

/* Credit & source: https://github.com/AlmasB/FXTutorials/blob/master/src/com/almasb/ios/IOSApp.java
 * This is a custom made toggle switch that has been modified to our need in the purple group.
 */
public class ToggleSwitch extends Parent {

    public BooleanProperty switchedOn = new SimpleBooleanProperty(false);

    public ToggleSwitch() {
        Rectangle background = new Rectangle(100, 50);
        background.setArcWidth(50);
        background.setArcHeight(50);
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);

        Circle trigger = new Circle(25, 25, 25);
        trigger.setFill(Color.WHITE);
        trigger.setStroke(Color.LIGHTGRAY);
        trigger.setEffect(new DropShadow(2, Color.valueOf("0x000000ff")));

        TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
        translateAnimation.setNode(trigger);

        FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));
        fillAnimation.setShape(background);

        ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

        getChildren().addAll(background, trigger);

        switchedOn.addListener((obs, oldState, newState) -> {
            setDisable(true);

            translateAnimation.setToX(newState ? 100 - 50 : 0);
            fillAnimation.setFromValue(newState ? Color.WHITE : Color.LIGHTGREEN);
            fillAnimation.setToValue(newState ? Color.LIGHTGREEN : Color.WHITE);
            trigger.setFill(newState ? Color.DARKRED : Color.WHITE);

            animation.play();
            animation.setOnFinished(e -> setDisable(false));
        });

        setOnMouseClicked(event -> { // add functionality here
            switchedOn.setValue(!switchedOn.get());
        });

    }

}
