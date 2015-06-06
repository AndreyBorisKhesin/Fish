package fish.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class UIUtil {
	public static final Font MENU_FONT = loadFont("resources/gecko.ttf").deriveFont(200f);
	public static final Font MENU_OPTION_FONT = MENU_FONT.deriveFont(75f);
	
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
}
