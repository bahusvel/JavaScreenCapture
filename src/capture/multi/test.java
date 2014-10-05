package capture.multi;

import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.jcodec.common.model.ColorSpace.RGB;

/**
 * Created by denislavrov on 10/3/14.
 */
public class test {
    public static Picture fromBufferedImage(int[] rgbs, Rectangle size) {
        int width = (int)size.getWidth();
        int height = (int)size.getHeight();
        Picture dst = Picture.create(width, height, RGB);
        int[] dstData = dst.getPlaneData(0);
        int off = 0;
        for (int i = 0; i < size.getHeight(); i++) {
            for (int j = 0; j < size.getWidth(); j++) {
                int rgb1 = rgbs[i*width + j];
                dstData[off++] = (rgb1 >> 16) & 0xff;
                dstData[off++] = (rgb1 >> 8) & 0xff;
                dstData[off++] = rgb1 & 0xff;
            }
        }
        return dst;
    }

    public static void main(String[] args) throws IOException {
        BufferedImage bi = ImageIO.read(new File("JavaScreenCapture/pics/001.jpg"));

        int[][] picData = new int[1][];
        picData[0] = bi.getRaster().getPixels(0,0,1440,900, new int[1440*900*3]);
        Picture pic = new Picture(1440,900,picData, ColorSpace.YUV420);


        ImageIcon icon = new ImageIcon();
        icon.setImage(AWTUtil.toBufferedImage(pic));
        JOptionPane.showMessageDialog(null, icon);

    }
}
