package encode.diff;

import encode.JPEGFrame;

import java.awt.image.BufferedImage;

/**
 * Created by denislavrov on 11/4/14.
 */
public class ReferenceFrame extends DiffImage {
    private JPEGFrame frame;

    @Override
    public void destroy() {

    }

    @Override
    public BufferedImage getImage() {
        return frame.getImage();
    }
}
