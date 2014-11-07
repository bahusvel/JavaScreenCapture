package capture.multi;

import capture.multi.raw.RawFrame;
import streamapi.DataSource;
import streamapi.InlineMutator;

/**
 * Created by denislavrov on 11/7/14.
 */
public class FrameOrderer extends InlineMutator<RawFrame, RawFrame> {



    protected FrameOrderer(DataSource<RawFrame> source) {
        super(source);
    }

    @Override
    public RawFrame getData() {
        return null;
    }
}
