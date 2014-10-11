package capture.multi.raw;

import interfaces.DataType;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import sun.awt.image.SunWritableRaster;

import java.awt.*;
import java.awt.image.*;
import java.io.Serializable;

import static org.jcodec.common.model.ColorSpace.RGB;

/**
 * Created by denislavrov on 10/10/14.
 */
public class RawFrame implements DataType {
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

    public RawFrame(int[] data, Rectangle dim, long stime, long duration) {
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
                dstData[off++] = rgb1 >> 16 & 0xff; //R
                dstData[off++] = rgb1 >> 8 & 0xff; //G
                dstData[off++] = rgb1 & 0xff; //B
            }
        }
        return dst;
    }

    public Picture pictureYUV420(){
        int width = dim.width;
        int height = dim.height;

        Picture dst = Picture.create(width, height, ColorSpace.YUV420);
        int[][] dstData = dst.getData();

        int R, G, B, Y, U, V;
        int index = 0;
        int uIndex = 0;
        int[] ya = dstData[0];
        int[] ua = dstData[1];
        int[] va = dstData[2];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int rgb1 = data[index];
                R = rgb1 >> 16 & 0xff;
                G = rgb1 >> 8 & 0xff;
                B = rgb1 & 0xff;
                Y = (66  * R + 129 * G +  25 * B + 128 >> 8) + 16;
                ya[index] = Y & 255;

                if ((j & 1) == 0 && (i & 1) == 0)
                {
                    U = (-38 * R -  74 * G + 112 * B + 128 >> 8) + 128;
                    V = (112 * R -  94 * G -  18 * B + 128 >> 8) + 128;
                    ua[uIndex] = U & 255;
                    va[uIndex++] = V & 255;
                }

                index ++;
            }
        }
        return dst;
    }

    public static void main(String[] args) {
        Picture dst = Picture.create(1440, 900, ColorSpace.YUV420);
        int[] dstData = dst.getPlaneData(1);
        System.out.println(dstData.length);
    }

}
