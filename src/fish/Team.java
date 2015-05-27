package fish;

import java.awt.Color;

/**
 * An enum for which team a player is on.
 */
public enum Team {
	RED, BLK;

	/**
	 * Returns a color to be used for this team in the GUI
	 * 
	 * @return A color object
	 */
	public Color getColor() {
		switch (this) {
		case RED:
			return Color.RED;
		case BLK:
			return Color.BLACK;
		default:
			throw new RuntimeException(
					"TEAM ENUM NOT MATCHING EITHER VALUE");
		}
	}
}
