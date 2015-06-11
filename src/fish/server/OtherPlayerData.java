package fish.server;

import java.util.ArrayList;
import java.util.List;

import fish.Team;

/**
 * Defines the information about other players given to each player
 */
public class OtherPlayerData {
	public final int id;
	public final int seat;
	public final String uname;
	public final Team t;
	public int numCards;

	public OtherPlayerData(int id, int seat, String uname, Team t, int numCards) {
		this.id = id;
		this.seat = seat;
		this.uname = uname;
		this.t = t;
		this.numCards = numCards;
	}

	/**
	 * Outputs formatted list of objects representing this other player data
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
		out.add(prepend + "\tuname: " + uname + ",\n");
		out.add(prepend + "\tteam: " + t + ",\n");
		out.add(prepend + "\tcards: " + numCards + ",\n");
		out.add(prepend + "}");

		return out;
	}
}
