package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private StackPane pie;

    private final Timeline timeline = new Timeline();

    @FXML
    void showPie(MouseEvent event) {
        timeline.setRate(1.0);
        timeline.play();
    }

    @FXML
    void hidePie(MouseEvent event) {
        timeline.setRate(-1.0);
        timeline.play();
    }

    @FXML
    void initialize() {
        assert pie != null : "fx:id=\"pie\" was not injected: check your FXML file 'RecordControl.fxml'.";
        final KeyValue kvx0 = new KeyValue(pie.scaleXProperty(), 1.0);
        final KeyValue kvy0 = new KeyValue(pie.scaleYProperty(), 1.0);
        final KeyFrame kf0 = new KeyFrame(Duration.millis(0), kvx0, kvy0);
        final KeyValue kvx1 = new KeyValue(pie.scaleXProperty(), 1.8);
        final KeyValue kvy1 = new KeyValue(pie.scaleYProperty(), 1.8);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(200), kvx1, kvy1);
        timeline.getKeyFrames().addAll(kf0, kf1);
    }
}
