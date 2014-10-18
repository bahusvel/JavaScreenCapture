package load;

import datastructure.ExchangeQueue;
import streamapi.AbstractSource;
import streamapi.DataSource;
import streamapi.DataStorage;
import streamapi.DataType;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4Factory;

import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by denislavrov on 10/11/14.
 */
public class DiskReader<T extends DataType> extends AbstractSource<T> {
    private ObjectInputStream ois;
    private FrameReader frameReader;
    long stime = System.nanoTime();

    private class FrameReader extends Thread {
        FrameReader() {
            start();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            T frame = null;
            try {
                frame = (T) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            while (frame != null) {
                store.add(frame);
                try {
                    frame = (T) ois.readObject();
                } catch (EOFException e) {
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            shutdownNow();
        }
    }

    public DiskReader(File file) {
        try {

            ois = new ObjectInputStream(new LZ4BlockInputStream(
                    new FileInputStream(new RandomAccessFile(file,"r").getFD()),
                    LZ4Factory.unsafeInstance().fastDecompressor()
            ));

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