package encode.compression;

import capture.multi.raw.RawFrame;
import datastructure.BlockingExchangeQueue;
import datastructure.ExchangeQueue;
import encode.compression.FrameCompressor.FramePacket;
import streamapi.AbstractSource;
import streamapi.DataSource;
import streamapi.DataStorage;
import streamapi.DataType;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4Factory;

import java.awt.*;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by denislavrov on 10/11/14.
 */
public class CompressedDiskReader extends AbstractSource<RawFrame> {
    private InputStream ois;
    private FrameReader frameReader;
    private FrameDecompressor decompressor;
    long stime = System.nanoTime();
    {
        store = new BlockingExchangeQueue<>(30);
    }


    private class FrameReader extends Thread {
        FrameReader() {
            start();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            FrameDecompressor.FramePacket frame = null;
            try {
                frame = decompressor.unpack();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (frame != null && frame.getResult() != -1) {
                RawFrame rawFrame = new RawFrame(frame.getData(), new Rectangle(1440,900), frame.getTimeStamp(), 10L);
                store.add(rawFrame);
                try {
                    frame = decompressor.unpack();
                } catch (EOFException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            shutdownNow();
        }
    }

    public CompressedDiskReader(File file) {
        try {

            ois = new LZ4BlockInputStream(
                    new FileInputStream(new RandomAccessFile(file, "r").getFD()),
                    LZ4Factory.unsafeInstance().fastDecompressor()
            );

            decompressor = new FrameDecompressor(ois, 1_296_000);

            /*
            ois = new ObjectInputStream(new InflaterInputStream(new FileInputStream(new RandomAccessFile(file,"r").getFD())));
            */
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something wrong with the file");
        }
        frameReader = new FrameReader();
    }

    private void closeStream() {
        try {
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            frameReader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.shutdown();
        closeStream();
    }

    public void shutdownNow() {
        super.shutdownNow();
        closeStream();
        System.out.println("Read file in: " + (System.nanoTime() - stime) / 1000_000L + "ms");
    }
}
