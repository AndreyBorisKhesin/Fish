package fish.server.messages;

/**
 * Indicates the player interface has successfully connected, and provides an
 * id.
 */
public class PMConnected implements PlayerMessage {

	@Override
	public PMType getType() {
		return PMType.CONNECTED;
	}

	/**
	 * The id of the connecting player
	 */
	public final int id;

	public PMConnected(int id) {
		this.id = id;
	}
}
