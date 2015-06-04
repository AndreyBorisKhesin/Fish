package fish.server.playerinterface;

import java.util.concurrent.ConcurrentLinkedQueue;

import fish.Declaration;
import fish.Question;
import fish.server.GameController;
import fish.server.Server;
import fish.server.ServerUtil;
import fish.server.messages.MDecEnded;
import fish.server.messages.MDecStart;
import fish.server.messages.MDecUpdate;
import fish.server.messages.MQuestion;
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

	@Override
	public void run() {
		while (true) {
			PlayerMessage pm = ServerUtil.waitOnQueue(this.mqueue);

			processMessage(pm);
		}
	}

	/**
	 * Process the incoming message
	 * 
	 * @param pm The message to handle
	 */
	protected abstract void processMessage(PlayerMessage pm);
	
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
		synchronized (mqueue) {
			mqueue.notify();
		}
	}

	/**
	 * Returns the assigned username for this player
	 */
	public String getUname() {
		return uname;
	}

	/**
	 * Interface to allow the player to ask a question from the server
	 * 
	 * @param q The question to send
	 * 
	 */
	public void sendQuestion(Question q) {
		s.insertMessage(new MQuestion(new Question(id, q.dest, q.c)));
	}

	public void sendDecStart(Declaration d) {
		s.insertMessage(new MDecStart(new Declaration(id, d.suit)));
	}

	public void sendDecUpdate(Declaration d) {
		s.insertMessage(new MDecUpdate(new Declaration(id, d.suit,
				d.locs)));
	}

	public void sendDecEnded(Declaration d) {
		s.insertMessage(new MDecEnded(new Declaration(id, d.suit,
				d.locs)));
	}
}
