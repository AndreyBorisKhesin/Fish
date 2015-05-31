package fish.server.messages;

/**
 * Represents the start of a game
 */
public class PMGameStart implements PlayerMessage {

	@Override
	public PMType getType() {
		return PMType.GAME_START;
	}

	@Override
	public String toString() {
		return "Player Message: GAME_START";
	}
}
