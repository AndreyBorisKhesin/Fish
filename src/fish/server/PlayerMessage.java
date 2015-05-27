package fish.server;

/**
 * A message to send to a player
 * 
 */
public class PlayerMessage {
	/**
	 * 0: game state
	 * 1: question asked
	 * 2: declaration started
	 * 3: declaration ended
	 */
	public final int type;

	public final PMData data;

	public PlayerMessage(int type, PMData data) {
		this.type = type;
		this.data = data;
	}
}
