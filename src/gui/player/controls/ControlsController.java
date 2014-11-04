package gui.player.controls;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by denislavrov on 11/3/14.
 */
public class ControlsController implements Initializable {

    protected SimpleBooleanProperty playing = new SimpleBooleanProperty(true);

    @FXML
    void back(ActionEvent event) {
        System.out.println("back fired");
    }

    @FXML
    void playPause(ActionEvent event) {
        if(playing.get()) playing.set(false);
        else playing.set(true);
        System.out.println("play fired");
    }

    @FXML
    void forward(ActionEvent event) {
        System.out.println("forward fired");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
