package save.disk;

import interfaces.DataSink;
import interfaces.DataSource;
import interfaces.DataType;

/**
 * Created by denislavrov on 10/11/14.
 */
public class DataMonitor<T extends DataType> implements Runnable {
    private DataSource<T> source;
    private DataSink<T> sink;

    public DataMonitor(DataSource<T> source, DataSink<T> sink) {
        this.source = source;
        this.sink = sink;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (sink.acceptingData()){
            T data = source.getStore().poll();
            if (data != null)
                sink.consume(data);
            else if (!source.producingData()) {
                sink.shutdown();
                break;
            }
        }
    }
}
