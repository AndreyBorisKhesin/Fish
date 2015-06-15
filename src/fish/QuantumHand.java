package fish;

import static fish.Util.isZero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	@SuppressWarnings("unchecked")
	private Map<Card, Double>[] quantumHand = new Map[9];

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

	private int genericCardNum;

	/**
	 * Default QuantumHand constructor.
	 * Contains each Card with equal probability.
	 */
	public QuantumHand(int numPlayers) {
		for (int i = 0; i < 8; i++) {
			quantumHand[i] = new HashMap<>();
			bounds[i][0] = 0;
			bounds[i][1] = Math.min(6, 48 / numPlayers);
		}
		quantumHand[8] = new HashMap<>();
		for (int i = 0; i < 48; i++) {
			quantumHand[8].put(new Card(i), 1d / numPlayers);
		}
		genericCardNum = 48 / numPlayers;
	}

	public QuantumHand(Hand hand) {
		this.hand = hand;
		for (int i = 0; i < 8; i++) {
			quantumHand[i] = new HashMap<>();
			bounds[i][0] = 0;
			bounds[i][1] = 0;
		}
		quantumHand[8] = new HashMap<>();
		for (int i = 0; i < 48; i++) {
			quantumHand[8].put(new Card(i), 0d);
		}
		genericCardNum = 0;
	}

	public List<Card> check() {
		List<Card> list = new ArrayList<>();
		for (int i = 0; i < 48; i++) {
			if (isZero(this.get(i) - 1)) {
				fix(i);
				list.add(new Card(i));
			}
		}
		return list;
	}

	public void fix(Card c) {
		if (!isZero(this.get(c))) {
			bounds[suit(c)][0]--;
			bounds[suit(c)][1]--;
			if (c.suit == 8) {
				genericCardNum--;
			}
		}
		hand.add(c);
		zero(c);
	}

	public void fix(int i) {
		fix(new Card(i));
	}

	public double get(Card c) {
		if (hand.contains(c)) {
			return 1;
		}
		if (!quantumHand[suit(c)].containsKey(c)) {
			return 0;
		}
		return quantumHand[suit(c)].get(c);
	}

	public double get(int i) {
		return get(new Card(i));
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
	public void move(int suit) {
		if (!moved[suit]) {
			int pos = 0;
			for (Card c : quantumHand[8].keySet()) {
				if (c.suit == suit) {
					pos++;
				}
			}
			bounds[suit][1] = Math.min(bounds[suit][1], pos);
			if (bounds[suit][0] == bounds[suit][1]) {
				for (int j = 0; j < 6; j++) {
					Card c = new Card(suit * 6 + j);
					if (quantumHand[8].containsKey(c)) {
						quantumHand[suit].put(c, quantumHand[8].remove(c));
					}
				}
				moved[suit] = true;
				genericCardNum -= bounds[suit][0];
			}
		}
	}

	public void zero(Card c) {
		quantumHand[suit(c)].remove(c);
	}

	public void zero(int i) {
		zero(new Card(i));
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

	public int[][] getBounds() {
		return bounds;
	}

	public boolean[] getMoved() {
		return moved;
	}

	public int getGenericCardNum() {
		return genericCardNum;
	}
}
