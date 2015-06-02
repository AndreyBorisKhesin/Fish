package fish.server.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fish.server.OtherPlayerData;
import fish.server.PlayerState;

/**
 * Represents all the knowledge about the game state a player receives
 */
public class PMGameState implements PlayerMessage {

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
	
	/**
	 * Whose turn it is
	 */
	public int turn;

	public PMGameState(PlayerState pstate,
			Map<Integer, Integer> tricks,
			List<OtherPlayerData> otherplayers) {
		this.pstate = pstate;
		this.tricks = tricks;
		this.otherplayers = otherplayers;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("Player Message: GAME_STATE\n");
		stringFormat(0).forEach(str::append);
		return str.toString();
	}

	/**
	 * Outputs formatted list of objects representing this game state
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
		{
			out.add(prepend + "\tpstate:\n");
			out.addAll(pstate.stringFormat(depth + 1));
			out.add(prepend + ",\n");
		}
		{
			out.add(prepend + "\ttricks: " + tricks.toString()
					+ ",\n");
		}
		{
			out.add(prepend + "\totherplayers:\n");
			out.add(prepend + "\t[\n");
			for (OtherPlayerData d : otherplayers) {
				out.addAll(d.stringFormat(depth + 1));
				out.add(prepend + ",\n");
			}
			out.add(prepend + "\t],\n");
		}
		out.add(prepend + "}");

		return out;
	}
}
