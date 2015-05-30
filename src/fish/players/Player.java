package fish.players;

import fish.Hand;
import fish.Team;

import java.util.Set;

/**
 * An interface for all Humans and AI's playing the game.
 */
public abstract class Player {
	private Hand hand;

	private Team team;

	private String name;

	private int id;

	private Set<Integer> tricks;
}
