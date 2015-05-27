package fish.server;

/**
 * A message to send to a player.
 */
public class PlayerMessage {
	/**
	 * The type of message from 0 to 3.
	 * 0: Game state.
	 * 1: Question asked.
	 * 2: Declaration started.
	 * 3: Declaration ended.
	 */
	public final int type;

	public final PMData data;

	public PlayerMessage(int type, PMData data) {
		this.type = type;
		this.data = data;
	}
}
