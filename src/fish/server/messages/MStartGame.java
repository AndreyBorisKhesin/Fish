package fish.server.messages;

/**
 * Indicates that this is a message to start the game
 *
 */
public class MStartGame implements ServerMessage, PlayerMessage {

	@Override
	public SMType smType() {
		return SMType.START_GAME;
	}

	@Override
	public PMType pmType() {
		return PMType.START_GAME;
	}

	/**
	 * The sending id
	 */
	public final int id;

	public MStartGame(int id) {
		this.id = id;
	}

	public MStartGame() {
		this(0);
	}

	@Override
	public String toString() {
		return "Message: START_GAME\nid: " + id;
	}
}
