package fish;

import static fish.Util.*;

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
	private Map<Card, Double>[] quantumHand = new Map[9];

	/**
	 * Cards that the Player definitely holding.
	 */
	private Hand hand = new Hand();

	/**
	 * Bounds on the possible number of cards in a suit that a Player can have.
	 */
	private int[][] bounds = new int[9][2];

	/**
	 * An array storing if a suit was moved away from the generic suit or not.
	 */
	private boolean[] moved = new boolean[8];

	/**
	 * Default QuantumHand constructor.
	 * Contains each Card with equal probability.
	 */
	public QuantumHand() {
		for (int i = 0; i < quantumHand.length; i++) {
			quantumHand[i] = new HashMap<>();
		}
		for (int i = 0; i < 48; i++) {
			quantumHand[8].put(new Card(i), 1d / 6);
		}
		bounds[8][0] = 8;
		bounds[8][1] = 8;
	}

	/**
	 * Updates and rebalances the QuantumHand.
	 */
	public void update() {
		boolean b = true;
		while (b) {
			b = false;
			for (int i = 0; i < 8; i++) {
				if (!moved[i] && bounds[i][0] == bounds[i][1]) {
					for (int j = 0; j < 6; j++) {
						Card c = new Card(i * 6 + j);
						if (quantumHand[8].containsKey(c)) {
							quantumHand[i].put(c, quantumHand[8].remove(c));
						}
					}
					moved[i] = true;
					bounds[8][0] -= bounds[i][0];
					bounds[8][1] -= bounds[i][0];
					b = true;
				}
				for (int j = 0; j < 6; j++) {
					Card c = new Card(6 * i + j);
					if (quantumHand[i].containsKey(c) && isZero(quantumHand[i].get(c) - 1)) {
						quantumHand[i].remove(c);
						hand.add(c);
						bounds[i][0]--;
						bounds[i][1]--;
						b = true;
					}
				}
				if (moved[i] && quantumHand[i].size() > 0) {
					double sum = quantumHand[i].values().stream().reduce(0d, Double::sum);
					double ratio = quantumHand[i].size() / sum;
					final int j = i;
					quantumHand[i].entrySet().forEach((Map.Entry<Card, Double> e) ->
							quantumHand[j].put(e.getKey(), e.getValue() * ratio));
					b |= !isZero(ratio - 1);
				}
			}
			double sum = quantumHand[8].values().stream().reduce(0d, Double::sum);
			double ratio = quantumHand[8].size() / sum;
			quantumHand[8].entrySet().forEach((Map.Entry<Card, Double> e) ->
					quantumHand[8].put(e.getKey(), e.getValue() * ratio));
			b |= !isZero(ratio - 1);
		}
	}

	/**
	 * Returns the probabilistic Cards of the QuantumHand.
	 *
	 * @return The list of Cards that are possibly in the QuantumHand.
	 */
	public Map<Card, Double>[] getQuantumHand() {
		return quantumHand;
	}

	/**
	 * Returns the non-probabilistic Cards of the QuantumHand.
	 *
	 * @return The list of Cards that are definitely in the QuantumHand.
	 */
	public Hand getHand() {
		return hand;
	}
}
