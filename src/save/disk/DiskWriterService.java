package save.disk;

import capture.multi.raw.CaptureFrame;
import interfaces.DataSink;
import interfaces.DataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

/**
 * Created by denislavrov on 10/11/14.
 */
public class DiskWriterService implements DataSink<CaptureFrame> {
    private ExecutorService service = Executors.newSingleThreadExecutor(); // Single thread for now may expand later
    private ObjectOutputStream oos;
    private DataSource<CaptureFrame> ds;
    private boolean acceptingData = true;

    private class DiskFrame implements Runnable {
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

    public DiskWriterService(DataSource<CaptureFrame> ds, File file) {
        this.ds = ds;
        try {
            oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something wrong with the file");
        }
        new DataMonitor<>(ds,this);
    }

    public void shutdown(){
        acceptingData = false;
        service.shutdown();
        try {
            service.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdownNow(){
        acceptingData = false;
        service.shutdownNow();
        try {
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void consume(CaptureFrame data) {
        service.submit(new DiskFrame(data, oos));
    }

    @Override
    public boolean acceptingData() {
        return acceptingData;
    }
}
