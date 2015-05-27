package fish.server;

import java.util.HashSet;
import java.util.List;

import fish.Event;
import fish.Util;

/**
 * The object controlling the game flow. It shall communicate with player
 * objects via an interface provided by the server.
 * 
 */
public class GameController {
	List<Event> events;

	HashSet<Integer> declared;

	List<PlayerContainer> players;

	public void startGame() {
		if (!Util.validPlayerNum(players.size()))
			throw new IllegalArgumentException("Invalid number of players: "
					+ players.size());
	}

	private class PlayerContainer {
		PlayerState s;
		PlayerInterface i;
	}
}
