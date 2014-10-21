package load;

import capture.multi.raw.RawFrame;
import encode.ToMov;
import save.MP4Encoder;
import sun.jvm.hotspot.HelloWorld;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by denislavrov on 10/11/14.
 */
public class LoadTest {
    public static void main(String[] args) throws IOException {
        DiskReader<RawFrame> loader = new DiskReader<>(new File("save.bin"));
        ToMov encode = new ToMov(loader, new File("test1.mp4"));
    }
}
