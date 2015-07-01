package fish.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fish.Card;
import fish.Declaration;
import fish.Hand;
import fish.Question;
import fish.Team;
import fish.Util;
import fish.client.test.GameTest;
import fish.server.messages.MDecEnded;
import fish.server.messages.MDecStart;
import fish.server.messages.MDecUpdate;
import fish.server.messages.MQuestion;
import fish.server.messages.MStartGame;
import fish.server.messages.PMGameState;
import fish.server.messages.PMResponse;
import fish.server.messages.ServerMessage;
import fish.server.playerinterface.PlayerInterface;

/**
 * The object controlling the game flow. It shall communicate with player
 * objects via an interface provided by the server.
 * 
 */
public class GameController implements Controller {

	public static final int RESPONSE_WAIT = 3000;

	/**
	 * The server that the clients are connected to
	 */
	private Server s;

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
	 * Begins the game with the given players.
	 * 
	 * @param s The server being used for communication with players.
	 */
	@Override
	public void enter(Server s) {
		if (!Util.validPlayerNum(s.clients.size()))
			throw new IllegalArgumentException(
					"Invalid number of players: "
							+ s.clients.size());
		if (Thread.currentThread() != s.t) {
			throw new IllegalArgumentException(
					"Server passed is running in a different thread from this.");
		}

		this.s = s;
		initGame(s.clients);
	}

	private void initGame(List<PlayerInterface> inters) {
		List<Integer> seats = new ArrayList<>();
		for (int i = 0; i < inters.size(); i++) {
			seats.add(i);
		}

		Collections.shuffle(seats, ServerUtil.rand);

		for (int i = 0; i < inters.size(); i++) {
			gs.players.add(new PlayerContainer(
					new PlayerState(
							i,
							seats.get(i),
							inters.get(i)
									.getUname(),
							seats.get(i) % 2 == 0 ? Team.RED
									: Team.BLU,
							new Hand()), inters
							.get(i)));
		}

		dealDeck();

		gs.turn = ServerUtil.rand.nextInt(s.clients.size());
		// FIXME: remove this
		if (GameTest.CHEATING)
			gs.turn = 0;

		sendGameState();
		sendGameStart();
	}

	private void dealDeck() {
		List<Card> deck = Util.deck();
		Collections.shuffle(deck, ServerUtil.rand);
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
			others.add(new OtherPlayerData(i, ps.seat, ps.name,
					ps.team, ps.hand.getNumCards()));
		}

		for (int i = 0; i < gs.players.size(); i++) {
			PlayerState old = gs.players.get(i).s;
			PlayerState ps = new PlayerState(old.id, old.seat,
					old.name, old.team, new Hand(
							old.hand.getCards()));
			PMGameState pk = new PMGameState(ps, gs.declared,
					others, gs.turn);

			// System.out.println(pk);

			gs.players.get(i).i.insertMessage(pk);
		}
	}

	private void sendGameStart() {
		s.broadcast(new MStartGame());
	}

	@Override
	public void handleMessage(ServerMessage sm) {
		System.out.println("Message received: " + sm);

		switch (sm.smType()) {
		case Q_ASKED:
			questionAsked((MQuestion) sm);
			break;
		case DEC_STARTED:
			decStarted((MDecStart) sm);
			break;
		case DEC_UPDATE:
			decUpdate((MDecUpdate) sm);
			break;
		case DEC_ENDED:
			decEnded((MDecEnded) sm);
			break;
		default:
			Log.log("Message invalid for this mode: " + sm);
			break;
		}
	}

	private void questionAsked(MQuestion sm) {
		/* make sure it comes from the correct person */
		if (sm.id != gs.turn) {
			return;
		}
		/* make sure they're asking the other team */
		if (gs.players.get(sm.id).s.team == gs.players.get(sm.q.dest).s.team) {
			return;
		}
		/* make sure they have a card in the half suit */
		if (gs.players.get(sm.id).s.hand.getSuit(sm.q.c.suit).size() == 0) {
			return;
		}
		/* make sure no one's declaring anything */
		if (gs.dec != null) {
			return;
		}
		/* first we tell everyone the question was asked */
		addEvent(s.getUname(sm.id) + " asked " + s.getUname(sm.q.dest)
				+ " for the " + sm.q.c.humanRep());

		s.broadcast(sm);

		/* then we wait for a bit */
		try {
			Thread.sleep(RESPONSE_WAIT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/* now we resolve the question and send the result */
		Question q = sm.q;
		boolean res = gs.players.get(q.dest).s.hand.contains(q.c);
		if (res) {
			addEvent(s.getUname(sm.q.dest) + " gives the "
					+ q.c.humanRep() + " to "
					+ s.getUname(sm.id));
			gs.players.get(sm.id).s.hand.add(q.c);
			gs.players.get(q.dest).s.hand.remove(q.c);
		} else {
			addEvent(s.getUname(sm.q.dest) + " does not have the "
					+ q.c.humanRep());
			gs.turn = q.dest;
		}

		checkEndgameState();

		s.broadcast(new PMResponse(q, res));
		sendGameState();
	}

	private void decStarted(MDecStart sm) {
		/* make sure we aren't interrupting another declaration */
		if (gs.dec != null) {
			return;
		}
		/* make sure the declarer is in the game */
		if (gs.players.get(sm.id).s.hand.getNumCards() == 0) {
			return;
		}
		/* make sure they aren't trying to cheat someone else */
		if (sm.id != sm.d.source) {
			return;
		}

		addEvent(s.getUname(sm.id) + " started declaring "
				+ Util.suitHumanRep(sm.d.suit));

		gs.dec = sm.d;
		gs.dec.locs = new int[6];
		for (int i = 0; i < 6; i++) {
			gs.dec.locs[i] = -1;
		}

		s.broadcast(sm);
	}

	private void decUpdate(MDecUpdate sm) {
		/* make sure we have a dec that we are updating */
		if (gs.dec == null) {
			return;
		}
		if (sm.id != gs.dec.source) {
			return;
		}
		if (sm.d.suit != gs.dec.suit) {
			return;
		}
		/* there's nothing to do in this case */
		if (sm.d.locs == null) {
			return;
		}

		boolean dirty = false;
		for (int i = 0; i < 6; i++) {
			if (gs.dec.locs[i] == -1 && sm.d.locs[i] != -1) {
				/* the players must be on the same team */
				if (gs.players.get(sm.d.locs[i]).s.team != gs.players
						.get(sm.d.source).s.team) {
					continue;
				}
				gs.dec.locs[i] = sm.d.locs[i];
				dirty = true;
			}
		}

		if (dirty) {
			s.broadcast(new MDecUpdate(gs.dec));
			addEvent(s.getUname(sm.id) + " updated declaration to "
					+ gs.dec);
		}
	}

	private void decEnded(MDecEnded sm) {
		/* make sure we have a dec that we are ending */
		if (gs.dec == null) {
			return;
		}
		if (sm.id != gs.dec.source) {
			return;
		}
		if (sm.d.suit != gs.dec.suit) {
			return;
		}

		decUpdate(new MDecUpdate(sm.d));

		/* make sure they are all assigned */
		boolean done = Arrays.stream(gs.dec.locs).boxed()
				.map(x -> x.intValue() != -1)
				.reduce(Boolean::logicalAnd).get();

		if (!done) {
			/* they are not done the declaration */
			return;
		}

		/* now we test whether or not they're right */
		int[] reallocs = new int[6];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < gs.players.size(); j++) {
				if (gs.players.get(j).s.hand.contains(new Card(
						sm.d.suit, i))) {
					reallocs[i] = j;
					continue;
				}
			}
		}

		boolean correct = true;
		for (int i = 0; i < 6; i++) {
			if (gs.dec.locs[i] != reallocs[i]) {
				correct = false;
			}
		}

		if (correct) {
			gs.declared.put(gs.dec.suit,
					gs.players.get(gs.dec.source).s.team);
		} else {
			gs.declared.put(gs.dec.suit, gs.players
					.get(gs.dec.source).s.team.other());
		}

		for (int i = 0; i < 6; i++) {
			gs.players.get(reallocs[i]).s.hand.remove(new Card(
					gs.dec.suit, i));
		}

		checkEndgameState();
		if (gs.turn != gs.players.size()
				&& gs.players.get(gs.turn).s.hand.getNumCards() == 0) {

			Team t = gs.players.get(gs.turn).s.team;

			while (gs.players.get(gs.turn).s.team != t
					|| gs.players.get(gs.turn).s.hand
							.getNumCards() == 0) {
				gs.turn++;
			}
		}

		s.broadcast(new MDecEnded(gs.dec, reallocs, correct));
		sendGameState();
	}

	/**
	 * Checks if the game has reached the endpoint, i.e. when there is only
	 * one team remaining
	 */
	private void checkEndgameState() {
		Team remaining = null;
		for (int i = 0; i < gs.players.size(); i++) {
			if (gs.players.get(i).s.hand.getNumCards() != 0) {
				if (remaining == null) {
					remaining = gs.players.get(i).s.team;
				} else {
					if (gs.players.get(i).s.team != remaining) {
						/*
						 * players from both teams are
						 * in, continue
						 */
						return;
					}
				}
			}
		}

		/*
		 * we only have one team in the game, so no one can ask
		 * questions
		 */
		gs.turn = gs.players.size();
	}

	@Override
	public void exit() {
		// TODO: implement
	}

	private void addEvent(String s) {
		gs.events.add(new Event(s));
		Log.log(s);
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
		private Map<Integer, Team> declared;

		/**
		 * The containers for the players of the games
		 */
		private List<PlayerContainer> players;

		/**
		 * Whose turn it is
		 */
		private int turn;

		/**
		 * The current declaration
		 */
		private Declaration dec;

		private GameState() {
			events = new ArrayList<Event>();
			declared = new HashMap<Integer, Team>();
			players = new ArrayList<GameController.PlayerContainer>();
			turn = 0;
			dec = null;
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
