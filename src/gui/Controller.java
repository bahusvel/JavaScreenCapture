package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;


public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private StackPane pie;

    @FXML
    private StackPane rootStack;

    private final Timeline timeline = new Timeline();

    boolean left = true;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    void showPie(MouseEvent event) {
        if(left) {
            timeline.setRate(1.0);
            timeline.play();
            left = false;
        }
    }

    @FXML
    void leftPieControls(MouseEvent event) throws InterruptedException {
        if (!left) {
            timeline.setRate(-1.0);
            timeline.play();
            left = true;
        }
    }

    @FXML
    void mouseDragged(MouseEvent event){
        Main.primaryStage.setX(event.getScreenX() - xOffset);
        Main.primaryStage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    void mousePressed(MouseEvent event){
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    void initialize() {
        assert pie != null : "fx:id=\"pie\" was not injected: check your FXML file 'RecordControl.fxml'.";
        Circle clip1 = new Circle(100, Color.TRANSPARENT);
        pie.getChildren().add(clip1);
        rootStack.getChildren().forEach(node ->  {
            if (node instanceof ImageView){

            }
        });

        final KeyValue kvx0 = new KeyValue(pie.scaleXProperty(), 1.0);
        final KeyValue kvy0 = new KeyValue(pie.scaleYProperty(), 1.0);
        final KeyFrame kf0 = new KeyFrame(Duration.millis(0), kvx0, kvy0);
        final KeyValue kvx1 = new KeyValue(pie.scaleXProperty(), 1.8);
        final KeyValue kvy1 = new KeyValue(pie.scaleYProperty(), 1.8);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(200), kvx1, kvy1);
        timeline.getKeyFrames().addAll(kf0, kf1);
    }
}
