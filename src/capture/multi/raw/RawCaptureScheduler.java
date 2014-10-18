package capture.multi.raw;

import datastructure.ExchangeQueue;
import streamapi.AbstractSource;
import streamapi.DataSource;
import streamapi.DataStorage;

import java.awt.*;
import java.awt.peer.RobotPeer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by denislavrov on 10/3/14.
 */
public class RawCaptureScheduler extends AbstractSource<RawFrame>{
    private int THREADS = 6;
    private int FPS_PER_THREAD = 6;
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(THREADS);
    private Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    private ThreadLocal<RobotPeer> localPeer = new ThreadLocal<RobotPeer>(){
        @Override
        protected RobotPeer initialValue() {
            return RobotPeerFactory.getPeer();
        }
    };
    private final static long toNanos = 1000_000_000L;

    public class RawCaptureWorker implements Runnable{
        @Override
        public void run() {
            final long stime = System.nanoTime();
            Toolkit.getDefaultToolkit().sync();
            final int[] pixels = localPeer.get().getRGBPixels(captureSize);
            final long duration = System.nanoTime() - stime;
            store.add(new RawFrame(pixels, captureSize, stime, duration));
        }
    }

    public RawCaptureScheduler(int THREADS, Rectangle captureSize) {
        this.THREADS = THREADS;
        this.captureSize = captureSize;
    }

    public RawCaptureScheduler(int THREADS, int FPS) {
        this.THREADS = THREADS;
        FPS_PER_THREAD = (int)((double) FPS / (double) THREADS);
    }

    public RawCaptureScheduler() {
    }

    private static long fractionToNanos(long a, long b){
        return a * toNanos / b;
    }

    public void init() throws Exception {
        for (int i = 0; i < THREADS; i++) {
            scheduledThreadPool.scheduleAtFixedRate(
                    new RawCaptureWorker(),
                    fractionToNanos(i,THREADS),
                    fractionToNanos(1,FPS_PER_THREAD),
                    TimeUnit.NANOSECONDS
            );
        }
    }

    public void shutdown(){
        scheduledThreadPool.shutdown();
        try {
            scheduledThreadPool.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.shutdown();
    }

    @Override
    public void shutdownNow() {
        super.shutdownNow();
        scheduledThreadPool.shutdownNow();
    }
}
