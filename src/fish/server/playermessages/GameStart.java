package fish.server.playermessages;

/**
 * Represents the start of a game
 */
public class GameStart implements PlayerMessage {

	@Override
	public PMType getType() {
		return PMType.GAME_START;
	}
}
