package fish.players;

import java.util.List;
import java.util.Map;

import fish.Declaration;
import fish.Question;
import fish.Team;
import fish.server.OtherPlayerData;
import fish.server.PlayerState;

/**
 * A Player who is controlled by a Human.
 */
public class Human extends Player {

	@Override
	public void updateGameState(PlayerState p,
			List<OtherPlayerData> others,
			Map<Integer, Team> tricks, int turn, Declaration dec) {
		// TODO implement

	}

	@Override
	public void questionAsked(Question q) {
		// TODO implement

	}

	@Override
	public void questionResponse(Question q, boolean works) {
		// TODO implement

	}

	@Override
	public void decStarted(Declaration d) {
		// TODO implement

	}

	@Override
	public void decUpdated(Declaration d) {
		// TODO implement

	}

	@Override
	public void decEnded(Declaration d, int[] locs, boolean succeeded) {
		// TODO implement

	}

}
