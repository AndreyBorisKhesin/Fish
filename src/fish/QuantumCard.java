package fish;

/**
 * A Card containing a probability of being in a Player's Hand.
 */
public class QuantumCard extends Card {

	/**
	 * Probability of being in a given Player's Hand.
	 */
	private double prob;

	/**
	 * QuantumCard constructor accepting suit, rank, and probability.
	 *
	 * @param suit Suit of Card.
	 * @param rank Value of Card.
	 * @param prob Probability of this Card of being in a Player's Hand.
	 */
	public QuantumCard(int suit, int rank, double prob) {
		super(suit, rank);
		if (prob < 0 || prob > 1) {
			throw new IllegalArgumentException
					("Probability value is out of bounds.");
		}
		this.prob = prob;
	}

	/**
	 * QuantumCard constructor accepting hashcode and probability.
	 *
	 * @param hashcode Hashcode of Card.
	 * @param prob Probability of this Card of being in a Player's Hand.
	 */
	public QuantumCard(int hashcode, double prob) {
		this(hashcode / 6, hashcode % 6, prob);
	}

	/**
	 * Probability getter.
	 *
	 * @return Probability of being in a Player's Hand.
	 */
	public double getProb() {
		return prob;
	}

	/**
	 * Probability setter.
	 * @param prob New probability value.
	 */
	public void setProb(double prob) {
		this.prob = prob;
	}
}
