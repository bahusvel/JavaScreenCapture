package capture.research;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.awt.*;

/**
 * Created by denislavrov on 10/2/14.
 */

@State(Scope.Benchmark)
public class RobotTest {

    Robot robot;
    Rectangle screenSize;

    @Setup
    public void init(){
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    }

    @Benchmark
    public void measureRobot() {
        robot.createScreenCapture(screenSize);
    }

    /*
    public static void main(String[] args) throws Exception{
        Robot robot = new Robot();
        Rectangle screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        ArrayList<BufferedImage> screens = new ArrayList<>();
        long sTime = System.nanoTime();
        int count = 0;
        while (System.nanoTime() < sTime + 1000_000_000) {
            //screens.add(robot.createScreenCapture(screenSize));
            robot.createScreenCapture(screenSize);
            count++;
        }
        System.out.printf("Captured %d screnshots", count);
        screens.forEach(s -> {
            try {
                ImageIO.write(s, "JPG", new File(System.nanoTime() + ".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
*/
}
