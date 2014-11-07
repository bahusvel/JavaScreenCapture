package encode.compression;


import capture.multi.raw.RawFrame;
import streamapi.*;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Factory;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * Created by denislavrov on 10/11/14.
 */
public class CompressedDiskWriter extends ServiceSink<RawFrame> {
    {
        service = Executors.newSingleThreadExecutor(); // Single thread for now may expand later
    }
    private OutputStream oos;
    private FrameCompressor compressor;

    private class DiskFrame implements Runnable {
        private RawFrame frame;

        public DiskFrame(RawFrame frame) {
            this.frame = frame;
        }

        @Override
        public void run() {
            try {
                compressor.pack(frame.raw(), frame.getFrameTime(), false);
                //oos.writeObject(frame);

            } catch (IOException e) {
                e.printStackTrace();
            }
            frame.destroy();
            frame = null;
        }
    }

    public CompressedDiskWriter(DataSource<RawFrame> ds, File file) {
        try {

            oos = new LZ4BlockOutputStream(
                    new FileOutputStream(new RandomAccessFile(file,"rw").getFD()),
                    512_000, // May need a little tuning, but increases compression
                    LZ4Factory.unsafeInstance().fastCompressor()
            );

            compressor = new FrameCompressor(oos, 1_296_000);

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
        super.shutdown();
        try {
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdownNow(){
        super.shutdownNow();
        try {
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void consume(RawFrame data) {
        service.submit(new DiskFrame(data));
    }
}
