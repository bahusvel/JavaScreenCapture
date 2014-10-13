package load;

import interfaces.DataSource;
import interfaces.DataType;

import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.GZIPInputStream;

/**
 * Created by denislavrov on 10/11/14.
 */
public class NativeDiskReader<T extends DataType> implements DataSource<T> {
    private ConcurrentLinkedQueue<T> store = new ConcurrentLinkedQueue<>();
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

    public NativeDiskReader(File file) {
        try {
            ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something wrong with the file");
        }
        frameReader = new FrameReader();
    }

    @Override
    public ConcurrentLinkedQueue<T> getStore() {
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