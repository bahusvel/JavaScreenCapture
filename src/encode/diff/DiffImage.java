package encode.diff;

import streamapi.DataType;

import java.awt.image.BufferedImage;

/**
 * Created by denislavrov on 11/4/14.
 */
public abstract class DiffImage implements DataType {
    public abstract BufferedImage getImage();
}
