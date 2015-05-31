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
	private HashMap<Card, Double>[] quantumHand = new HashMap[9];

	/**
	 * Cards that the Player definitely holding.
	 */
	private Hand hand = new Hand();

	/**
	 * Bounds on the possible number of cards in a suit that a Player can have.
	 */
	private int[][] bounds = new int[8][2];

	/**
	 * An array storing if a suit was moved away from the generic suit or not.
	 */
	private boolean[] moved = new boolean[8];

	/**
	 * Default QuantumHand constructor.
	 * Contains each Card with equal probability.
	 */
	public QuantumHand() {
		for (int i = 0; i < 8; i++) {
			quantumHand[i] = new HashMap<>();
			bounds[i][0] = 0;
			bounds[i][1] = 6;
		}
		quantumHand[8] = new HashMap<>();
		for (int i = 0; i < 48; i++) {
			quantumHand[8].put(new Card(i), 1d / 6);
		}
	}

	public int suit(int suit) {
		return moved[suit] ? suit : 8;
	}

	public int suit(Card c) {
		return suit(c.suit);
	}

	/**
	 * Updates and re-balances the QuantumHand.
	 */
	public void update() {
		boolean b = true;
		while (b) {
			b = false;
			for (int i = 0; i < 8; i++) {
				if (!moved[i]) {
					int pos = 0;
					for (Card c : (Card[]) quantumHand[8].keySet().toArray()) {
						if (c.suit == i) {
							pos++;
						}
					}
					bounds[i][1] = Math.min(bounds[i][1], pos);
					if (bounds[i][0] == bounds[i][1]) {
						for (int j = 0; j < 6; j++) {
							Card c = new Card(i * 6 + j);
							if (quantumHand[8].containsKey(c)) {
								quantumHand[i].put(c, quantumHand[8].remove(c));
							}
						}
						moved[i] = true;
						b = true;
					}
				}
				for (int j = 0; j < 6; j++) {
					Card c = new Card(6 * i + j);
					if (quantumHand[i].containsKey(c)
							&& isZero(quantumHand[i].get(c) - 1)) {
						quantumHand[i].remove(c);
						hand.add(c);
						bounds[i][0]--;
						bounds[i][1]--;
						b = true;
					}
				}
				if (moved[i] && quantumHand[i].size() > 0) {
					double sum = quantumHand[i].values().stream().reduce(0d,
							Double::sum);
					double ratio = quantumHand[i].size() / sum;
					final int j = i;
					quantumHand[i].entrySet().forEach((HashMap.Entry<Card,
							Double> e) -> quantumHand[j].put(e.getKey(),
							e.getValue() * ratio));
					b |= !isZero(ratio - 1);
				}
			}
			double sum = quantumHand[8].values().stream().reduce(0d,
					Double::sum);
			double ratio = quantumHand[8].size() / sum;
			quantumHand[8].entrySet().forEach((HashMap.Entry<Card, Double> e) ->
					quantumHand[8].put(e.getKey(), e.getValue() * ratio));
			b |= !isZero(ratio - 1);
		}
	}

	public void zero(Card c) {
		quantumHand[suit(c)].remove(c);
	}

	/**
	 * Returns the probabilistic Cards of the QuantumHand.
	 *
	 * @return The list of Cards that are possibly in the QuantumHand.
	 */
	public HashMap<Card, Double>[] getQuantumHand() {
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

	public int[][] getBounds() {
		return bounds;
	}
}
