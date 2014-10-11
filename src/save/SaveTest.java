package save;

import capture.multi.raw.RawCaptureScheduler;
import capture.multi.raw.RawFrame;
import save.disk.DiskWriterService;

import java.io.File;

/**
 * Created by denislavrov on 10/11/14.
 */
public class SaveTest {
    public static void main(String[] args) throws Exception {
        RawCaptureScheduler cs = new RawCaptureScheduler(4,30);
        DiskWriterService<RawFrame> dsk = new DiskWriterService<>(cs, new File("save.bin"));
        System.out.println("Capture Started");
        cs.init();
        Thread.sleep(5000);
        cs.shutdown();
        System.out.println("Finished Capture");
    }
}
