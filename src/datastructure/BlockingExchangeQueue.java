package datastructure;

import capture.multi.raw.RawFrame;
import encode.JPEGFrame;
import streamapi.DataStorage;
import streamapi.DataType;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by denislavrov on 10/20/14.
 */
public class BlockingExchangeQueue<T extends DataType> extends LinkedBlockingQueue<T> implements DataStorage<T>{
    public BlockingExchangeQueue(int capacity){
        super(capacity);
    }

    @Override
    public boolean add(T t) {
        try {
            put(t);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static void main(String[] args) {
        BlockingExchangeQueue<JPEGFrame> queue = new BlockingExchangeQueue<>(5);
        for (int i = 0; i < 10; i++) {
            System.out.println("Putting in: " + i);
            queue.add(new JPEGFrame(new byte[0]));
        }
    }
}

