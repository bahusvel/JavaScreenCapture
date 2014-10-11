package capture.multi;

import capture.multi.raw.BareCaptureScheduler;
import capture.multi.raw.Stats;
import save.MP4Encoder;

import java.awt.*;
import java.io.File;

/**
 * Created by denislavrov on 10/2/14.
 */
public class MultiTest {
    public static void main(String[] args) throws Exception {
        BareCaptureScheduler cs = new BareCaptureScheduler(6,30);
        System.out.println("Capture Started");
        cs.init();
        Thread.sleep(5000);
        cs.shutdown();
        while (!cs.isTerminated());
        System.out.println("Finished Capture");

        MP4Encoder se = new MP4Encoder(new File("test.mp4"), new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        long sTime = System.nanoTime();
        cs.getStore().stream().sorted((c1,c2) -> c1.getFrameTime() < c2.getFrameTime() ? -1 : 1).forEach(cf ->{
            try{
                se.encodeNativeFrame(cf);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        System.out.println("Saving took: " + (System.nanoTime()-sTime)/1000_000L + "ms");
        se.finish();
        new Stats(cs.getStore()).displayStats();
    }
}
