package fish.server;

import java.util.ArrayList;
import java.util.List;

import fish.Hand;
import fish.Team;

/**
 * Holds the set of information for one player including cards held, and suits
 * taken.
 */
class PlayerState {
	int id;
	Team team;
	Hand hand;
	List tricks;

	PlayerState(int id, Team team) {
		this.id = id;
		this.team = team;
		hand = new Hand();
		tricks = new ArrayList();
	}
}
