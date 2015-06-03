package fish.server.messages;

import fish.Declaration;

public class MDecUpdate implements PlayerMessage, ServerMessage {

	@Override
	public SMType smType() {
		return SMType.DEC_UPDATE;
	}

	@Override
	public PMType pmType() {
		return PMType.DEC_UPDATE;
	}

	/**
	 * The declaration we're defining
	 */
	public Declaration d;

	/**
	 * The originating player id, used for messages to the server
	 */
	public int id;

	public MDecUpdate(Declaration d) {
		this.d = d;
		this.id = d.source;
	}

	@Override
	public String toString() {
		return "Message: DEC_UPDATED\n" + d;
	}
}
