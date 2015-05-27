package fish.server;


/**
 * A message to send to a player
 * 
 */
public class ServerMessage {
	/**
	 * The type of message to be sent
	 */
	public final SMType type;

	/**
	 * The data to be transmitted
	 */
	public final SMData data;

	public ServerMessage(SMType type, SMData data) {
		this.type = type;
		this.data = data;
	}

	public enum SMType {
		Q_ASKED, DEC_STARTED, DEC_ENDED, CONNECTION, RECONNECTION;
	}

	/**
	 * A data object to be sent in a message
	 * 
	 */
	public interface SMData {

		/**
		 * Returns the type of this data object
		 * 
		 */
		public int getType();
	}

}
