package fish.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fish.Event;
import fish.Team;
import fish.Util;

/**
 * The object controlling the game flow. It shall communicate with player
 * objects via an interface provided by the server.
 * 
 */
public class GameController {
	/**
	 * A list of events that have occurred this far into the game, such as
	 * question asked and declarations
	 */
	private List<Event> events;

	/**
	 * The set of halfsuits that have already been declared, and the players
	 * that declared them
	 */
	private HashMap<Integer, Integer> declared;

	/**
	 * The containers for the players of the games
	 */
	private List<PlayerContainer> players;

	public void startGame(List<PlayerInterface> inters) {
		if (!Util.validPlayerNum(inters.size()))
			throw new IllegalArgumentException(
					"Invalid number of players: "
							+ inters.size());
		List<Team> teams = new ArrayList<Team>();
		for (int i = 0; i < inters.size(); i++) {
			teams.add(i < inters.size() / 2 ? Team.BLK : Team.RED);
		}
		Collections.shuffle(teams);

		for (int i = 0; i < inters.size(); i++) {
			players.add(new PlayerContainer(new PlayerState(i,
					teams.get(i)), inters.get(i)));
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
