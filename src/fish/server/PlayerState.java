package fish.server;

import java.util.ArrayList;
import java.util.List;

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
	 * The name of the player.
	 */
	public final String name;

	/**
	 * The team of the Player.
	 */
	public final Team team;

	/**
	 * The Hand of Cards that the Player is holding.
	 */
	public final Hand hand;

	/**
	 * PlayerState constructor accepting an ID and a Team.
	 *
	 * @param id The ID of the Player.
	 * @param name The name of the Player.
	 * @param team The Team of the Player.
	 */
	PlayerState(int id, String name, Team team) {
		this.id = id;
		this.name = name;
		this.team = team;
		hand = new Hand();
	}

	/**
	 * Outputs formatted list of objects representing this player state
	 * in JSON format
	 * 
	 * @param depth The number of tabs to prepend
	 * @return A list of string objects representing the output
	 */
	public List<String> stringFormat(int depth) {
		List<String> out = new ArrayList<String>();
		String prepend = new String(new char[depth])
				.replace('\0', '\t');

		out.add(prepend + "{\n");
		out.add(prepend + "\tid: " + id + ",\n");
		out.add(prepend + "\tuname: " + name + ",\n");
		out.add(prepend + "\tteam: " + team + ",\n");
		out.add(prepend + "\thand: " + hand + ",\n");
		out.add(prepend + "}");

		return out;
	}
}
