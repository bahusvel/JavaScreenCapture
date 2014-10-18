package save.disk;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.FastOutput;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Factory;
import streamapi.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

/**
 * Created by denislavrov on 10/11/14.
 */
public class KryoDiskWriter<T extends DataType> extends ServiceSink<T> {
    {
        service = Executors.newSingleThreadExecutor(); // Single thread for now may expand later
    }
    protected FastOutput output;
    protected Kryo kryo = new Kryo(); // TODO Use thread local here

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
            output = new FastOutput(new LZ4BlockOutputStream(
                    new FileOutputStream(new RandomAccessFile(file,"rw").getFD()),
                    512_000, // May need a little tuning, but increases compression
                    LZ4Factory.unsafeInstance().fastCompressor()));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something wrong with the file");
        }
        new DataMonitor<>(ds,this);
    }

    public void shutdown(){
        super.shutdown();
        output.close();
    }

    public void shutdownNow(){
        super.shutdownNow();
        output.close();
    }

    @Override
    public void consume(T data) {
        service.submit(new DiskFrame(data));
    }
}
