package save.disk;

import interfaces.DataSink;
import interfaces.DataSource;
import interfaces.DataType;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Factory;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by denislavrov on 10/11/14.
 */
public class DiskWriter<T extends DataType> implements DataSink<T> {
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

    public DiskWriter(DataSource<T> ds, File file) {
        try {

            oos = new ObjectOutputStream(new LZ4BlockOutputStream(
                    new FileOutputStream(new RandomAccessFile(file,"rw").getFD()),
                    512_000, // May need a little tuning, but increases compression
                    LZ4Factory.unsafeInstance().fastCompressor()
            ));

            /*
            oos = new ObjectOutputStream(new DeflaterOutputStream(new FileOutputStream(new RandomAccessFile(file,"rw").getFD())
            ,new Deflater(Deflater.BEST_SPEED))); // Similar in perfomance to LZ4 but trashes it in compression
            */

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
    public void consume(T data) {
        service.submit(new DiskFrame(data));
    }

    @Override
    public boolean acceptingData() {
        return acceptingData;
    }
}