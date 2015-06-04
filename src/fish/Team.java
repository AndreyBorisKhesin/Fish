package fish;

import java.awt.Color;

/**
 * An enum for which team a player is on.
 */
public enum Team {
	RED, BLU;

	/**
	 * Returns a color to be used for this team in the GUI
	 * 
	 * @return A color object
	 */
	public Color getColor() {
		switch (this) {
		case RED:
			return Color.RED;
		case BLU:
			return Color.BLUE;
		default:
			throw new RuntimeException(
					"TEAM ENUM NOT MATCHING EITHER VALUE");
		}
	}

	public Team other() {
		switch (this) {
		case RED:
			return BLU;
		case BLU:
			return RED;
		default:
			throw new RuntimeException(
					"TEAM ENUM NOT MATCHING EITHER VALUE");
		}
	}

	@Override
	public String toString() {
		switch (this) {
		case RED:
			return "RED";
		case BLU:
			return "BLU";
		default:
			throw new RuntimeException(
					"TEAM ENUM NOT MATCHING EITHER VALUE");
		}
	}

	public char delim() {
		switch (this) {
		case RED:
			return (char) 30;
		case BLU:
			return (char) 31;
		default:
			throw new RuntimeException(
					"TEAM ENUM NOT MATCHING EITHER VALUE");
		}
	}

	public String teamStr(String str) {
		char c = delim();
		return c + str + c;
	}
}
