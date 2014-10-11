package capture.multi.raw;

import java.awt.*;
import java.awt.peer.RobotPeer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by denislavrov on 10/3/14.
 */
public class RawCaptureWorker implements Runnable, CaptureWorker{
    RobotPeer peer = RobotPeerFactory.getPeer();
    Rectangle captureSize;
    ConcurrentLinkedQueue<RawFrame> sharedStore;

    public RawCaptureWorker(Rectangle captureSize, ConcurrentLinkedQueue<RawFrame> store) {
        this.captureSize = captureSize;
        sharedStore = store;
    }

    @Override
    public void run() {
        final long stime = System.nanoTime();
        Toolkit.getDefaultToolkit().sync();
        final int[] pixels = peer.getRGBPixels(captureSize);
        final long duration = System.nanoTime() - stime;
        sharedStore.add(new RawFrame(pixels, captureSize, stime, duration));
    }
}
