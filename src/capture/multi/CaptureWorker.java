package capture.multi;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by denislavrov on 10/2/14.
 */
public class CaptureWorker implements Runnable {
    Robot robot;
    Rectangle captureSize;
    ConcurrentLinkedQueue<BufferedImage> sharedStore;

    public CaptureWorker(Robot robot, Rectangle captureSize, ConcurrentLinkedQueue<BufferedImage> store) {
        this.robot = robot;
        this.captureSize = captureSize;
        sharedStore = store;
    }

    @Override
    public void run() {
        sharedStore.add(robot.createScreenCapture(captureSize));
    }
}
