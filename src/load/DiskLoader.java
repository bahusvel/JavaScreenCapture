package load;

import capture.multi.raw.RawFrame;
import interfaces.DataSource;

import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.GZIPInputStream;

/**
 * Created by denislavrov on 10/11/14.
 */
public class DiskLoader implements DataSource<RawFrame> {
    private ConcurrentLinkedQueue<RawFrame> store = new ConcurrentLinkedQueue<>();
    private ObjectInputStream ois;
    private FrameLoader frameLoader;
    private boolean producingData = true;

    private class FrameLoader implements Runnable{
        private Thread thread;
        FrameLoader(){
            thread = new Thread(this);
            thread.start();
        }

        public void join(){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            RawFrame frame = null;
            try {
                frame = (RawFrame) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            while (frame != null){
                store.add(frame);
                try {
                    frame = (RawFrame) ois.readObject();
                } catch (EOFException e) {break;}
                catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            shutdownNow();
        }
    }

    public DiskLoader(File file){
        try {
            ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something wrong with the file");
        }
        frameLoader = new FrameLoader();
    }

    @Override
    public ConcurrentLinkedQueue<RawFrame> getStore() {
        return store;
    }

    @Override
    public boolean producingData() {
        return producingData;
    }

    private void closeStream(){
        try {
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        producingData = false;
        frameLoader.join();
        closeStream();
    }

    public void shutdownNow(){
        producingData = false;
        //frameLoader.shutdown();
        closeStream();
    }
}
