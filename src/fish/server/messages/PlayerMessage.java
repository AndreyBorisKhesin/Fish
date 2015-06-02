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
	public PMType pmType();

	public enum PMType {
		GAME_STATE, Q_ASKED, Q_RESPONSE, DEC_STARTED, DEC_UPDATE, DEC_ENDED, START_GAME, CONNECTED, PREGAME_UPDATE;
	}
}
