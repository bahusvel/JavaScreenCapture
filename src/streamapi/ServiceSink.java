package streamapi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by denislavrov on 10/16/14.
 */
public abstract class ServiceSink<T extends DataType> extends AbstractSink<T> {
    protected ExecutorService service;

    @Override
    public void shutdown() {
        super.shutdown();
        service.shutdown();
        try {
            service.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdownNow() {
        super.shutdownNow();
        service.shutdownNow();
    }
}
