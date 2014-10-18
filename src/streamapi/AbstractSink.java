package streamapi;

/**
 * Created by denislavrov on 10/16/14.
 */
public abstract class AbstractSink<T extends DataType> implements DataSink<T> {
    protected volatile boolean acceptingData = true;

    @Override
    public boolean acceptingData() {
        return acceptingData;
    }

    @Override
    public void shutdown() {
        acceptingData = false;
    }

    @Override
    public void shutdownNow() {
        acceptingData = false;
    }
}
