package fish.server.messages;

import fish.server.playerinterface.PlayerInterface;

public class SMConnection implements ServerMessage {

	@Override
	public SMType getType() {
		return SMType.CONNECTION;
	}

	/**
	 * The player interface that is connecting
	 */
	public final PlayerInterface pi;

	public SMConnection(PlayerInterface pi) {
		this.pi = pi;
	}

	@Override
	public String toString() {
		return "Server Message: CONNECTION\n" + pi;
	}
}
