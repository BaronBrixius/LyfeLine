import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

	/* Credit & source: https://github.com/AlmasB/FXTutorials/blob/master/src/com/almasb/ios/IOSApp.java
	 * This is a custom made toggle switch that has been modified to our need in the purple group.
	 */
    public class ToggleSwitch extends Parent {

        private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

        private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
        private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));

        private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

        public BooleanProperty switchedOnProperty() {
            return switchedOn;
        }

        public ToggleSwitch() {
            Rectangle background = new Rectangle(100, 50);
            background.setArcWidth(50);
            background.setArcHeight(50);
            background.setFill(Color.WHITE);
            background.setStroke(Color.LIGHTGRAY);

            Circle trigger = new Circle(25);
            trigger.setCenterX(25);
            trigger.setCenterY(25);
            trigger.setFill(Color.WHITE);
            trigger.setStroke(Color.LIGHTGRAY);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(2);
            trigger.setEffect(shadow);

            translateAnimation.setNode(trigger);
            fillAnimation.setShape(background);

            getChildren().addAll(background, trigger);

            switchedOn.addListener((obs, oldState, newState) -> {
                boolean isOn = newState;
                
                translateAnimation.setToX(isOn ? 100 - 50 : 0);
                fillAnimation.setFromValue(isOn ? Color.WHITE : Color.LIGHTGREEN);
                fillAnimation.setToValue(isOn ? Color.LIGHTGREEN : Color.WHITE);
               
                trigger.setFill(isOn ? Color.WHITE : Color.DARKRED);

                animation.play();
            });

            setOnMouseClicked(event -> { // add functionality here
                switchedOn.set(!switchedOn.get());
            });
        
        }
        
        
    }
