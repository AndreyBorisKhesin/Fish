package fish.server.messages;



/**
 * A message to send to a player
 * 
 */
public interface ServerMessage {
	
	/**
	 * The type of message to be sent.
	 * 
	 * @return An enum representing the type to be sent
	 */
	public SMType getType();
	
	/**
	 * The id of the player sending this message.
	 *
	 */
	public int getId();

	public enum SMType {
		Q_ASKED, DEC_STARTED, DEC_ENDED, CONNECTION, RECONNECTION, END_SERVER, START_GAME;
	}
}
