package fish.server;

import fish.Team;

/**
 * Defines the information about other players given to each player
 */
public class OtherPlayerData {
	public final int id;
	public final String uname;
	public final Team t;
	public int numCards;
	
	public OtherPlayerData(int id, String uname, Team t, int numCards) {
		this.id = id;
		this.uname = uname;
		this.t = t;
		this.numCards = numCards;
	}
}
