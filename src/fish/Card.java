package fish;

/**
 * A class that represents a card that holds a suit and a rank.
 */
public class Card {

	/**
	 * Rank of suit from 0 to 7.
	 * 0: Low clubs
	 * 1: High clubs
	 * 2: Low diamonds
	 * 3: High diamonds
	 * 4: Low hearts
	 * 5: High hearts
	 * 6: Low spades
	 * 7: High spades
	 */
	public final int suit;

	/**
	 * Rank of card from 0 to 5.
	 * 0: 2 or 9
	 * 1: 3 or T
	 * 2: 4 or J
	 * 3: 5 or Q
	 * 4: 6 or K
	 * 5: 7 or A
	 */
	public final int rank;

	/**
	 * Card constructor accepting suit and rank.
	 *
	 * @param suit Suit of card.
	 * @param rank Value of card.
	 */
	public Card(int suit, int rank) {
		if (suit < 0 || suit > 7 || rank < 0 || rank > 5) {
			throw new IllegalArgumentException();
		}
		this.suit = suit;
		this.rank = rank;
	}

	/**
	 * Card constructor accepting hashcode.
	 *
	 * @param hashCode Hashcode of card.
	 */
	public Card(int hashCode) {
		this(hashCode / 6, hashCode % 6);
	}


	/**
	 * Returns a clone of this card.
	 *
	 * @return A clone of the invoking card.
	 */
	@Override
	public Card clone() {
		return new Card(this.hashCode());
	}

	/**
	 * Determines if two card objects are equivalent by suit and rank.
	 *
	 * @param o Card object for this to be compared to.
	 * @return True if objects are equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Card)) {
			return false;
		}
		Card c = (Card) o;
		return c.suit == suit && c.rank == rank;
	}

	/**
	 * Returns a hashcode uniquely representing the card in the form of suit *
	 * 6 + rank.
	 *
	 * @return Returns hashcode for the card.
	 */
	@Override
	public int hashCode() {
		return suit * 6 + rank;
	}
}
