package streamapi;

/**
 * Created by denislavrov on 11/7/14.
 */
public abstract class InlineMutator<C extends DataType, P extends DataType> implements DataMutator<C, P> {
    protected DataSource<C> source;

    protected InlineMutator(DataSource<C> source) {
        this.source = source;
    }

    @Override
    public void consume(C data) {}

    @Override
    public boolean wantsData() {
        return true;
    }

    @Override
    public boolean acceptingData() {
        return true;
    }

    @Override
    public boolean producingData() {
        return source.producingData();
    }

    @Override
    public void shutdown() {
        source.shutdown();
    }

    @Override
    public void shutdownNow() {
        source.shutdownNow();
    }
}
