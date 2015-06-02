package fish.server.messages;

/**
 * A message to send to a player.
 */
public interface PlayerMessage {

	/**
	 * The type of the player message
	 * 
	 * @return A PMType representing the type
	 */
	public PMType getType();

	public enum PMType {
		GAME_STATE, Q_ASKED, DEC_START, DEC_END, GAME_START, CONNECTED, PREGAME_UPDATE;
	}

}
