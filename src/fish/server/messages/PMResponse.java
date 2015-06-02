package fish.server.messages;

import fish.Question;

/**
 * Represents the result of a question
 *
 */
public class PMResponse implements PlayerMessage {

	@Override
	public PMType pmType() {
		return PMType.Q_RESPONSE;
	}

	public final Question q;
	public final boolean res;

	public PMResponse(Question q, boolean res) {
		this.q = q;
		this.res = res;
	}

	@Override
	public String toString() {
		return "Server Message: QUESTION_RESPONSE\n" +
				"{\n" +
				"\tq: " + q + ",\n" +
				"\tres: " + res + ",\n" +
				"}";
	}
}
