package fish.server.playermessages;

import java.util.List;
import java.util.Map;

import fish.server.PlayerState;
import fish.server.OtherPlayerData;

/**
 * Represents all the knowledge about the game state a player receives
 */
public class PlayerKnowledge implements PlayerMessage {
	
	@Override
	public PlayerMessage.PMType getType() {
		return PlayerMessage.PMType.GAME_STATE;
	}
	
	/**
	 * The players state for the player this is being sent to.
	 */
	public PlayerState pstate;
	
	/**
	 * The tricks taken.
	 */
	public Map<Integer, Integer> tricks;
	
	/**
	 * The visible state of other players.
	 */
	public List<OtherPlayerData> otherplayers;

	public PlayerKnowledge(PlayerState pstate, Map<Integer, Integer> tricks,
			List<OtherPlayerData> otherplayers) {
		this.pstate = pstate;
		this.tricks = tricks;
		this.otherplayers = otherplayers;
	}
}
