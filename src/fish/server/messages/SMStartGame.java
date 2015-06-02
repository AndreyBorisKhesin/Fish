package fish.server.messages;

/**
 * Indicates that this is a message to start the game
 *
 */
public class SMStartGame implements ServerMessage {

	@Override
	public SMType getType() {
		return SMType.START_GAME;
	}

	/**
	 * The sending id
	 */
	public final int id;
	
	public SMStartGame(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Server Message: START_GAME\nid: " + id;
	}
}
