package fish.players;

import java.util.List;
import java.util.Map;

import fish.Card;
import fish.Declaration;
import fish.Question;
import fish.Team;
import fish.client.ui.screens.GameGUI;
import fish.server.OtherPlayerData;
import fish.server.PlayerState;

/**
 * A Player who is controlled by a Human.
 */
public class Human extends Player {

	private GameGUI gui;

	public Human(GameGUI gui, String name) {
		this.gui = gui;
		this.name = name;
	}

	public void declare(int suit) {
		pi.sendDecStart(new Declaration(id, suit));
	}

	@Override
	public void updateGameState(PlayerState p,
			List<OtherPlayerData> others,
			Map<Integer, Team> tricks, int turn, Declaration dec) {
		super.updateGameState(p, others, tricks, turn, dec);

		gui.updated();
	}

	@Override
	public void questionAsked(Question q) {
		gui.question(q);
	}

	@Override
	public void questionResponse(Question q, boolean works) {
		gui.response(works);
	}

	@Override
	public void decStarted(Declaration d) {
		this.dec = d;
		gui.declaration(d);
	}

	@Override
	public void decUpdated(Declaration d) {
		this.dec = d;
		if (d.source == this.id) {
			/*
			 * this is a different case, we should probably do
			 * nothing here
			 */
		} else {
			/* update the display of the declaration */
		}
	}

	@Override
	public void decEnded(Declaration d, int[] locs, boolean succeeded) {
		this.dec = d;

		/*
		 * we need to temporarily show the declaration before sweeping
		 * it away
		 */
	}

	/**
	 * Once this is received we should move on to the ready-ing up screen
	 */
	@Override
	public void connected() {

	}
}
