package capture.multi.raw;
import interfaces.DataSource;



/**
 * Created by denislavrov on 10/10/14.
 */
public interface CaptureScheduler extends DataSource<CaptureFrame> {
    public void init() throws Exception;
    public void shutdown();
}
