package fish;

import java.util.HashSet;
import java.util.Set;

/**
 * Data object holding a set of cards.
 */
public class Hand {
	/**
	 * The hand containing a set of cards for every suit.
	 */
	private Set<Card>[] hand;

	/**
	 * Default hand constructor. Makes an empty hand.
	 */
	public Hand() {
		hand = new Set[8];
		for (int i = 0; i < 8; i++) {
			hand[i] = new HashSet();
		}
	}

	/**
	 * Creates a set containing the cards in the set.
	 * 
	 * @param cards Cards that are added to player's hand.
	 */
	public Hand(Set<Card> cards) {
		this();
		cards.forEach(this::insert);
	}

	/**
	 * Inserts a card into the hand.
	 * 
	 * @param c Card to be inserted.
	 */
	public void insert(Card c) {
		hand[c.suit].add(c);
	}

	/**
	 * Removes a card from a hand.
	 * 
	 * @param c The card to be removed.
	 */
	public void remove(Card c) {
		hand[c.suit].remove(c);
	}

	/**
	 * Returns a set containing the cards in the hand.
	 * 
	 * @return A set containing all cards in the hand.
	 */
	public Set getCards() {
		Set cards = new HashSet();
		for (Set s : hand) {
			cards.addAll(s);
		}
		return cards;
	}

	/**
	 * Returns a set containing the cards in the specified suit.
	 * 
	 * @param suit The suit identifier to access.
	 * @return A set containing the cards held in the suit.
	 */
	public Set<Card> getSuit(int suit) {
		return new HashSet(hand[suit]);
	}

	/**
	 * Returns whether or not the hand holds this card.
	 * 
	 * @param c The card to query.
	 * @return True if the player has this card, false otherwise.
	 */
	public boolean has(Card c) {
		return hand[c.suit].contains(c);
	}

	/**
	 * Returns the number of cards in the player's hand.
	 * 
	 * @return The number of cards in the hand.
	 */
	public int getNumCards() {
		int num = 0;
		for (Set s : hand) {
			num += s.size();
		}
		return num;
	}
}
