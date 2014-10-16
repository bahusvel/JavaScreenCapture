package encode;

import capture.multi.raw.RawFrame;
import streamapi.DataSink;
import streamapi.DataSource;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import save.disk.DataMonitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by denislavrov on 10/4/14.
 */
public class ToMov implements DataSink<RawFrame>{
    private static final int THREADS = 4;
    private SeekableByteChannel ch;
    private ArrayList<ByteBuffer> spsList;
    private ArrayList<ByteBuffer> ppsList;
    private final FramesMP4MuxerTrack outTrack; // ACCESS MUST BE BLOCKING
    private ThreadLocal<ByteBuffer> local_out = new ThreadLocal<ByteBuffer>(){
        // thread allocates its own buffer, so just leave this null
        @Override
        protected ByteBuffer initialValue() {
            return null;
        }
    };
    private ThreadLocal<H264Encoder> localEncoder = new ThreadLocal<H264Encoder>(){
        // allocate encoder per thread
        @Override
        public H264Encoder initialValue() {
            return new H264Encoder();
        }
    };
    private long frameNo;
    private MP4Muxer muxer;
    private boolean acceptingData = true;
    private ExecutorService service = Executors.newFixedThreadPool(THREADS);
    long sTime = System.nanoTime();

    private class EncodeTask implements Runnable{
        RawFrame data;
        long fn;

        public EncodeTask(RawFrame data, long frameNumber) {
            this.data = data;
            fn = frameNumber;
        }

        @Override
        public void run() {
            if (local_out.get() == null){
                local_out.set(ByteBuffer.allocate(data.getWidth() * data.getHeight() * 6));
            }

            Picture toEncode = data.pictureYUV420();

            // Encode image into H.264 frame, the result is stored in '_out' buffer
            local_out.get().clear();
            ByteBuffer result = localEncoder.get().encodeFrame( local_out.get(), toEncode); // not thread safe

            // Based on the frame above form correct MP4 packet
            if (spsList == null){
                // Allocate sps and pps lists, if run for the first time
                spsList = new ArrayList<>();
                ppsList = new ArrayList<>();
                synchronized (spsList){
                    // only first encode is synchronized
                    H264Utils.encodeMOVPacket(result, spsList, ppsList); // Parameters aren't thread safe
                }
            } else {
                // if running for the second time, just encode and give it new arrays which are just ignored
                H264Utils.encodeMOVPacket(result, new ArrayList<>(), new ArrayList<>());
            }


            // Add packet to video track
            MP4Packet packet = new MP4Packet(result, fn, 25, 1, fn, true, null, fn, 0);

            try {
                synchronized (outTrack) {
                    // Must synchronize on outTrack, can only be one instance, needs multi-threaded access
                    outTrack.addFrame(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public ToMov(DataSource<RawFrame> ds, File out){
        try {
            ch = NIOUtils.writableFileChannel(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Muxer that will store the encoded frames
        try {
            muxer = new MP4Muxer(ch, Brand.MP4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add video track to muxer
        outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, 25);

        DataMonitor<RawFrame> dm = new DataMonitor<>(ds, this);
    }

    @Override
    public void consume(RawFrame data) {
        service.submit(new EncodeTask(data, frameNo++));
    }

    @Override
    public boolean acceptingData() {
        return acceptingData;
    }


    @Override
    public void shutdown() {
        acceptingData = false;
        service.shutdown();
        try {
            service.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Push saved SPS/PPS to a special storage in MP4
        outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));

        // Write MP4 header and finalize recording
        try {
            muxer.writeHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NIOUtils.closeQuietly(ch);

        System.out.println("Saving took: " + (System.nanoTime()-sTime)/1000_000L + "ms");
    }

    @Override
    public void shutdownNow() {
        acceptingData = false;
        service.shutdownNow();
        NIOUtils.closeQuietly(ch);
    }
}
