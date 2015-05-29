package fish;

import java.util.HashMap;
import java.util.Map;

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
	private Map<Card, Double>[] hand = new Map[9];

	private int[][] bounds = new int[9][2];

	private boolean[] moved = new boolean[8];

	/**
	 * Default QuantumHand constructor.
	 * It contains each Card with equal probability.
	 */
	public QuantumHand() {
		for (int i = 0; i < hand.length; i++) {
			hand[i] = new HashMap<>();
		}
		for (int i = 0; i < 48; i++) {
			hand[8].put(new Card(i), 1d / 6);
		}
		bounds[8][0] = 8;
		bounds[8][1] = 8;
	}

	public void update() {
		for (int i = 0; i < 8; i++) {
			if (!moved[i] && bounds[i][0] == bounds[i][1]) {
				for (int j = 0; j < 6; j++) {
					Card c = new Card(i * 6 + j);
					if (hand[8].containsKey(c)) {
						hand[i].put(c, hand[8].remove(c));
					}
				}
				moved[i] = true;
			}
			if (moved[i]) {
				double sum = hand[i].values().stream().reduce(0d, Double::sum);
				double ratio = hand[i].size() / sum;
				final int j = i;
				hand[i].entrySet().forEach((Map.Entry<Card, Double> e) ->
						hand[j].put(e.getKey(), e.getValue() * ratio));
			}
		}
	}

	public Map<Card, Double>[] getHand() {
		return hand;
	}
}
