package save.disk;

import interfaces.DataSink;
import interfaces.DataSource;
import interfaces.DataType;

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
public class NativeDiskWriter<T extends DataType> implements DataSink<T> {
    private ExecutorService service = Executors.newSingleThreadExecutor(); // Single thread for now may expand later
    private ObjectOutputStream oos;
    private boolean acceptingData = true;

    private class DiskFrame implements Runnable {
        private DataType frame;

        public DiskFrame(DataType frame) {
            this.frame = frame;
        }

        @Override
        public void run() {
            try {
                oos.writeObject(frame);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public NativeDiskWriter(DataSource<T> ds, File file) {
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
    public void consume(DataType data) {
        service.submit(new DiskFrame(data));
    }

    @Override
    public boolean acceptingData() {
        return acceptingData;
    }
}