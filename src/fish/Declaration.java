package fish;

import java.util.Arrays;

/**
 * A data object representing a player's declaration
 */
public final class Declaration {
	public final int source;
	public final int suit;
	public int[] locs;

	public Declaration(int source, int suit) {
		this.source = source;
		this.suit = suit;
	}
	
	public Declaration(int source, int suit, int[] locs) {
		this(source, suit);
		updateLocs(locs);
	}

	public void updateLocs(int[] locs) {
		this.locs = locs;
	}

	@Override
	public String toString() {
		return "{source: " + source + ",suit: " + suit + ", locs: "
				+ Arrays.toString(locs) + ",}";
	}
}
