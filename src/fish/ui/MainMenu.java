package fish.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import fish.Team;

/**
 * The interface used to allow the player to select game mode, etc.
 *
 */
public class MainMenu implements Painter {

	int i = 0;

	@Override
	public void paintFrame(Graphics2D g, int w, int h) {
		/* draw the title */
		{
			g.setFont(Resources.MENU_FONT);
			FontMetrics fm = g.getFontMetrics();
			String s = "FI";
			String e = "SH";
			g.setColor(Team.BLU.getColor());
			g.drawString(s, w / 2 - fm.stringWidth(s + e) / 2,
					10 + fm.getAscent());
			g.setColor(Team.RED.getColor());
			g.drawString(e,
					w / 2 - fm.stringWidth(s + e) / 2
							+ fm.stringWidth(s),
					10 + fm.getAscent());
		}
	}

}
