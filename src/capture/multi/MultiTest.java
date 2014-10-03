package capture.multi;

import save.SequenceEncoder;

import java.awt.*;
import java.io.File;

/**
 * Created by denislavrov on 10/2/14.
 */
public class MultiTest {
    public static void main(String[] args) throws Exception {
        CaptureScheduler cs = new CaptureScheduler(6,30);
        System.out.println("Capture Started");
        cs.init();
        Thread.sleep(5000);
        cs.stop();
        System.out.println("Finished Capture");

        SequenceEncoder se = new SequenceEncoder(new File("test.mp4"), new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        cs.getStore().forEach(bi ->{
            try{
                se.encodeNativeFrame(bi);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        se.finish();

        System.out.println("FPS " + cs.getStore().size());
    }
}
