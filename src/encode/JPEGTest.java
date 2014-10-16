package encode;

import capture.multi.raw.RawFrame;
import load.DiskReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by denislavrov on 10/16/14.
 */
public class JPEGTest {
    public static void main(String[] args) throws IOException {
        DiskReader<RawFrame> dr = new DiskReader<>(new File("save.bin"));
        ToJPEG jpeg = new ToJPEG(dr);

        while (jpeg.producingData());

        int counter = 0;
        for (JPEGFrame pic : jpeg.getStore()){
            try (FileOutputStream fos = new FileOutputStream("pics/" + counter++ + ".jpg")){
                fos.write(pic.getData());
            }
        }

    }

}
