package capture.research.fx;

import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotImage;
import com.sun.javafx.robot.impl.BaseFXRobot;
import com.sun.javafx.robot.impl.FXRobotHelper;
import com.sun.javafx.robot.impl.FXRobotHelper.FXRobotImageConvertor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * Created by denislavrov on 11/7/14.
 */
public class FXTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(new AnchorPane(), 700, 700));
        primaryStage.getScene().setFill(Color.TRANSPARENT);
        primaryStage.show();
        FXRobot robot = new BaseFXRobot(primaryStage.getScene());
        FXRobotHelper.setImageConvertor(new FXRobotImageConvertor() {
            @Override
            public FXRobotImage convertToFXRobotImage(Object platformImage) {
                return null;
            }
        });
        FXRobotImage image = robot.getSceneCapture(0, 0, 700, 700);
    }
}
