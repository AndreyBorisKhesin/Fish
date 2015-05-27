package fish;

/**
 * A card containing a probability of being in a player's hand.
 */
public class QuantumCard extends Card {

	/**
	 * Probability of being in a given player's hand.
	 */
	private double prob;

	/**
	 * Quantum card constructor accepting suit, rank, and probability.
	 *
	 * @param suit Suit of card.
	 * @param rank Value of card.
	 * @param prob Probability of this card of being in a player's hand.
	 */
	public QuantumCard(int suit, int rank, double prob) {
		super(suit, rank);
		if (prob < 0 || prob > 1) {
			throw new IllegalArgumentException();
		}
		this.prob = prob;
	}

	/**
	 * Quantum card constructor accepting hashcode and probability.
	 *
	 * @param hashcode Hashcode of card.
	 * @param prob Probability of this card of being in a player's hand.
	 */
	public QuantumCard(int hashcode, double prob) {
		this(hashcode / 6, hashcode % 6, prob);
	}

	/**
	 * Probability getter.
	 *
	 * @return Probability of being in a player's hand.
	 */
	public double getProb() {
		return prob;
	}
}
