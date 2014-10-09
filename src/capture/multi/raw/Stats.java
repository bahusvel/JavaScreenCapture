package capture.multi.raw;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by denislavrov on 10/10/14.
 */
public class Stats {
    private ConcurrentLinkedQueue<CaptureFrame> store;
    private long MaxClockDeviation;
    private long MinClockDeviation;
    private double AvgClockDeviation;
    private long MaxCaptureDuration;
    private long MinCaptureDuration;
    private double AvgCaptureDuration;
    private long FrameCount;
    private static long toMillis = 1000_000L;

    public Stats(ConcurrentLinkedQueue<CaptureFrame> store) {
        this.store = store;
        computeStats();
    }

    public void computeStats() {
        long[] times = store.stream().sorted((c1,c2) -> c1.getFrameTime() < c2.getFrameTime() ? -1 : 1).mapToLong(CaptureFrame::getStime).toArray();
        long[] durations = store.stream().sorted((c1, c2) -> c1.getFrameTime() < c2.getFrameTime() ? -1 : 1).mapToLong(CaptureFrame::getDuration).toArray();
        long[] devs = new long[times.length];
        for (int i = 0; i < times.length - 1; i++) {
            devs[i] = times[i + 1] - times[i];
        }

        //Arrays.stream(devs).forEach(l -> System.out.println(l / toMillis + "ms"));

        MaxClockDeviation = Arrays.stream(devs).max().getAsLong() / toMillis;
        MinClockDeviation = Arrays.stream(devs).min().getAsLong() / toMillis;
        AvgClockDeviation = Arrays.stream(devs).average().getAsDouble() / toMillis;
        MaxCaptureDuration = Arrays.stream(durations).max().getAsLong() / toMillis;
        MinCaptureDuration = Arrays.stream(durations).min().getAsLong() / toMillis;
        AvgCaptureDuration = Arrays.stream(durations).average().getAsDouble() / toMillis;
        FrameCount = store.size();
    }

    public void displayStats() {
        long[] times = store.stream().mapToLong(CaptureFrame::getStime).toArray();
        long[] durations = store.stream().mapToLong(CaptureFrame::getDuration).toArray();
        long[] devs = new long[times.length];
        for (int i = 0; i < times.length - 1; i++) {
            devs[i] = times[i + 1] - times[i];
        }

        System.out.println("Max Clock deviation: " + MaxClockDeviation + "ms");
        System.out.println("Min Clock deviation: " + MinClockDeviation + "ms");
        System.out.println("Avg Clock deviation: " + AvgClockDeviation + "ms");
        System.out.println("Max capture Duration: " + MaxCaptureDuration + "ms");
        System.out.println("Min capture Duration: " + MinCaptureDuration + "ms");
        System.out.println("Avg capture Duration: " + AvgCaptureDuration + "ms");
        System.out.println("Frames captured: " + FrameCount + "ms");
    }

}
