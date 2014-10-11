package capture.multi.raw;

import interfaces.DataSource;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by denislavrov on 10/3/14.
 */
public class RawCaptureScheduler implements DataSource<RawFrame>{
    private int THREADS = 6;
    private int FPS_PER_THREAD = 6;
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(THREADS);
    private Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    private ConcurrentLinkedQueue<RawFrame> store = new ConcurrentLinkedQueue<>();
    private boolean producingData = true;
    final static long toNanos = 1000_000_000L;

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
                    new RawCaptureWorker(captureSize, store),
                    fractionToNanos(i,THREADS),
                    fractionToNanos(1,FPS_PER_THREAD),
                    TimeUnit.NANOSECONDS
            );
        }
    }

    public void shutdown(){
        producingData = false;
        scheduledThreadPool.shutdown();
        try {
            scheduledThreadPool.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdownNow() {
        producingData = false;
        scheduledThreadPool.shutdownNow();
    }

    public ConcurrentLinkedQueue<RawFrame> getStore(){
        return store;
    }

    public boolean producingData() {
        return producingData;
    }
}
