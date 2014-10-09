package capture.multi.raw;

import org.jcodec.common.model.Picture;
import sun.awt.image.SunWritableRaster;

import java.awt.*;
import java.awt.image.*;

import static org.jcodec.common.model.ColorSpace.RGB;

/**
 * Created by denislavrov on 10/10/14.
 */
public class CaptureFrame {
    private int[] data;
    private Rectangle dim;
    private long stime;
    private long duration;
    private static DirectColorModel screenCapCM = new DirectColorModel(24,
                                               /* red mask */    0x00FF0000,
                                               /* green mask */  0x0000FF00,
                                               /* blue mask */   0x000000FF);

    public int getWidth(){
        return dim.width;
    }

    public int getHeight(){
        return dim.height;
    }

    public long getStime() {
        return stime;
    }

    public long getDuration() {
        return duration;
    }

    public long getFrameTime(){
        return stime + duration / 2;
    }

    public CaptureFrame(int[] data, Rectangle dim, long stime, long duration) {
        this.data = data;
        this.dim = dim;
        this.stime = stime;
        this.duration = duration;
    }

    public int[] raw(){
        return data;
    }

    public BufferedImage bufferedImage(){
        BufferedImage image;
        DataBufferInt buffer;
        WritableRaster raster;

        int[] bandmasks = new int[3];

        buffer = new DataBufferInt(data, data.length);

        bandmasks[0] = screenCapCM.getRedMask();
        bandmasks[1] = screenCapCM.getGreenMask();
        bandmasks[2] = screenCapCM.getBlueMask();

        raster = Raster.createPackedRaster(buffer, dim.width, dim.height, dim.width, bandmasks, null);
        SunWritableRaster.makeTrackable(buffer);

        image = new BufferedImage(screenCapCM, raster, false, null);

        return image;
    }

    public Picture pictureRGB(){
        int width = dim.width;
        int height = dim.height;
        Picture dst = Picture.create(width, height, RGB);
        int[] dstData = dst.getPlaneData(0);
        int off = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb1 = data[i * width + j];
                dstData[off++] = rgb1 >> 16 & 0xff;
                dstData[off++] = rgb1 >> 8 & 0xff;
                dstData[off++] = rgb1 & 0xff;
            }
        }
        return dst;
    }

}
