package fish.server;

import java.util.HashSet;

import fish.Hand;
import fish.Team;

/**
 * Holds the set of information for one player including cards held, and suits
 * taken.
 */
public class PlayerState {

	/**
	 * The number of the Player.
	 */
	public final int id;

	/**
	 * The username of the player.
	 */
	public final String uname;

	/**
	 * The team of the Player.
	 */
	public final Team team;

	/**
	 * The Hand of Cards that the Player is holding.
	 */
	public final Hand hand;

	/**
	 * The set of suits that the Player has claimed.
	 */
	public final HashSet<Integer> tricks;

	/**
	 * PlayerState constructor accepting an ID and a Team.
	 *
	 * @param id The ID of the Player.
	 * @param team The Team of the Player.
	 */
	PlayerState(int id, String uname, Team team) {
		this.id = id;
		this.uname = uname;
		this.team = team;
		hand = new Hand();
		tricks = new HashSet<Integer>();
	}
}
