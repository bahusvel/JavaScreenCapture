package gui.player;

import gui.player.controls.MediaControls;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by denislavrov on 11/3/14.
 */
public class PlayerController implements Initializable {
    @FXML
    public AnchorPane rootPane;
    @FXML
    private ImageView viewPort;
    @FXML
    private MediaControls playerControls;
    protected Player player = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //viewPort.setImage(new Image("test.png"));
        rootPane.setBottomAnchor(playerControls, 30.0);
        playerControls.translateXProperty().bind(rootPane.widthProperty().divide(2).subtract(playerControls.widthProperty().divide(2)));

        playerControls.playingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) player.play();
            else player.pause();
        });
    }

    public void setFrame(Image image){
        try {
            viewPort.setImage(image);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }
}
