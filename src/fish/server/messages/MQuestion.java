package fish.server.messages;

import fish.Question;

/**
 * Represents a player asking a question
 *
 */
public class MQuestion implements ServerMessage, PlayerMessage {

	@Override
	public SMType smType() {
		return SMType.Q_ASKED;
	}

	@Override
	public PMType pmType() {
		return PMType.Q_ASKED;
	}

	public final int id;

	public final Question q;

	public MQuestion(Question q) {
		id = q.source;
		this.q = q;
	}

	@Override
	public String toString() {
		return "Message: QUESTION_ASKED\n" +
				"{\n" +
				"\tid: " + id + ",\n" +
				"\tq: " + q + ",\n" +
				"}";
	}
}
