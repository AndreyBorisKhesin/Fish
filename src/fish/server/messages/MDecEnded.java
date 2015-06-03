package fish.server.messages;

import java.util.Arrays;

import fish.Declaration;

public class MDecEnded implements PlayerMessage, ServerMessage {

	@Override
	public SMType smType() {
		return SMType.DEC_ENDED;
	}

	@Override
	public PMType pmType() {
		return PMType.DEC_ENDED;
	}

	/**
	 * The declaration we're defining
	 */
	public Declaration d;

	/**
	 * The actual positions of the cards
	 */
	public int pos[];

	/**
	 * Whether or not it succeeded
	 */
	public boolean succeeded;

	/**
	 * The originating player id, used for messages to the server
	 */
	public int id;
	
	public MDecEnded(Declaration d, int pos[], boolean succeeded) {
		this.d = d;
		this.pos = pos;
		this.succeeded = succeeded;
		this.id = d.source;
	}

	@Override
	public String toString() {
		return "Message: DEC_ENDED\n" +
				"{\n"+
				"\td: " + d + ",\n" +
				"\tpos: " + Arrays.toString(pos) + ",\n" +
				"\tsucceeded: " + succeeded + ",\n" +
				"}";
	}
}
