package capture.research;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by denislavrov on 10/2/14.
 */
public class ThreadedCapture {
    public static void main(String[] args) throws AWTException, InterruptedException {
        final int THREADS = 4;
        AtomicInteger counter = new AtomicInteger(0);
        Rectangle screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] =  new Thread(new CaptureEntity(new Robot(), screenSize, counter));
            threads[i].start();
        }
        Thread.sleep(1000);
        for (int i = 0; i < THREADS; i++) {
            threads[i].stop();
        }

        System.out.println(counter);
    }

}
