package fish;

import java.awt.*;

/**
 * An enum for which team a player is on.
 */
public enum Team {
	RED(Color.RED),
	BLK(Color.BLACK);

	Color color;

	Team(Color color) {
		if (!color.equals(Color.RED) && !color.equals(Color.BLACK)) {
			throw new IllegalArgumentException();
		}
		this.color = color;
	}
}
