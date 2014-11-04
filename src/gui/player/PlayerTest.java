package gui.player;

import capture.multi.raw.RawFrame;
import encode.ToMov;
import load.DiskReader;

import java.io.File;
import java.io.IOException;

/**
 * Created by denislavrov on 11/3/14.
 */
public class PlayerTest {
    public static void main(String[] args) throws IOException {
        DiskReader<RawFrame> loader = new DiskReader<>(new File("save.bin"));
        Player player = new Player(loader);
    }
}
