package capture.multi;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

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
        cs.getStore().forEach(im -> {
            try {
                ImageIO.write(im, "JPG", new File("pics/" + System.nanoTime() + ".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("FPS " + cs.getStore().size());
    }
}
