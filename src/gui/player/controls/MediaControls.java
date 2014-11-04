package gui.player.controls;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Created by denislavrov on 11/3/14.
 */
public class MediaControls extends AnchorPane {
    private SimpleBooleanProperty playing = new SimpleBooleanProperty();

    private ControlsController controller;

    public MediaControls(){
        controller = load();
        playing = controller.playing;
    }

    public boolean getPlaying() {
        return playing.get();
    }

    public SimpleBooleanProperty playingProperty() {
        return playing;
    }

    private ControlsController load(){
        final FXMLLoader loader = new FXMLLoader();
        loader.setRoot(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.setLocation(getClass().getResource("Controls.fxml"));
        try {
            final Object root = loader.load();
            assert root == this;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loader.getController();
    }
}
