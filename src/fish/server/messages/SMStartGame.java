package fish.server.messages;

import fish.server.playerinterface.PlayerInterface;

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

	@Override
	public int getId() {
		return id;
	}
}
