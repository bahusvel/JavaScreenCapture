package capture.research;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by denislavrov on 10/2/14.
 */
public class CaptureEntity implements Runnable{
    Robot robot;
    Rectangle screenSize;
    AtomicInteger counter;
    boolean run = true;

    CaptureEntity(Robot robot, Rectangle screenSize, AtomicInteger counter){
        this.robot = robot;
        this.screenSize = screenSize;
        this.counter = counter;
    }

    public void stop(){
        run = false;
    }

    @Override
    public void run() {
        while (run) {
            robot.createScreenCapture(screenSize);
            counter.addAndGet(1);
        }
    }
}