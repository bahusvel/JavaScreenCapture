package capture.research;

import sun.lwawt.macosx.LWCToolkit;

import java.awt.*;
import java.awt.peer.RobotPeer;
import java.util.ArrayList;

/**
 * Created by denislavrov on 10/2/14.
 */
public class NativeTest {
    public static void main(String[] args) throws AWTException {
        RobotPeer rp = new LWCToolkit().createRobot(new Robot(),GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice());
        Rectangle screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        ArrayList<int[]> screens = new ArrayList<>();
        long sTime = System.nanoTime();
        while (System.nanoTime() < sTime + 1000_000_000)
            screens.add(rp.getRGBPixels(screenSize));
        System.out.printf("Captured %d screnshots", screens.size());
    }



}
