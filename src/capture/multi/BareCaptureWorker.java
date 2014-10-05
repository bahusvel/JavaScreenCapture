package capture.multi;

import java.awt.*;
import java.awt.peer.RobotPeer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by denislavrov on 10/3/14.
 */
public class BareCaptureWorker implements Runnable {
    RobotPeer peer = RobotPeerFactory.getPeer();
    Rectangle captureSize;
    ConcurrentLinkedQueue<int[]> sharedStore;

    public BareCaptureWorker(Rectangle captureSize, ConcurrentLinkedQueue<int[]> store) {
        this.captureSize = captureSize;
        sharedStore = store;
    }

    @Override
    public void run() {
        Toolkit.getDefaultToolkit().sync();
        sharedStore.add(peer.getRGBPixels(captureSize));
    }
}
