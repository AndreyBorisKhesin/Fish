package fish;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data object holding a set of cards
 * 
 */
public class Hand {
	private List<Set<Card>> hand;

	public Hand() {
		hand = new ArrayList<Set<Card>>();
		for (int i = 0; i < 8; i++) {
			hand.add(new HashSet<Card>());
		}
	}

	/**
	 * Creates a set containing the cards in the set
	 * 
	 * @param cards
	 */
	public Hand(Set<Card> cards) {
		this();
		for (Card c : cards) {
			insert(c);
		}
	}

	/**
	 * Inserts a card into the hand
	 * 
	 * @param c Card to be inserted
	 */
	public void insert(Card c) {
		hand.get(c.suit).add(c);
	}

	/**
	 * Removes a card from a hand
	 * 
	 * @param c The card to be removed
	 */
	public void remove(Card c) {
		hand.get(c.suit).remove(c);
	}

	/**
	 * Returns a set containing the cards in the hand
	 * 
	 * @return HashSet<Card> containing all cards in the hand
	 */
	public Set<Card> getCards() {
		Set<Card> cards = new HashSet<Card>();
		for (Set<Card> s : hand) {
			cards.addAll(s);
		}

		return cards;
	}

	/**
	 * Returns a set containing the cards in the specified halfsuit
	 * 
	 * @param suit The halfsuit identifier to access
	 * @return A Set<Card> containing the cards held in the halfsuit
	 */
	public Set<Card> getHalfsuit(int suit) {
		return new HashSet<Card>(hand.get(suit));
	}

	/**
	 * Returns whether or not the hand holds this card
	 * 
	 * @param c The card to query
	 * @return boolean representing whether it is held or not
	 */
	public boolean has(Card c) {
		return hand.get(c.suit).contains(c);
	}

	/**
	 * The number of cards in the hand
	 * 
	 * @return int representing the number of cards
	 */
	public int getNumCards() {
		int num = 0;
		for (Set<Card> s : hand) {
			num += s.size();
		}
		return num;
	}
}
