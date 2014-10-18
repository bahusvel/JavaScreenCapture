package streamapi;

import datastructure.ExchangeQueue;


/**
 * Created by denislavrov on 10/16/14.
 */
public abstract class AbstractSource<T extends DataType> implements DataSource<T> {
    protected DataStorage<T> store = new ExchangeQueue<>();
    protected volatile boolean producingData = true;

    @Override
    public DataStorage<T> getStore() {
        return store;
    }

    @Override
    public boolean producingData() {
        return producingData;
    }

    @Override
    public void shutdown() {
        producingData = false;
    }

    @Override
    public void shutdownNow() {
        producingData = false;

    }
}
