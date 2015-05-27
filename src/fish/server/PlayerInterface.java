package fish.server;

import java.util.concurrent.ConcurrentLinkedQueue;

import fish.Question;

/**
 * An interface to define interactions between the player and server.
 * 
 */
public abstract class PlayerInterface implements Runnable {

	/**
	 * The Thread on which this interface is running
	 */
	protected Thread t;

	/**
	 * The message queue to deliver to the player
	 */
	protected ConcurrentLinkedQueue<PlayerMessage> mqueue;

	public PlayerInterface() {
		mqueue = new ConcurrentLinkedQueue<>();
		t = new Thread(this);
	}

	/**
	 * Call at the end of subclass constructors
	 */
	protected void launch() {
		t.start();
	}

	/**
	 * Provides a game controller object for communication with the game.
	 * state
	 * 
	 * @param g The GameController object running the current game.
	 */
	public abstract void setController(GameController g);

	/**
	 * Requests the player to ask a question.
	 * 
	 * @return A question object representing the requested question.
	 */
	public abstract Question getQuestion();

	/**
	 * Returns the thread operating this player.
	 * 
	 * @return A thread object that is maintaining this player.
	 */
	public Thread getThread() {
		return t;
	}

	/**
	 * Inserts the message to be sent along in the processing queue.
	 * 
	 * @param pm The message to be passed to the player.
	 */
	public void insertMessage(PlayerMessage pm) {
		mqueue.add(pm);
	}
}
