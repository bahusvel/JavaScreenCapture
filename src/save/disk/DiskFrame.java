package save.disk;


import capture.multi.raw.CaptureFrame;
import interfaces.DataType;
import java.io.IOException;
import java.io.ObjectOutputStream;


/**
 * Created by denislavrov on 10/11/14.
 */
public class DiskFrame implements Runnable, DataType {
    private CaptureFrame frame;
    private ObjectOutputStream out;

    public DiskFrame(CaptureFrame frame, ObjectOutputStream out) {
        this.frame = frame;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            out.writeObject(frame);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
