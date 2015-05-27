package fish.server.playermessages;

import java.util.List;
import java.util.Map;

import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState;

import fish.server.OtherPlayerData;

public class GameState {
	/**
	 * The players state for the player this is being sent to
	 */
	public PlayerState pstate;
	
	/**
	 * The tricks taken
	 */
	public Map<Integer, Integer> tricks;
	
	/**
	 * The visible state of other players
	 */
	public List<OtherPlayerData> otherplayers;

	public GameState(PlayerState pstate, Map<Integer, Integer> tricks,
			List<OtherPlayerData> otherplayers) {
		super();
		this.pstate = pstate;
		this.tricks = tricks;
		this.otherplayers = otherplayers;
	}
}
