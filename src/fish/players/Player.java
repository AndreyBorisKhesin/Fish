package fish.players;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fish.Declaration;
import fish.Hand;
import fish.Question;
import fish.Team;
import fish.server.OtherPlayerData;
import fish.server.PlayerState;

/**
 * An interface for all Humans and AI's playing the game.
 */
public abstract class Player {
	protected Hand hand;

	protected Team team;

	protected String name;

	protected int id;

	protected Set<Integer> tricks;

	public String getName() {
		return name;
	}

	public abstract void updateGameState(PlayerState p,
			List<OtherPlayerData> others,
			Map<Integer, Team> tricks, int turn, Declaration dec);

	public abstract void questionAsked(Question q);

	public abstract void questionResponse(Question q, boolean works);

	public abstract void decStarted(Declaration d);

	public abstract void decUpdated(Declaration d);

	public abstract void decEnded(Declaration d, int[] locs,
			boolean succeeded);
}
