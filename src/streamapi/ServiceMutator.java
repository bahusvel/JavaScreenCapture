package streamapi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by denislavrov on 10/16/14.
 */
public abstract class ServiceMutator<C extends DataType, P extends DataType> extends AbstractMutator<C, P> {
    protected ExecutorService service;

    @Override
    public void shutdown() {
        acceptingData = false;
        service.shutdown();
        try {
            service.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        producingData = false;
    }

    @Override
    public void shutdownNow() {
        super.shutdownNow();
        service.shutdownNow();
    }
}
