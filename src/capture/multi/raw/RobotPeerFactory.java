package capture.multi.raw;

import sun.lwawt.macosx.LWCToolkit;

import java.awt.*;
import java.awt.peer.RobotPeer;

/**
 * Created by denislavrov on 10/3/14.
 */
public class RobotPeerFactory {
    public static RobotPeer getPeer(){
        try {
            return new LWCToolkit().createRobot(new Robot(),GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice());
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return null;
    }

}
