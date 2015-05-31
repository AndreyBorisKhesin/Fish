package fish.server.playerinterface;

import java.util.concurrent.ConcurrentLinkedQueue;

import fish.server.GameController;
import fish.server.Server;
import fish.server.messages.PlayerMessage;

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
	 * The id of this user, used for sending messages.
	 */
	protected int id;

	/**
	 * The username of this user
	 */
	protected String uname;

	/**
	 * The server with which the interface is communicating
	 */
	protected Server s;
	
	/**
	 * The game controller to be accessed by a player
	 */
	protected GameController gc;
	
	public PlayerInterface(String uname, Server s) {
		mqueue = new ConcurrentLinkedQueue<>();
		t = new Thread(this);
		this.uname = uname;
		this.s = s;
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
	 * @param gc The GameController object running the current game.
	 */
	public void setController(GameController gc) {
		this.gc = gc;
	}

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
