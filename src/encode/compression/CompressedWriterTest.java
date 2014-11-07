package encode.compression;

import capture.multi.raw.RawCaptureScheduler;

import java.io.File;

/**
 * Created by denislavrov on 11/7/14.
 */
public class CompressedWriterTest {
    public static void main(String[] args) throws Exception {
        RawCaptureScheduler cs = new RawCaptureScheduler(4,30);
        CompressedDiskWriter dsk = new CompressedDiskWriter(cs, new File("compressed.bin"));
        System.out.println("Capture Started");
        cs.init();
        Thread.sleep(40_000);
        cs.shutdown();
        System.out.println("Finished Capture");
    }
}
