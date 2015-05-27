package fish;

import java.util.HashSet;
import java.util.Set;

/**
 * A probabilistic Hand containing a distribution of Cards held by your
 * opponents based on the probabilities of them having those Cards.
 */
public class QuantumHand {

	/**
	 * Possible Cards that could be in a Player's Hand and their probabilities.
	 * Elements 0 to 7 are specific suits.
	 * Element 8 is the generic suit.
	 */
	private Set<QuantumCard>[] hand = new Set[9];

	private int[][] bounds = new int[2][9];

	/**
	 * Default QuantumHand constructor.
	 * It contains each Card with equal probability.
	 */
	public QuantumHand() {
		for (int i = 0; i < hand.length; i++) {
			hand[i] = new HashSet<>();
		}
		for (int i = 0; i < 48; i++) {
			hand[8].add(new QuantumCard(i, 1d / 6));
		}
		bounds[1][8] = 8;
	}

	public Set<QuantumCard>[] getHand() {
		return hand;
	}
}
