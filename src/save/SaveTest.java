package save;

import capture.multi.raw.BareCaptureScheduler;
import save.disk.DiskWriterService;

import java.io.File;

/**
 * Created by denislavrov on 10/11/14.
 */
public class SaveTest {
    public static void main(String[] args) throws Exception {
        BareCaptureScheduler cs = new BareCaptureScheduler(4,30);
        DiskWriterService dsk = new DiskWriterService(cs, new File("save.bin"));
        System.out.println("Capture Started");
        cs.init();
        Thread.sleep(5000);
        cs.shutdown();
        while (!cs.isTerminated());
        System.out.println("Finished Capture");
    }
}
