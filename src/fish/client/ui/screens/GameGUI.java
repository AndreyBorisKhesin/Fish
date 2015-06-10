package fish.client.ui.screens;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fish.Card;
import fish.client.ui.FishGUI;
import fish.client.ui.Resources;
import fish.players.Human;

public class GameGUI implements GUIScreen {

	private FishGUI gui;

	private Human p;

	public GameGUI(FishGUI gui) {
		this.gui = gui;
	}

	/**
	 * Assigns this game gui to the given player
	 * Since the dependency injection has to go both ways, we do this after
	 * constructing the player object using this
	 * 
	 * @param p The human to which we are attaching
	 */
	public void setPlayer(Human p) {
		this.p = p;
	}

	@Override
	public void paintFrame(Graphics2D g, int w, int h) {
		if (p == null) {
			return;
		}

		/* draw your hand */
		if(p.p != null && p.p.hand != null) {
			/* amount the card drawing is scaled by */
			double cardScale = 1.9;
			int handwidth = (int) (((p.p.hand.getNumCards() - 1) * 12 + 71) * cardScale);
			AffineTransform transform = new AffineTransform();

			transform.translate(w / 2 - handwidth / 2, h - 20);
			transform.scale(cardScale, cardScale);

			for (Card c : p.p.hand.getCardsSorted()) {
				g.drawImage(Resources.CARD_IMGS.get(c),
						transform, null);
				transform.translate(12, 0);
			}
		}
	}

	public void updated() {
		gui.repaint();
	}

}
