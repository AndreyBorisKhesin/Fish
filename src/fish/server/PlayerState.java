package fish.server;

import java.util.HashSet;
import java.util.Set;

import fish.Hand;
import fish.Team;

/**
 * Holds the set of information for one player including cards held, and suits
 * taken.
 * 
 */
class PlayerState {
	int idx;
	Team t;
	Hand hand;
	Set<Integer> tricks;

	PlayerState(int idx, Team t) {
		this.idx = idx;
		this.t = t;
		hand = new Hand();
		tricks = new HashSet<Integer>();
	}
}
