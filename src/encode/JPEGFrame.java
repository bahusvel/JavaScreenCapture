package encode;

import streamapi.DataType;

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

    @Override
    public void destroy() {

    }
}
