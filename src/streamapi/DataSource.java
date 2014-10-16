package streamapi;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by denislavrov on 10/11/14.
 */
public interface DataSource<T extends DataType> {
    public DataStorage<T> getStore();
    public boolean producingData();
    public void shutdown();
    public void shutdownNow();
}
