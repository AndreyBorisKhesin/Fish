package fish.server;

import fish.Question;

/**
 * An interface to define interactions between the player and server
 * 
 */
public interface PlayerInterface {
	/**
	 * Requests the player to ask a question
	 * 
	 * @return A question object representing the requested question
	 */
	public Question getQuestion();
}
