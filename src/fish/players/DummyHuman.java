package fish.players;

import java.util.List;
import java.util.Map;

import fish.Declaration;
import fish.Question;
import fish.Team;
import fish.client.ui.screens.GameGUI;
import fish.server.OtherPlayerData;
import fish.server.PlayerState;
import fish.server.playerinterface.PlayerInterface;

/**
 * A Player who is controlled by a Human.
 */
public class DummyHuman extends Human {

	private RandAI rai;

	public DummyHuman(GameGUI gui, String name) {
		super(gui, name);

		rai = new RandAI(name);
	}

	@Override
	public void setPlayerInterface(PlayerInterface pi) {
		super.setPlayerInterface(pi);
		rai.setPlayerInterface(pi);
	}

	@Override
	public void updateGameState(PlayerState p,
			List<OtherPlayerData> others,
			Map<Integer, Team> tricks, int turn, Declaration dec) {
		super.updateGameState(p, others, tricks, turn, dec);
		rai.updateGameState(p, others, tricks, turn, dec);
	}

	@Override
	public void questionAsked(Question q) {
		super.questionAsked(q);
		rai.questionAsked(q);
	}

	@Override
	public void questionResponse(Question q, boolean works) {
		super.questionResponse(q, works);
		rai.questionResponse(q, works);
	}

	@Override
	public void decStarted(Declaration d) {
		super.decStarted(d);
		rai.decStarted(d);
	}

	@Override
	public void decUpdated(Declaration d) {
		super.decUpdated(d);
		rai.decUpdated(d);
	}

	@Override
	public void decEnded(Declaration d, int[] locs, boolean succeeded) {
		super.decEnded(d, locs, succeeded);
		rai.decEnded(d, locs, succeeded);
	}

	/**
	 * Once this is received we should move on to the ready-ing up screen
	 */
	@Override
	public void connected() {
		super.connected();
		rai.connected();
	}
}
