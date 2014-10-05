package capture.multi;

import save.SequenceEncoder;

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
        cs.stop();
        System.out.println("Finished Capture");

        SequenceEncoder se = new SequenceEncoder(new File("test.mp4"), new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        long sTime = System.nanoTime();
        cs.getStore().forEach(bi ->{
            try{
                se.encodeNativeFrame(bi);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        System.out.println("Saving took: " + (System.nanoTime()-sTime)/1000_000L + "ms");
        se.finish();

        System.out.println("FPS " + cs.getStore().size());
    }
}
