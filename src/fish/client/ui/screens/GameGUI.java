package fish.client.ui.screens;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fish.Card;
import fish.Question;
import fish.Team;
import fish.client.ui.FishGUI;
import fish.client.ui.Resources;
import fish.players.Human;
import fish.server.OtherPlayerData;

public class GameGUI implements GUIScreen {

	private FishGUI gui;

	private Human p;

	private DrawMode mode;

	private static enum DrawMode {
		WAIT_FOR_Q, DECLARATION, ASK_QUESTION, QUESTION_RESPONSE,
	}

	private int w, h;
	/* the width of the game screen */
	private int gw;

	/* location of the mouse */
	private int mx = Integer.MAX_VALUE, my = Integer.MAX_VALUE;

	/* state variables for WAIT_FOR_Q */
	private int selection;

	/* state variables for ASK_QUESTION */
	private double cardscale = 1.5;

	private static enum AskMode {
		SUIT, RANK
	}

	private AskMode askmode;

	private boolean[] grayedsuits;

	private int suitselection;
	private int rankselection;

	private int askee;

	public GameGUI(FishGUI gui) {
		this.gui = gui;

		mode = DrawMode.WAIT_FOR_Q;

		selection = -1;
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

	private void switchMode(DrawMode mode) {
		this.mode = mode;
		mouseMoved();
		this.updated();
	}

	private void switchAskMode(AskMode mode) {
		this.askmode = mode;
		mouseMoved();
		this.updated();
	}

	@Override
	public void paintFrame(Graphics2D g, int w, int h) {
		this.w = w;
		this.h = h;
		this.gw = w - 250;

		/* if the player is null or the hand is null we aren't ready yet */
		if (p == null || p.hand == null) {
			return;
		}

		drawBasicGame(g);

		switch (mode) {
		case WAIT_FOR_Q:
			drawWaitForQuestion(g);
			break;
		case ASK_QUESTION:
			drawAskQuestion(g);
			break;
		}

		g.clearRect(gw, 0, w - gw, h);
	}

	/**
	 * Draws the basic parts of the game such as your and others' hands
	 */
	private void drawBasicGame(Graphics2D g) {
		/* draw your hand */
		{
			/* amount the card drawing is scaled by */
			double cardScale = 2.5;
			int handwidth = (int) (((p.hand.getNumCards() - 1) * 12 + 71) * cardScale);
			AffineTransform transform = new AffineTransform();

			transform.translate(gw / 2 - handwidth / 2, h);
			transform.scale(cardScale, cardScale);
			/* the size of the cards in pixels */
			transform.translate(0, -96 / 2);

			int handheight = (int) ((96 / 2) * cardScale);

			/* if it is your turn draw an outline around your hand */
			if (p.turn == p.id) {
				g.setColor(Resources.GLOW);
				g.fillRoundRect(gw / 2 - handwidth / 2 - 40, h
						- handheight - 65,
						handwidth + 80,
						handheight + 40 * 2, 20, 20);
			}

			/* draw an outline around the cards */
			g.setColor(p.team.getColor());
			g.fillRoundRect(gw / 2 - handwidth / 2 - 20, h
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
			g.drawString(p.name, gw / 2 - handwidth / 2, h
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

			int x = (int) (l.x * gw);
			int y = (int) (l.y * h);

			/* draw the card */
			{
				AffineTransform xform = new AffineTransform();
				/* translate to drawing location */
				xform.translate(x, y);
				/* rotate */
				xform.quadrantRotate(l.rot);
				/* scale card */
				xform.scale(scale, scale);
				/* translate to centre of card */
				xform.translate(-71 / 2, -96 / 2);

				/* if they are selected, indicate that */
				if (mode == DrawMode.WAIT_FOR_Q
						&& p.turn == p.id
						&& selection == d.id
						&& d.t != p.team) {
					xform.translate(0, -10);
				}

				g.setTransform(xform);
				/* if it's their turn draw the glow */
				if (d.id == p.turn) {
					g.setColor(Resources.GLOW);
					g.fillRoundRect(-10, -10, 71 + 10 * 2,
							96 + 10 * 2, 10, 10);
				}
				/* render the card back image */
				BufferedImage cardImg = drawCardBack(d.t,
						d.numCards, l.rot);
				g.drawImage(cardImg, AffineTransform
						.getScaleInstance(.1, .1), null);
				g.setTransform(new AffineTransform());
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
					/*
					 * if they are selected, we have to
					 * shift the name too
					 */
					if (mode == DrawMode.WAIT_FOR_Q
							&& p.turn == p.id
							&& selection == d.id
							&& d.t != p.team) {
						ty += 10;
					}
					break;
				case 3:
					tx = -48;
					ty = -37;
					break;
				}

				xform.translate(tx, ty);

				g.setTransform(xform);
				g.drawString(d.uname, 0, 0);
				g.setTransform(new AffineTransform());
			}
		}
	}

	private void drawWaitForQuestion(Graphics2D g) {
		if (p.turn != p.id)
			return;
		String s = "Select a player to ask";

		g.setColor(Color.BLACK);
		g.setFont(Resources.GAME_FONT.deriveFont(30f));
		FontMetrics fm = g.getFontMetrics();
		g.drawString(s, (int) (0.5 * gw - fm.stringWidth(s) / 2),
				(int) (0.5 * h + fm.getAscent() / 2));
	}

	private void drawAskQuestion(Graphics2D g) {
		switch (askmode) {
		case SUIT:
			drawAskSuit(g);
			break;
		case RANK:
			drawAskRank(g);
			break;
		}
	}

	private void drawAskSuit(Graphics2D g) {
		double scale = cardscale;

		String s = "Select a half suit to ask for";

		g.setColor(Color.BLACK);
		g.setFont(Resources.GAME_FONT.deriveFont(30f));
		FontMetrics fm = g.getFontMetrics();
		g.drawString(s,
				(int) (0.5 * gw - fm.stringWidth(s) / 2),
				(int) (0.5 * h - 111 * scale - fm.getDescent() - 10));

		for (int i = 0; i < 8; i++) {
			AffineTransform xform = new AffineTransform();
			xform.translate(0.5 * gw, 0.5 * h - 10);
			xform.scale(scale, scale);
			xform.translate(((i / 2) - 2) * 81,
					((i % 2) - 1) * 106 + 5);

			g.setTransform(xform);
			/* if its selected outline it */
			if (suitselection == i) {
				g.setColor(Resources.GLOW);
				g.fillRoundRect(-10, -10, 91, 116, 10, 10);
			}

			BufferedImage img = (!grayedsuits[i] ? Resources.SUIT_CARDS
					: Resources.SUIT_CARDS_GRAY).get(i);
			g.drawImage(img, 0, 0, null);
			g.setTransform(new AffineTransform());
		}
	}

	private void drawAskRank(Graphics2D g) {
		double scale = cardscale;

		String s = "Select a rank to ask for";

		g.setColor(Color.BLACK);
		g.setFont(Resources.GAME_FONT.deriveFont(30f));
		FontMetrics fm = g.getFontMetrics();
		g.drawString(s,
				(int) (0.5 * gw - fm.stringWidth(s) / 2),
				(int) (0.5 * h - 111 * scale - fm.getDescent() - 10));

		for (int i = 0; i < 6; i++) {
			AffineTransform xform = new AffineTransform();
			xform.translate(0.5 * gw, 0.5 * h - 10);
			xform.scale(scale, scale);
			xform.translate(((i / 2) - 2) * 81,
					((i % 2) - 1) * 106 + 5);
			xform.translate(71 / 2., 0);

			g.setTransform(xform);
			/* if its selected outline it */
			if (rankselection == i) {
				g.setColor(Resources.GLOW);
				g.fillRoundRect(-10, -10, 91, 116, 10, 10);
			}

			BufferedImage img = Resources.CARD_IMGS.get(new Card(
					suitselection, i));
			g.drawImage(img, 0, 0, null);
			g.setTransform(new AffineTransform());
		}
	}

	/**
	 * Buffer used for the function below, we scale it up by 10 so we can
	 * later downsize it to the right size without the text looking blocky
	 */
	private BufferedImage cardBackBuf = new BufferedImage(71 * 10, 96 * 10,
			BufferedImage.TYPE_INT_ARGB);
	private Graphics2D cardBackG = (Graphics2D) cardBackBuf.getGraphics();

	/**
	 * The image return here is reused so it must be drawn before the next
	 * call to this function
	 */
	private BufferedImage drawCardBack(Team t, int numCards, int rot) {
		Graphics2D g = cardBackG;

		g.drawImage(Resources.CARD_BACKS.get(t),
				AffineTransform.getScaleInstance(10, 10), null);

		Font f = Resources.GAME_FONT.deriveFont(40f);
		/* translate by the font size */
		FontMetrics fm = g.getFontMetrics(f);

		AffineTransform xform = new AffineTransform();

		xform.scale(10, 10);
		xform.translate(71 / 2., 27.5);
		xform.quadrantRotate(-rot);
		xform.translate(-fm.stringWidth("" + numCards) / 2,
				fm.getAscent() / 2 - 2);

		TextLayout num = new TextLayout("" + numCards, f,
				g.getFontRenderContext());

		Shape s = num.getOutline(null);

		Stroke orig = g.getStroke();
		g.setStroke(new BasicStroke(2));

		g.setTransform(xform);
		g.setColor(Color.WHITE);
		g.fill(s);
		g.setColor(Color.BLACK);
		g.draw(s);
		g.setTransform(new AffineTransform());

		g.setStroke(orig);

		return cardBackBuf;
	}

	public void updated() {
		gui.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();

		mouseMoved();
	}

	private void mouseMoved() {
		switch (mode) {
		case WAIT_FOR_Q:
			movedWaitForQuestion();
			break;
		case ASK_QUESTION:
			movedAskQuestion();
			break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	private void movedWaitForQuestion() {
		if (p.others == null) {
			return;
		}
		int oldSelection = selection;

		selection = -1;

		double scale = getLayoutScale(p.others.size());
		for (int i = 0; i < getLayoutSet(p.others.size()).length; i++) {
			Layout l = getLayoutSet(p.others.size())[i];
			Point2D src = new Point2D.Double(mx - l.x * gw, my
					- l.y * h);
			AffineTransform xform = AffineTransform
					.getQuadrantRotateInstance(-l.rot);

			Point2D res = xform.transform(src, null);

			if (Math.abs(res.getX()) <= scale * 71 / 2.
					&& res.getY() <= 0
					&& res.getY() >= -48 * scale) {
				int seat = (p.seat + i + 1) % p.others.size();

				for (OtherPlayerData d : p.others) {
					if (d.seat == seat) {
						selection = d.id;
					}
				}
			}
		}

		if (selection != oldSelection) {
			System.out.println("Selected " + selection);
			this.updated();
		}
	}

	private void movedAskQuestion() {
		switch (askmode) {
		case SUIT:
			movedAskSuit();
			break;
		case RANK:
			movedAskRank();
			break;
		}
	}

	private void movedAskSuit() {
		int oldselection = suitselection;

		suitselection = -1;

		for (int i = 0; i < 8; i++) {
			if (grayedsuits[i])
				continue;
			/*
			 * we use the same code as for drawing the cards in the
			 * ask suit function to determine if we are in the right
			 * range
			 */
			AffineTransform xform = new AffineTransform();
			xform.translate(0.5 * gw, 0.5 * h - 10);
			xform.scale(cardscale, cardscale);
			xform.translate(((i / 2) - 2) * 81, ((i % 2) - 1) * 96
					+ ((i % 2) * 2 - 1) * 5);

			Point2D res = null;
			try {
				res = xform.inverseTransform(new Point(mx, my),
						null);
			} catch (NoninvertibleTransformException e) {
				/* this should never happen */
				e.printStackTrace();
			}

			if (res.getX() >= 0 && res.getX() < 71
					&& res.getY() >= 0 && res.getY() < 96) {
				suitselection = i;
			}
		}

		if (oldselection != suitselection) {
			this.updated();
			System.out.println("Selected suit: " + suitselection);
		}
	}

	private void movedAskRank() {
		int oldselection = rankselection;

		rankselection = -1;

		for (int i = 0; i < 6; i++) {
			/*
			 * we use the same code as for drawing the cards in the
			 * ask suit function to determine if we are in the right
			 * range
			 */
			AffineTransform xform = new AffineTransform();
			xform.translate(0.5 * gw, 0.5 * h - 10);
			xform.scale(cardscale, cardscale);
			xform.translate(((i / 2) - 2) * 81, ((i % 2) - 1) * 96
					+ ((i % 2) * 2 - 1) * 5);
			xform.translate(71 / 2., 0);

			Point2D res = null;
			try {
				res = xform.inverseTransform(new Point(mx, my),
						null);
			} catch (NoninvertibleTransformException e) {
				/* this should never happen */
				e.printStackTrace();
			}

			if (res.getX() >= 0 && res.getX() < 71
					&& res.getY() >= 0 && res.getY() < 96) {
				rankselection = i;
			}
		}

		if (oldselection != rankselection) {
			this.updated();
			System.out.println("Selected rank: " + rankselection);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}

		switch (mode) {
		case WAIT_FOR_Q:
			clickWaitForQuestion();
			break;
		case ASK_QUESTION:
			clickAskQuestion();
			break;
		}
	}

	private void clickWaitForQuestion() {
		if (p.turn == p.id && selection != -1) {
			for (OtherPlayerData d : p.others) {
				if (d.id == selection && d.t == p.team) {
					break;
				}
			}

			askee = selection;

			rankselection = -1;
			suitselection = -1;

			this.grayedsuits = new boolean[8];
			for (int i = 0; i < 8; i++) {
				this.grayedsuits[i] = p.hand.getSuit(i).size() == 0;
			}

			askmode = AskMode.SUIT;
			switchMode(DrawMode.ASK_QUESTION);
		}
	}

	private void clickAskQuestion() {
		switch (askmode) {
		case SUIT:
			if (suitselection == -1) {
				switchMode(DrawMode.WAIT_FOR_Q);
			} else {
				switchAskMode(AskMode.RANK);
			}

			break;
		case RANK:
			if (rankselection == -1) {
				switchMode(DrawMode.WAIT_FOR_Q);
			} else {
				/*
				 * We construct and send the question. In the
				 * meantime we return to waiting for questions,
				 * but make it appear as if it is no longer our
				 * turn so we can't screw anything up while we
				 * wait for a response.
				 */
				p.pi.sendQuestion(new Question(p.id, selection,
						new Card(suitselection,
								rankselection)));

				this.selection = -1;
				p.turn = -1;
				switchMode(DrawMode.WAIT_FOR_Q);
			}

			break;
		}
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
