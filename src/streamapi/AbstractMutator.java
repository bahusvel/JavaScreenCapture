package streamapi;


import datastructure.ExchangeQueue;

/**
 * Created by denislavrov on 10/16/14.
 */
public abstract class AbstractMutator<C extends DataType, P extends DataType> implements DataMutator<C, P> {
    protected volatile boolean acceptingData = true;
    protected volatile boolean producingData = true;
    protected DataStorage<P> store = new ExchangeQueue<>();


    @Override
    public boolean acceptingData() {
        return acceptingData;
    }

    @Override
    public DataStorage<P> getStore() {
        return store;
    }

    @Override
    public boolean producingData() {
        return producingData;
    }

    @Override
    public void shutdownNow() {
        acceptingData = false;
        producingData = false;
    }
}
