package fish.server;

import java.util.concurrent.ConcurrentLinkedQueue;

import fish.events.Question;
import fish.server.playermessages.PlayerMessage;

/**
 * An interface to define interactions between the player and server.
 */
public abstract class PlayerInterface implements Runnable {

	/**
	 * The Thread on which this interface is running.
	 */
	protected Thread t;

	/**
	 * The message Queue to deliver to the Player.
	 */
	protected ConcurrentLinkedQueue<PlayerMessage> mqueue;

	/**
	 * The username of this user
	 */
	protected String uname;

	public PlayerInterface(String uname) {
		mqueue = new ConcurrentLinkedQueue<>();
		t = new Thread(this);
		this.uname = uname;
	}

	/**
	 * Call at the end of subclass constructors.
	 */
	protected void launch() {
		t.start();
	}

	/**
	 * Provides a game controller object for communication with the game
	 * state.
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

	/**
	 * Returns the assigned username for this player
	 */
	public String getUname() {
		return uname;
	}
}
