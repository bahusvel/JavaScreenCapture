package streamapi;

/**
 * Created by denislavrov on 10/11/14.
 */
public interface DataSink<T extends DataType> {
    public void consume(T data);
    public boolean acceptingData();
    public void shutdown();
    public void shutdownNow();
}
