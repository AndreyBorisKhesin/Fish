package fish.client.ui.screens;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fish.Card;
import fish.client.ui.FishGUI;
import fish.client.ui.Resources;
import fish.players.Human;
import fish.server.OtherPlayerData;

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
		int gameWidth = w - 250;

		/* if the player is null or the hand is null we aren't ready yet */
		if (p == null || p.hand == null) {
			return;
		}

		/* draw your hand */
		{
			/* amount the card drawing is scaled by */
			double cardScale = 2.5;
			int handwidth = (int) (((p.hand.getNumCards() - 1) * 12 + 71) * cardScale);
			AffineTransform transform = new AffineTransform();

			transform.translate(gameWidth / 2 - handwidth / 2, h);
			transform.scale(cardScale, cardScale);
			/* the size of the cards in pixels */
			transform.translate(0, -96 / 2);

			int handheight = (int) ((96 / 2) * cardScale);

			/* draw an outline around the cards */
			g.setColor(p.team.getColor());
			g.fillRoundRect(gameWidth / 2 - handwidth / 2 - 20, h
					- handheight - 20, handwidth + 20 * 2,
					handheight + 20 * 2, 20, 20);

			for (Card c : p.hand.getCardsSorted()) {
				g.drawImage(Resources.CARD_IMGS.get(c),
						transform, null);
				transform.translate(12, 0);
			}

			/* draw your name */
			g.setFont(Resources.GAME_FONT.deriveFont(40f));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(Color.BLACK);
			g.drawString(p.name, gameWidth / 2 - handwidth / 2, h
					- handheight - 20 - fm.getDescent() + 1);
		}

		/* create a list containing players at the table in sorted order */
		List<OtherPlayerData> table = new ArrayList<>();
		table.addAll(p.others);
		Collections.sort(table,
				((a, b) -> Integer.compare(a.seat, b.seat)));
		/* draw opponents, cycling around from our seat */
		/* lidx represents position in the layout list */
		for (int i = (p.seat + 1) % table.size(), lidx = 0; i != p.seat; i = (i + 1)
				% table.size(), lidx++) {
			OtherPlayerData d = table.get(i);
			Layout l = getLayoutSet(table.size())[lidx];
			double scale = getLayoutScale(table.size());

			int x = (int) (l.x * gameWidth);
			int y = (int) (l.y * h);

			/* draw the card */
			{
				AffineTransform xform = new AffineTransform();
				/* translate to drawing location */
				xform.translate(x, y);
				/* rotate */
				xform.quadrantRotate(-l.rot);
				/* scale card */
				xform.scale(scale, scale);
				/* translate to centre of card */
				xform.translate(-71 / 2, -96 / 2);

				g.drawImage(Resources.CARD_BACKS.get(d.t),
						xform, null);
			}
			/* draw the number on the card */
			{
				TextLayout num = new TextLayout(
						"" + d.numCards,
						Resources.GAME_FONT
								.deriveFont(40f),
						g.getFontRenderContext());

				Shape s = num.getOutline(AffineTransform
						.getScaleInstance(scale, scale));

				Stroke orig = g.getStroke();
				g.setStroke(new BasicStroke(2));

				AffineTransform xform = AffineTransform
						.getTranslateInstance(x, y);
				switch (l.rot) {
				case 1:
					xform.translate((int) (20 * scale), -5);
					break;
				case 2:
					xform.translate(0, (int) (20 * scale));
					break;
				case 3:
					xform.translate((int) (-20 * scale), -5);
					break;
				}
				/* translate by the font size */
				FontMetrics fm = g
						.getFontMetrics(Resources.GAME_FONT
								.deriveFont(40f));
				xform.translate(scale
						* (-fm.stringWidth(""
								+ d.numCards) / 2),
						scale * (fm.getAscent() / 2));
				g.setTransform(xform);
				g.setColor(Color.WHITE);
				g.fill(s);
				g.setColor(Color.BLACK);
				g.draw(s);
				g.setTransform(new AffineTransform());

				g.setStroke(orig);
			}
			/* draw the name of the player */
			{
				g.setFont(Resources.GAME_FONT.deriveFont(10f));
				g.setColor(Color.BLACK);
				FontMetrics fm = g.getFontMetrics();

				AffineTransform xform = AffineTransform
						.getTranslateInstance(x, y);
				xform.scale(scale, scale);

				int tx = 0;
				int ty = 0;
				switch (l.rot) {
				case 1:
					tx = 1;
					ty = -37;
					break;
				case 2:
					tx = -36;
					ty = 48 + fm.getAscent();
					break;
				case 3:
					tx = -48;
					ty = -36;
					break;
				}

				xform.translate(tx, ty);

				g.setTransform(xform);
				g.drawString(d.uname, 0, 0);
				g.setTransform(new AffineTransform());
			}
		}

		g.clearRect(gameWidth, 0, w - gameWidth, h);
	}

	public void updated() {
		gui.repaint();
	}

	/**
	 * Defines the positions and rotations for layouts of other players for
	 * each number of players
	 */
	private static final Layout[][] layouts = new Layout[4][];
	/**
	 * Defines the scale at wich cards should be drawn depending on the
	 * number of other players
	 */
	private static final double[] layout_scales = { 2.5, 2.5, 1.9, 1.5 };

	static {
		layouts[0] = new Layout[] { new Layout(0, 0.5, 1),
				new Layout(0.5, 0, 2), new Layout(1, 0.5, 3) };
		layouts[1] = new Layout[] { new Layout(0, 0.5, 1),
				new Layout(0.2, 0, 2), new Layout(0.5, 0, 2),
				new Layout(0.8, 0, 2), new Layout(1, 0.5, 3) };
		layouts[2] = new Layout[] { new Layout(0, 2 / 3., 1),
				new Layout(0, 1 / 3., 1),
				new Layout(0.25, 0, 2), new Layout(0.5, 0, 2),
				new Layout(0.75, 0, 2),
				new Layout(1, 1 / 3., 3),
				new Layout(1, 2 / 3., 3) };
		layouts[3] = new Layout[] { new Layout(0, 0.75, 1),
				new Layout(0, 0.5, 1), new Layout(0, 0.25, 1),
				new Layout(1 / 6., 0, 2),
				new Layout(2 / 6., 0, 2),
				new Layout(0.5, 0, 2),
				new Layout(4 / 6., 0, 2),
				new Layout(5 / 6., 0, 2),
				new Layout(1, 0.25, 3), new Layout(1, 0.5, 3),
				new Layout(1, 0.75, 3) };
	}

	public static Layout[] getLayoutSet(int numPlayers) {
		int i = 0;
		switch (numPlayers) {
		case 4:
			i = 0;
			break;
		case 6:
			i = 1;
			break;
		case 8:
			i = 2;
			break;
		case 12:
			i = 3;
			break;
		default:
			throw new IllegalArgumentException(
					"Illegal number of players");
		}
		return layouts[i];
	}

	public static double getLayoutScale(int numPlayers) {
		int i = 0;
		switch (numPlayers) {
		case 4:
			i = 0;
			break;
		case 6:
			i = 1;
			break;
		case 8:
			i = 2;
			break;
		case 12:
			i = 3;
			break;
		default:
			throw new IllegalArgumentException(
					"Illegal number of players");
		}
		return layout_scales[i];
	}

	private static class Layout {

		/**
		 * The x and y centre of the card for the layout, and the
		 * orientation in quadrants from upright clockwise
		 */
		private double x, y;
		private int rot;

		public Layout(double x, double y, int rot) {
			this.x = x;
			this.y = y;
			this.rot = rot;
		}
	}
}
