package load;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import interfaces.DataSource;
import interfaces.DataType;

import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.GZIPInputStream;

/**
 * Created by denislavrov on 10/11/14.
 */
public class DiskReaderService<T extends DataType> implements DataSource<T> {
    private ConcurrentLinkedQueue<T> store = new ConcurrentLinkedQueue<>();
    private Input input;
    private Kryo kryo = new Kryo();
    private FrameReader frameReader;
    private boolean producingData = true;

    private class FrameReader extends Thread{
        FrameReader(){
            start();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            T frame = (T) kryo.readClassAndObject(input);
            while (frame != null){
                store.add(frame);
                try {
                    frame = (T) kryo.readClassAndObject(input);
                } catch (Exception e){
                    System.err.println("Kryo tried to die");
                }
            }
            shutdownNow();
        }
    }

    public DiskReaderService(File file){
        try {
            input = new Input(new GZIPInputStream(new FileInputStream(file)));
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

    public void shutdown(){
        producingData = false;
        try {
            frameReader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        input.close();
    }

    public void shutdownNow(){
        producingData = false;
        input.close();
    }
}
