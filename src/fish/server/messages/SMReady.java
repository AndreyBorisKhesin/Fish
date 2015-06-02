package fish.server.messages;

public class SMReady implements ServerMessage {

	/**
	 * The sending player's id
	 */
	public final int id;

	/**
	 * Whether or not the player is ready
	 */
	public final boolean ready;

	public SMReady(int id, boolean ready) {
		this.id = id;
		this.ready = ready;
	}

	@Override
	public SMType getType() {
		return SMType.READY_UPDATE;
	}

	@Override
	public String toString() {
		return "Server Message: READY\nid: " + id + ",\nready: " + ready;
	}
}
