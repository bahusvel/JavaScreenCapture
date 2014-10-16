package load;

import datastructure.ExchangeQueue;
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
public class DiskReader<T extends DataType> implements DataSource<T> {
    private DataStorage<T> store = new ExchangeQueue<>();
    private ObjectInputStream ois;
    private FrameReader frameReader;
    private boolean producingData = true;

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

    @Override
    public DataStorage<T> getStore() {
        return store;
    }

    @Override
    public boolean producingData() {
        return producingData;
    }

    private void closeStream() {
        try {
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        producingData = false;
        try {
            frameReader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeStream();
    }

    public void shutdownNow() {
        producingData = false;
        closeStream();
    }
}