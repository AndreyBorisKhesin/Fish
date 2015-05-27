package fish;

/**
 * A data object representing a query from one player to another
 * 
 */
public class Question implements Event {

	/**
	 * The person asking the question
	 */
	final int source;

	/**
	 * The person being asked
	 */
	final int dest;

	/**
	 * The card being asked for
	 */
	final Card c;

	public Question(int source, int dest, Card c) {
		super();
		this.source = source;
		this.dest = dest;
		this.c = c;
	}
}
