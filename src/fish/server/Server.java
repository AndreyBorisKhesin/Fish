package fish.server;

import static fish.server.Log.log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import fish.server.messages.PMConnected;
import fish.server.messages.SMConnection;
import fish.server.messages.ServerMessage;
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

	public Server() {
		sq = new ConcurrentLinkedQueue<ServerMessage>();
		clients = new ArrayList<PlayerInterface>();
		
		(t = new Thread(this)).start();
	}

	@Override
	public void run() {
		while (true) {
			ServerMessage sm = waitOnQueue(sq);

			switch (sm.getType()) {
			case CONNECTION:
				connection((SMConnection) sm);
				break;
			case START_GAME:
				break;
			}
		}
	}

	private void connection(SMConnection sm) {
		clients.add(sm.pi);
		sm.pi.insertMessage(new PMConnected(clients.size() - 1));
	}

	/**
	 * Asynchronously adds a message to be processed by the server
	 */
	public void insertMessage(ServerMessage m) {
		sq.add(m);
		synchronized(sq) {
			sq.notify();
		}
	}

	/**
	 * This should be run in the thread that will be interrupted on new
	 * messages. No one else should be waiting on this queue.
	 * 
	 * @param q The queue to wait on
	 * @return An object that will be delivered in the queue
	 */
	public static <T> T waitOnQueue(ConcurrentLinkedQueue<T> q) {
		while (q.isEmpty()) {
			synchronized(q) {
				try {
					q.wait();
				} catch (InterruptedException e) {
				}
			}
		}

		/*
		 * there are no other consumers of this queue so we can
		 * safely dequeue
		 */
		T t = q.poll();
		if (t == null) {
			log("attempted to dequeue waiting queue and found it empty");
		}

		return t;
	}
}
