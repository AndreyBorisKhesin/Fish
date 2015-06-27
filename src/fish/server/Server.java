package fish.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import fish.players.AI;
import fish.players.Player;
import fish.server.messages.PlayerMessage;
import fish.server.messages.SMConnection;
import fish.server.messages.ServerMessage;
import fish.server.playerinterface.LocalInterface;
import fish.server.playerinterface.PlayerInterface;

/**
 * The server object coordinates communications with clients and is the basis
 * for controlling the game
 *
 */
public class Server implements Runnable {

	/**
	 * The thread in which this server operates
	 */
	Thread t;

	/**
	 * The queue of input messages received from clients
	 */
	ConcurrentLinkedQueue<ServerMessage> sq;

	/**
	 * List of player interfaces with which to communicate with clients
	 */
	List<PlayerInterface> clients;

	/**
	 * The object we pass control over to to run the game
	 */
	GameController c;

	/**
	 * The controller currently in control
	 */
	private Controller controller;

	/**
	 * The controllers that are available to switch control to
	 */
	private Controller pregameC, gameC;

	private AINames ainames;

	public Server() {
		sq = new ConcurrentLinkedQueue<ServerMessage>();
		clients = new ArrayList<PlayerInterface>();

		ainames = new AINames();

		(t = new Thread(this)).start();
	}

	@Override
	public void run() {
		initServer();

		while (true) {
			ServerMessage sm = ServerUtil.waitOnQueue(sq);

			if (controller == null) {
				Log.log("server controller is null");
				continue;
			}

			controller.handleMessage(sm);
		}
	}

	private void initServer() {
		pregameC = new PregameController();
		gameC = new GameController();

		switchState(ServerState.PRE_GAME);
	}

	/**
	 * Returns the username of the specified player
	 * 
	 * @param id The id of the player
	 * @return The string of the player's username
	 */
	public String getUname(int id) {
		return clients.get(id).getUname();
	}

	/**
	 * Indicates that the server should switch to a new state, should be
	 * called by other controllers
	 */
	void switchState(ServerState s) {
		if (controller != null) {
			controller.exit();
		}
		switch (s) {
		case PRE_GAME:
			controller = pregameC;
			break;
		case GAME:
			controller = gameC;
			break;
		}
		controller.enter(this);
	}

	enum ServerState {
		PRE_GAME, GAME;
	}

	public void addAI() {
		Player ai = new AI(ainames.getName());
		this.insertMessage(new SMConnection(
				new LocalInterface(this, ai)));
	}
	
	/**
	 * Send a message to all connected players
	 * 
	 * @param pm The message to send
	 */
	public void broadcast(PlayerMessage pm) {
		clients.forEach(pi -> pi.insertMessage(pm));
	}

	/**
	 * Asynchronously adds a message to be processed by the server
	 */
	public void insertMessage(ServerMessage m) {
		sq.add(m);
		synchronized (sq) {
			sq.notify();
		}
	}
}
