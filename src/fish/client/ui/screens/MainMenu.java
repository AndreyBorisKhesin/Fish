package fish.client.ui.screens;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.List;

import fish.Card;
import fish.Team;
import fish.Util;
import fish.client.ClientMaster;
import fish.client.ui.FishGUI;
import fish.client.ui.Resources;
import fish.client.ui.elements.Button;
import fish.client.ui.elements.MainMenuButton;

/**
 * The interface used to allow the player to select game mode, etc.
 *
 */
public class MainMenu implements GUIScreen {

	private MainMenuButton[] buttons;

	private FishGUI g;

	private List<Card> deck;

	public MainMenu(ClientMaster c, FishGUI g) {
		initButtons(c);
		this.g = g;
		this.deck = Util.deck();
	}

	private void initButtons(ClientMaster c) {
		buttons = new MainMenuButton[] {
				new MainMenuButton(20, 530, "Single Player",
						c::localGame),
				new MainMenuButton(335, 530, "Online Game",
						c::onlineGame),
				new MainMenuButton(650, 530, "Settings",
						c::settings),
				new MainMenuButton(965, 530, "Quit", c::quit) };
	}

	@Override
	public void paintFrame(Graphics2D g, int w, int h) {
		/* draw the title */
		{
			g.setFont(Resources.MENU_FONT);
			FontMetrics fm = g.getFontMetrics();
			String F = "F";
			String I = "I";
			String S = "S";
			String H = "H";
			/* draw it backward for favourable overlapping */
			g.setColor(Team.BLU.getColor());
			g.drawString(H, w / 2 - fm.stringWidth(F + I + S + H)
					/ 2 + fm.stringWidth(F + I + S),
					10 + fm.getAscent());
			g.setColor(Team.RED.getColor());
			g.drawString(S, w / 2 - fm.stringWidth(F + I + S + H)
					/ 2 + fm.stringWidth(F + I),
					10 + fm.getAscent());
			g.setColor(Team.BLU.getColor());
			g.drawString(I, w / 2 - fm.stringWidth(F + I + S + H)
					/ 2 + fm.stringWidth(F),
					10 + fm.getAscent());
			g.setColor(Team.RED.getColor());
			g.drawString(F, w / 2 - fm.stringWidth(F + I + S + H)
					/ 2, 10 + fm.getAscent());
		}
		/* draw the buttons */
		{
			for (Button b : buttons) {
				b.draw(g);
			}
		}
		/* draw the deck */
		{
			double s = 1.9;
			AffineTransform trans = new AffineTransform();
			trans.translate(36, 250);
			trans.scale(s, s);
			for (int i = 0; i < deck.size(); i++) {
				g.drawImage(Resources.CARD_IMGS
						.get(deck.get(i)),
						trans, null);
				trans.translate(12, 0);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		for (Button b : buttons) {
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				b.mouseLeftClick(x, y, g);
				break;
			case MouseEvent.BUTTON2:
				b.mouseRightClick(x, y, g);
				break;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		for (Button b : buttons) {
			b.mouseMoved(x, y, g);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}
}
