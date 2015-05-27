package fish;

/**
 * A data object representing a query from one player to another.
 */
public class Question implements Event {

	/**
	 * The person asking the question.
	 */
	final int source;//FIXME change int to Player

	/**
	 * The person being asked.
	 */
	final int dest;//FIXME change int to Player

	/**
	 * The card being asked for.
	 */
	final Card c;

	/**
	 * Basic constructor for the question.
	 *
	 * @param source The player asking.
	 * @param dest The player being asked.
	 * @param c The card being asked for.
	 */
	public Question(int source, int dest, Card c) {//FIXME change int to Player
		this.source = source;
		this.dest = dest;
		this.c = c;
	}
}
