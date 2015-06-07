package fish.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UIUtil {
	/**
	 * Loads a font, given a location in String format.
	 * 
	 * @param ref The location in the hard drive in String format.
	 * @return The font at that location.
	 */
	public static Font loadFont(String ref) {
		Font temp = null;
		try {
			temp = Font.createFont(
					java.awt.Font.TRUETYPE_FONT,
					UIUtil.class.getClassLoader()
							.getResourceAsStream(
									ref));
		} catch (Exception e) {
			try {
				temp = java.awt.Font.createFont(
						java.awt.Font.TRUETYPE_FONT,
						new File(ref));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (FontFormatException e1) {
				e1.printStackTrace();
			}
		}
		return temp;
	}

	/**
	 * Loads an image given a location for it
	 * 
	 * @param ref The filepath to the image
	 * @return The image loaded
	 */
	public static BufferedImage loadImage(String ref) {
		BufferedImage temp = null;
		try {
			temp = ImageIO.read(UIUtil.class.getClassLoader()
					.getResourceAsStream(ref));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
}
