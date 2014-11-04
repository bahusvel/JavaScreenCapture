package encode.diff;

import capture.multi.raw.RawFrame;
import streamapi.ServiceMutator;

/**
 * Created by denislavrov on 11/4/14.
 */
public class ToDiff extends ServiceMutator<RawFrame, DiffImage> {
    @Override
    public void consume(RawFrame data) {

    }

    @Override
    public boolean wantsData() {
        return false;
    }
}
