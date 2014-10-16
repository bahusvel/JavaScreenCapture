package datastructure;

import streamapi.DataStorage;
import streamapi.DataType;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by denislavrov on 10/16/14.
 */
public class ExchangeQueue<T extends DataType> extends ConcurrentLinkedQueue<T> implements DataStorage<T> {
}
