package encode;

import capture.multi.raw.RawFrame;
import datastructure.ExchangeQueue;
import streamapi.*;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by denislavrov on 10/4/14.
 */
public class ToJPEG extends ServiceMutator<RawFrame, JPEGFrame>{
    private static final int THREADS = 3;
    private ThreadLocal<ImageWriter> localWriter = new ThreadLocal<ImageWriter>(){
        @Override
        protected ImageWriter initialValue() {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext())
                throw new IllegalStateException("No writers found");
            return writers.next();
        }
    };
    private static final float quality = 0.25f;
    private ImageWriteParam param;
    long sTime = System.nanoTime();

    {
        service = Executors.newFixedThreadPool(THREADS);
        param = localWriter.get().getDefaultWriteParam();
        // compress to a given quality
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);
    }

    private class ToJPEGFrame implements Runnable{
        RawFrame frame;

        ToJPEGFrame(RawFrame data){
            frame = data;
        }

        @Override
        public void run() {
            try(ByteArrayOutputStream baos = new ByteArrayOutputStream(); ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                localWriter.get().setOutput(ios);
                localWriter.get().write(null, new IIOImage(frame.bufferedImage(),null, null), param);
                store.add(new JPEGFrame(baos.toByteArray()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ToJPEG(DataSource<RawFrame> ds){
        DataMonitor<RawFrame> dm = new DataMonitor<>(ds, this);
    }

    @Override
    public void consume(RawFrame data) {
        service.submit(new ToJPEGFrame(data));
    }

    @Override
    public void shutdown() {
        super.shutdown();
        System.out.println("Encoding took: " + (System.nanoTime() - sTime) / 1000_000L + "ms");
    }

    public DataStorage<JPEGFrame> getStore(){
        return store;
    }
}
