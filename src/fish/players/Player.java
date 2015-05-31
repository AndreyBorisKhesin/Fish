package fish.players;

import fish.Hand;
import fish.Team;

import java.util.Set;

/**
 * An interface for all Humans and AI's playing the game.
 */
public abstract class Player {
	protected Hand hand;

	protected Team team;

	protected String name;

	protected int id;

	protected Set<Integer> tricks;
}
