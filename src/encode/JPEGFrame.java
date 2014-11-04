package encode;

import streamapi.DataType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by denislavrov on 10/16/14.
 */
public class JPEGFrame implements DataType {
    private byte[] data;

    public JPEGFrame(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public BufferedImage getImage()
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage ret = null;
        try {
            ret = ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void destroy() {

    }
}
