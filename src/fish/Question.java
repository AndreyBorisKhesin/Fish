package fish;


/**
 * A data object representing a query from one player to another.
 */
public final class Question {

	/**
	 * The person asking the question.
	 */
	public final int source;

	/**
	 * The person being asked.
	 */
	public final int dest;

	/**
	 * The card being asked for.
	 */
	public final Card c;

	/**
	 * Basic constructor for the question.
	 *
	 * @param source The player asking.
	 * @param dest The player being asked.
	 * @param c The card being asked for.
	 */
	public Question(int source, int dest, Card c) {
		this.source = source;
		this.dest = dest;
		this.c = c;
	}
	
	@Override
	public String toString() {
		return "{source: " + source + ", dest: " + dest + ", card: " + c + "}";
	}
}
