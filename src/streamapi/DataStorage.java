package streamapi;

import java.util.Collection;
import java.util.Queue;

/**
 * Created by denislavrov on 10/16/14.
 */
public interface DataStorage<T extends DataType> extends Iterable<T>, Queue<T>, Collection<T> {
}
