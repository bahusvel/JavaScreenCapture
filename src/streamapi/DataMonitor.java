package streamapi;

import streamapi.DataSink;
import streamapi.DataSource;
import streamapi.DataType;

/**
 * Created by denislavrov on 10/11/14.
 */
public class DataMonitor<T extends DataType> extends Thread{
    private DataSource<T> source;
    private DataSink<T> sink;

    public DataMonitor(DataSource<T> source, DataSink<T> sink) {
        this.source = source;
        this.sink = sink;
        start();
    }

    @Override
    public void run() {
        while (sink.acceptingData()){
            T data = source.getStore().poll();
            if (data != null) {
                while (!sink.wantsData()) {
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sink.consume(data);
            }
            else if (!source.producingData()) {
                sink.shutdown();
                break;
            }
            else {
                try {
                    Thread.sleep(1L); // prevent DataMonitor from polling too much
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
