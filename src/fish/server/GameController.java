package fish.server;

import static fish.server.Log.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import fish.Card;
import fish.Team;
import fish.Util;
import fish.events.Event;
import fish.server.playermessages.GameStart;
import fish.server.playermessages.PlayerKnowledge;

/**
 * The object controlling the game flow. It shall communicate with player
 * objects via an interface provided by the server.
 * 
 */
public class GameController {

	/**
	 * The thread in which this game controller operates
	 */
	private Thread t;

	/**
	 * The queue of input messages received from clients
	 */
	private ConcurrentLinkedQueue<ServerMessage> sq;

	/**
	 * Data object holding the current game state
	 */
	private GameState gs;

	/**
	 * Initializes data structures used in game operation
	 */
	public GameController() {
		gs = new GameState();
	}

	/**
	 * Begins the game with the given players
	 * 
	 * @param inters The interfaces with which to communicate with the
	 * players
	 */
	public void startGame(List<PlayerInterface> inters,
			ConcurrentLinkedQueue<ServerMessage> sq) {
		if (!Util.validPlayerNum(inters.size()))
			throw new IllegalArgumentException(
					"Invalid number of players: "
							+ inters.size());

		this.t = Thread.currentThread();
		this.sq = sq;
		initGame(inters);

	}

	private void initGame(List<PlayerInterface> inters) {
		List<Team> teams = new ArrayList<Team>();
		for (int i = 0; i < inters.size(); i++) {
			teams.add(i < inters.size() / 2 ? Team.BLK : Team.RED);
		}
		Collections.shuffle(teams);

		for (int i = 0; i < inters.size(); i++) {
			gs.players.add(new PlayerContainer(
					new PlayerState(i, inters.get(i)
							.getUname(), teams
							.get(i)), inters.get(i)));
		}

		dealDeck();

		sendGameState();
		sendGameStart();

		mainGameLoop();
	}

	private void dealDeck() {
		List<Card> deck = Util.deck();
		Collections.shuffle(deck);
		int numplayers = gs.players.size();
		for (int i = 0; i < deck.size(); i++) {
			Card c = deck.get(i);
			gs.players.get(i % numplayers).s.hand.add(c);
		}
	}

	private void sendGameState() {
		/* the data to send to other players about each other */
		List<OtherPlayerData> others = new ArrayList<OtherPlayerData>();
		for (int i = 0; i < gs.players.size(); i++) {
			PlayerState ps = gs.players.get(i).s;
			others.add(new OtherPlayerData(i, ps.uname, ps.team,
					ps.hand.getNumCards()));
		}

		for (int i = 0; i < gs.players.size(); i++) {
			PlayerKnowledge pk = new PlayerKnowledge(
					gs.players.get(i).s, gs.declared,
					others);

			gs.players.get(i).i.insertMessage(pk);
		}
	}

	private void sendGameStart() {
		for (PlayerContainer pc : gs.players) {
			pc.i.insertMessage(new GameStart());
		}
	}

	private void mainGameLoop() {
		while (gs.running) {
			while (sq.isEmpty()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					/*
					 * ignore this exception, it means a
					 * message was added
					 */
				}
			}

			/*
			 * there are no other consumers of this queue so we can
			 * safely dequeue
			 */
			ServerMessage m = sq.poll();
			if (m == null) {
				log("attempted to dequeue from server queue and found it empty");
			}
		}
	}

	/**
	 * Asynchronously adds a message to be processed by the server
	 */
	public void insertMessage(ServerMessage m) {
		sq.add(m);
		t.interrupt();
	}

	private class GameState {
		/**
		 * A list of fish.events that have occurred this far into the
		 * game, such
		 * as question asked and declarations
		 */
		private List<Event> events;

		/**
		 * The set of half suits that have already been declared, and
		 * the players
		 * that declared them
		 */
		private Map<Integer, Integer> declared;

		/**
		 * The containers for the players of the games
		 */
		private List<PlayerContainer> players;

		/**
		 * Indicates whether the game is still running
		 */
		private boolean running;

		private GameState() {
			events = new ArrayList<Event>();
			declared = new HashMap<Integer, Integer>();
			players = new ArrayList<GameController.PlayerContainer>();
			running = false;
		}
	}

	private class PlayerContainer {
		PlayerState s;
		PlayerInterface i;

		public PlayerContainer(PlayerState s, PlayerInterface i) {
			super();
			this.s = s;
			this.i = i;
		}
	}
}
