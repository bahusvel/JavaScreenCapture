package streamapi;

/**
 * Created by denislavrov on 11/5/14.
 */
public class LockingMonitor<T extends DataType> extends Thread {
    private DataSource<T> source;
    private DataSink<T> sink;

    public LockingMonitor(DataSource<T> source, DataSink<T> sink) {
        this.source = source;
        this.sink = sink;
        start();
    }

    @Override
    public void run() {
        while (sink.acceptingData()) {
            System.out.println("Monitoring");
            synchronized (source) {
                try {
                    source.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            T data = source.getData();

            if (data != null) {
                while (!sink.wantsData()) {
                    sleepUninterupted(1L);
                }
                sink.consume(data);
            } else if (!source.producingData()) {
                sink.shutdown();
                break;
            }
        }
    }

    private static void sleepUninterupted(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
