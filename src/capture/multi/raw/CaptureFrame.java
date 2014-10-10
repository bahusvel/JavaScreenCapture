package capture.multi.raw;

import org.jcodec.common.model.ColorSpace;
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
        int yIndex = 0;
        int uIndex = 0;

        Picture dst = Picture.create(width, height, ColorSpace.YUV420);
        int[][] dstData = dst.getData();


        int R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int rgb1 = data[j * width + i];
                R = rgb1 >> 16 & 0xff;
                G = rgb1 >> 8 & 0xff;
                B = rgb1 & 0xff;
                Y = (66  * R + 129 * G +  25 * B + 128 >> 8) +  16;
                U = (-38 * R -  74 * G + 112 * B + 128 >> 8) + 128;
                V = (112 * R -  94 * G -  18 * B + 128 >> 8) + 128;


                dstData[0][yIndex++] = Y < 0 ? 0 : Y > 255 ? 255 : Y;

                if (j % 2 == 0 && index % 2 == 0)
                {
                    dstData[1][uIndex] += U < 0 ? 0 : U > 255 ? 255 : U;
                    dstData[2][uIndex] += V < 0 ? 0 : V > 255 ? 255 : V;
                    uIndex++;
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
