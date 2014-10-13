package load;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.FastInput;
import interfaces.DataSource;
import interfaces.DataType;

import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.GZIPInputStream;

/**
 * Created by denislavrov on 10/11/14.
 */
public class KryoDiskReader<T extends DataType> implements DataSource<T> {
    protected ConcurrentLinkedQueue<T> store = new ConcurrentLinkedQueue<>();
    protected FastInput input;
    protected Kryo kryo = new Kryo();
    protected Thread frameReader;
    protected boolean producingData = true;

    protected class FrameReader extends Thread{
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

    public KryoDiskReader(File file){
        try {
            input = new FastInput(new GZIPInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something wrong with the file");
        }
        frameReader = getFrameReader();
    }

    protected Thread getFrameReader(){
        return new FrameReader();
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
