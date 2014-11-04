package encode.diff;

import encode.JPEGFrame;

import java.awt.image.BufferedImage;

/**
 * Created by denislavrov on 11/4/14.
 */
public class DifferenceFrame extends DiffImage {
    private JPEGFrame forward;
    private JPEGFrame back;

    @Override
    public void destroy() {

    }

    @Override
    public BufferedImage getImage() {
        return null;
    }
}
