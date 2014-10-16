package streamapi;

/**
 * Created by denislavrov on 10/16/14.
 */
public interface DataMutator<C extends DataType, P extends DataType> extends DataSink<C>, DataSource<P> {
}
