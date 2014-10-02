package capture.research;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class screen2image
{
	public static void main(String[] args) throws Exception
	{
		Robot robot = new Robot();

		BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		ImageIO.write(screenShot, "JPG", new File("screenShot.jpg"));
	}
}