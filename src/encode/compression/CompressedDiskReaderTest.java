package encode.compression;

import capture.multi.raw.RawFrame;
import encode.ToMov;
import load.DiskReader;

import java.io.File;
import java.io.IOException;

/**
 * Created by denislavrov on 11/7/14.
 */
public class CompressedDiskReaderTest {
    public static void main(String[] args) throws IOException {
        CompressedDiskReader loader = new CompressedDiskReader(new File("compressed.bin"));
        ToMov encode = new ToMov(loader, new File("test1.mp4"));
    }
}
