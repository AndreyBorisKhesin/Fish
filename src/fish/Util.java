package fish;

import java.util.ArrayList;
import java.util.List;

/**
 * General class for utility variables and methods.
 */
public final class Util {

	/**
	 * Determines if a given number is a valid number of players.
	 *
	 * @param n The proposed number of players.
	 * @return True if n is a valid number of players, false otherwise.
	 */
	public static boolean validPlayerNum(int n) {
		return n == 4 || n == 6 || n == 8 || n == 12;
	}

	/**
	 * Generic floating point epsilon value
	 */
	public static final Double EPS = 1e-9;

	public static final Double TOL = 0.1;

	public static boolean isZero(double d) {
		return Math.abs(d) < EPS;
	}

	public static boolean inTol(double a, double b) {
		return Math.max(a, b) / Math.min(a, b) < 1 + TOL;
	}

	/**
	 * Returns a full deck of cards
	 * 
	 * @return A full deck of cards
	 */
	public static List<Card> deck() {
		List<Card> deck = new ArrayList<Card>();
		for (int i = 0; i < 48; i++) {
			deck.add(new Card(i));
		}
		return deck;
	}

	/**
	 * Returns the human representation of a given suit
	 */
	public static String suitHumanRep(int suit) {
		final String suits[] = { "Clubs", "Diamonds", "Hearts",
				"Spades" };
		return suit % 2 == 0 ? "Low " : "High " + suits[suit / 2];
	}
}
