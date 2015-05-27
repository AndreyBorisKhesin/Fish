package fish.server;

/**
 * A data object to be sent in a message.
 */
public interface PMData {

	/**
	 * Returns the type of this data object.
	 * 
	 * @see fish.server.PlayerMessage.type
	 */
	public int getType();
}
