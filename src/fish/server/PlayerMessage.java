package fish.server;

/**
 * A message to send to a player.
 */
public class PlayerMessage {
	/**
	 * The type of message to be sent
	 */
	public final PMType type;

	/**
	 * The data to be transmitted
	 */
	public final PMData data;

	public PlayerMessage(PMType type, PMData data) {
		this.type = type;
		this.data = data;
	}

	public enum PMType {
		GAME_STATE, Q_ASKED, DEC_START, DEC_END, GAME_START, CONNECTED;
	}

	/**
	 * A data object to be sent in a message
	 * 
	 */
	public interface PMData {

		/**
		 * Returns the type of this data object
		 * 
		 */
		public int getType();
	}

}
