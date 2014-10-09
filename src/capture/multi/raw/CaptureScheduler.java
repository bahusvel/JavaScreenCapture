package capture.multi.raw;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Created by denislavrov on 10/10/14.
 */
public interface CaptureScheduler {
    public void init() throws Exception;
    public void stop();
    public ConcurrentLinkedQueue<CaptureFrame> getStore();
}
