package load;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.FastInput;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4Factory;
import streamapi.AbstractSource;
import streamapi.DataType;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by denislavrov on 10/11/14.
 */
public class KryoDiskReader<T extends DataType> extends AbstractSource<T>{
    protected FastInput input;
    protected Kryo kryo = new Kryo();
    protected Thread frameReader;

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
            input = new FastInput(new LZ4BlockInputStream(
                    new FileInputStream(new RandomAccessFile(file,"r").getFD()),
                    LZ4Factory.unsafeInstance().fastDecompressor()
            ));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something wrong with the file");
        }
        frameReader = new FrameReader();
    }


    public void shutdown(){
        super.shutdown();
        try {
            frameReader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        input.close();
    }

    public void shutdownNow(){
        super.shutdownNow();
        input.close();
    }
}
