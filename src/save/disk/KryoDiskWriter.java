package save.disk;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.FastOutput;
import streamapi.DataSink;
import streamapi.DataSource;
import streamapi.DataType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

/**
 * Created by denislavrov on 10/11/14.
 */
public class KryoDiskWriter<T extends DataType> implements DataSink<T> {
    protected ExecutorService service = Executors.newSingleThreadExecutor(); // Single thread for now may expand later
    protected FastOutput output;
    protected Kryo kryo = new Kryo(); // TODO Use thread local here
    protected boolean acceptingData = true;

    private class DiskFrame implements Runnable {
        private DataType frame;

        public DiskFrame(DataType frame) {
            this.frame = frame;
        }

        @Override
        public void run() {
            kryo.writeClassAndObject(output, frame);
        }
    }

    public KryoDiskWriter(DataSource<T> ds, File file) {
        try {
            output = new FastOutput(new GZIPOutputStream(new FileOutputStream(file)));
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
        output.close();
    }

    public void shutdownNow(){
        acceptingData = false;
        service.shutdownNow();
        output.close();
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
