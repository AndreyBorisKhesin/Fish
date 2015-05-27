package fish.server;

import fish.Team;

/**
 * Defines the information about other players given to each player
 */
public class OtherPlayerData {
	public final Team t;
	public final int numCards;
	
	public OtherPlayerData(Team t, int numCards) {
		super();
		this.t = t;
		this.numCards = numCards;
	}
}
