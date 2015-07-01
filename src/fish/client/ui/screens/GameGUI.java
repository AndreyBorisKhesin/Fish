package fish.client.ui.screens;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.IntToDoubleFunction;

import fish.Card;
import fish.Declaration;
import fish.Question;
import fish.Team;
import fish.client.ui.FishGUI;
import fish.client.ui.GameLog;
import fish.client.ui.Resources;
import fish.client.ui.elements.Button;
import fish.players.Human;
import fish.server.OtherPlayerData;
import fish.server.ServerUtil;

public class GameGUI extends GUIScreen {

	private FishGUI gui;

	private Human p;

	private DrawMode mode;

	private static enum DrawMode {
		WAIT_FOR_Q, DECLARATION, ASK_QUESTION, QUESTION_ASKED, QUESTION_RESPONSE,
	}

	private int w, h;

	/* the width of the game screen */
	private int gw;
	/* the width of the sidebar */
	private int sw;

	/* sidebar buttons */
	private List<Button> buttons;

	/* location of the mouse */
	private int mx = Integer.MAX_VALUE, my = Integer.MAX_VALUE;

	/* the log showing game actions */
	private GameLog glog;

	/* if this is true we should render the prompt to choose a suit */
	private boolean declarePressed;

	/* state variables for WAIT_FOR_Q */
	private int selection;

	/* state variables for ASK_QUESTION/DECLARATION */
	private double cardscale = 1.5;

	private boolean[] grayedsuits;

	private int suitselection;
	private int rankselection;

	/* state variables for QUESTION_ASKED */
	private Question question;

	/* state variables for QUESTION_RESPONSE */
	private boolean qcorrect;

	/* parameter for shifting the card into place */
	/* 0->0.5: the back of the card with the old number and colour */
	/* 0.5->1: the front of the card, ending at the centre of the screen */
	/* 1->2 : wait for everyone to see it */
	/* 2->2.5: the front of the card transitioning to new position */
	/*
	 * 2.5->3: the back of the card with the new number and colour, sliding
	 * into place
	 */
	private double t;

	/* the affine transform matrices we are interpolating between */
	private double[] startX, middleX, endX;
	/* the differences we are interpolating through */
	private double[] diff1, diff2;

	/* the images we are drawing */
	private BufferedImage startI, middleI, endI;

	private static enum AskMode {
		SUIT, RANK
	}

	private AskMode askmode;

	public GameGUI(FishGUI gui) {
		super(gui);
		this.gui = gui;
		setSize(gui.bufferDimensions());

		mode = DrawMode.WAIT_FOR_Q;

		selection = -1;

		this.glog = new GameLog(sw - 5, this.h / 2);

		initButtons();
	}

	private void setSize(Dimension d) {
		this.w = d.width;
		this.h = d.height;
		this.sw = 245;
		this.gw = w - sw - 5;
	}

	private void initButtons() {
		this.buttons = new ArrayList<Button>();

		Button startDec = new Button(w - sw + 5, 120, sw - 10, 70,
				"Declare", Resources.font(30),
				this::declareButton);

		this.buttons.add(startDec);
	}

	private void declareButton() {
		System.out.println("DECLARING");

		/*
		 * when choosing a suit to declare, the game should continue as
		 * no one else will be stopped yet, so this is not a distinct
		 * mode
		 */
		declarePressed = true;
		suitselection = -1;
		this.grayedsuits = new boolean[8];

		updated();
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

	public void question(Question q) {
		this.question = q;

		{
			/* construct a string representing this message */

			OtherPlayerData asker = p.others.get(q.source);
			OtherPlayerData askee = p.others.get(q.dest);

			String msg = asker.t.delim() + asker.uname + " asked "
					+ askee.t.delim() + askee.uname
					+ " for the " + q.c.humanRep();

			/* add this to the log */
			glog.addString(msg);
		}

		this.switchMode(DrawMode.QUESTION_ASKED);

		/*
		 * we need to hang here to make sure the player sees the
		 * question
		 */
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void response(boolean correct) {
		this.mode = DrawMode.QUESTION_RESPONSE;
		this.qcorrect = correct;

		{
			/* add appropriate string to the log */
			if (qcorrect) {
				OtherPlayerData asker = p.others
						.get(question.source);
				OtherPlayerData askee = p.others
						.get(question.dest);

				String msg = askee.t.delim() + askee.uname
						+ " gave " + asker.t.delim()
						+ asker.uname + " the "
						+ question.c.humanRep();

				glog.addString(msg);
			} else {
				OtherPlayerData askee = p.others
						.get(question.dest);

				String msg = askee.t.delim() + askee.uname
						+ " does not have the "
						+ question.c.humanRep();
				glog.addString(msg);

				msg = askee.t.delim() + askee.uname + "'s turn";
				glog.addString(msg);
			}
		}

		renderResponse(correct);
	}

	private void renderResponse(boolean correct) {
		double step = 0.025;
		double time = 3.;

		if (correct) {
			setupCardTransfer();
		}

		gui.repaint();

		t = 0;
		while (t < 3 - 1e-9) {
			long start = System.currentTimeMillis();
			if (correct) {
				gui.repaint();
			}

			long sleepTime = (long) ((time / (3 / step)) * 1000)
					- (System.currentTimeMillis() - start);
			if (sleepTime >= 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			t += step;
		}

		if (correct) {
			/* we need to update the card num for the recipient */
			OtherPlayerData d = p.others.get(question.source);
			d.numCards++;

			/* wait here for a little bit to avoid jitter */

			t = 3;
			long start = System.currentTimeMillis();
			gui.repaint();

			long sleepTime = (long) ((time / (3 / step)) * 1000)
					- (System.currentTimeMillis() - start);
			if (sleepTime >= 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		switchMode(DrawMode.WAIT_FOR_Q);
	}

	private void setupCardTransfer() {
		/* find the layout of the aske[re] */
		Layout askerL = getPlayerLayout(question.source);
		Layout askeeL = getPlayerLayout(question.dest);

		AffineTransform startxform = null;
		AffineTransform endxform = null;

		AffineTransform flipxform = AffineTransform.getScaleInstance(
				-1, 1);
		flipxform.translate(-710, 0);
		AffineTransformOp flip = new AffineTransformOp(flipxform, null);

		this.middleI = Resources.scaleImage(
				Resources.CARD_IMGS.get(question.c), 10);

		if (question.dest == p.id) {
			startI = this.middleI;
			startxform = getPlayerCardXform(question.c);
		} else {
			startxform = getOpponentXform(askeeL);
			startxform.scale(-1, 1);
			startxform.translate(-71, 0);

			OtherPlayerData d = p.others.get(question.dest);
			startI = flip.filter(
					drawCardBack(d.t, d.numCards,
							askeeL.rot), null);

			/*
			 * render the underlying player card as
			 * having one less card
			 */
			d.numCards--;

		}
		if (question.source == p.id) {
			/*
			 * we should insert it into the hand now to make
			 * space for it, the hand will draw it as
			 * invisible
			 */
			p.hand.add(question.c);

			endxform = getPlayerCardXform(question.c);

			endI = this.middleI;
		} else {
			endxform = getOpponentXform(askerL);
			endxform.scale(-1, 1);
			endxform.translate(-71, 0);

			OtherPlayerData d = p.others.get(question.source);
			endI = flip.filter(
					drawCardBack(d.t, d.numCards + 1,
							askerL.rot), null);
		}

		AffineTransform middlexform = new AffineTransform();
		middlexform.translate(gw / 2, h / 2);
		double scale = 2.5;
		middlexform.scale(scale, scale);
		middlexform.translate(-71. / 2, -96. / 2);

		/* the images are 10x size */
		startxform.scale(0.1, 0.1);
		endxform.scale(0.1, 0.1);
		middlexform.scale(0.1, 0.1);

		this.startX = new double[6];
		this.middleX = new double[6];
		this.endX = new double[6];
		this.diff1 = new double[6];
		this.diff2 = new double[6];

		startxform.getMatrix(startX);
		middlexform.getMatrix(middleX);
		endxform.getMatrix(endX);

		for (int i = 0; i < 6; i++) {
			diff1[i] = middleX[i] - startX[i];
			diff2[i] = endX[i] - middleX[i];
		}
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
		long start = System.currentTimeMillis();

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
		case QUESTION_ASKED:
			drawQuestionAsked(g);
			break;
		case QUESTION_RESPONSE:
			drawQuestionResponse(g);
			break;
		}

		/* we might be choosing a suit to declare */
		if (declarePressed) {
			drawAskSuit(g);
		}

		g.clearRect(gw, 0, w - gw, h);

		/* draw the sidebar */
		drawSidebar(g);

		System.out.println(System.currentTimeMillis() - start);
	}

	private void drawSidebar(Graphics2D g) {
		g.setTransform(AffineTransform.getTranslateInstance(this.w
				- this.sw, 0));

		drawSidebarBorder(g);
		drawLog(g);
		drawTricks(g);

		g.setTransform(new AffineTransform());

		/* draw buttons */
		for (Button b : buttons) {
			b.draw(g);
		}
	}

	private void drawSidebarBorder(Graphics2D g) {
		g.setColor(Color.BLACK);

		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(5.0f));

		g.drawLine(-3, 0, -3, h);

		g.setStroke(s);
	}

	private void drawLog(Graphics2D g) {
		g.drawImage(glog.getImg(), 5, h - glog.h, null);
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(3.0f));

		g.drawLine(0, h - glog.h, sw, h - glog.h);

		g.setStroke(s);
	}

	private void drawTricks(Graphics2D g) {
		p.tricks = new HashMap<Integer, Team>();
		for (int i = 0; i < 8; i++) {
			switch (ServerUtil.rand.nextInt(3)) {
			case 0:
				break;
			case 1:
				p.tricks.put(i, Team.BLU);
				break;
			case 2:
				p.tricks.put(i, Team.RED);
				break;
			}
		}
		g.setColor(Color.BLACK);
		g.setFont(Resources.font(30f));
		FontMetrics fm = g.getFontMetrics();

		g.drawString("Tricks: ", 5, fm.getHeight());

		int y = fm.getHeight() + 5;
		int x = 5;
		for (int i = 0; i < 8; i++) {
			if (p.tricks.get(i) != null) {
				BufferedImage img = null;
				switch (p.tricks.get(i)) {
				case RED:
					img = Resources.SUIT_TILES_RED.get(i);
					break;
				case BLU:
					img = Resources.SUIT_TILES_BLU.get(i);
					break;
				}
				g.drawImage(img,
						x + (i / 2) * img.getWidth(),
						y
								+ (i % 2)
								* (img.getHeight() + 4),
						null);
			}
		}
	}

	/**
	 * Draws the basic parts of the game such as your and others' hands
	 */
	private void drawBasicGame(Graphics2D g) {
		drawOpponents(g);
		drawHand(g);
	}

	private void drawHand(Graphics2D g) {
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
			if (p.turn == p.id && mode == DrawMode.WAIT_FOR_Q) {
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

			/* draw your name */
			/*
			 * this must be before the cards so that transferring
			 * cards do not slide underneath it
			 */
			g.setFont(Resources.font(40f));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(Color.BLACK);
			g.drawString(p.name, gw / 2 - handwidth / 2, h
					- handheight - 20 - fm.getDescent() + 1);

			for (Card c : p.hand.getCardsSorted()) {
				/*
				 * if the card is being transferred draw the
				 * transferring version instead
				 */
				if (mode == DrawMode.QUESTION_RESPONSE
						&& qcorrect
						&& c.equals(question.c)) {
					drawQuestionResponseRight(g);
				} else {
					g.drawImage(Resources.CARD_IMGS.get(c),
							transform, null);
				}
				transform.translate(12, 0);
			}
		}
	}

	private void drawOpponents(Graphics2D g) {
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

				AffineTransform xform = getOpponentXform(l);

				/* if they are selected, indicate that */
				if (mode == DrawMode.WAIT_FOR_Q
						&& p.turn == p.id
						&& selection == d.id
						&& d.t != p.team) {
					xform.translate(0, -10);
				}

				g.setTransform(xform);
				/* if it's their turn draw the glow */
				if (d.id == p.turn
						&& mode == DrawMode.WAIT_FOR_Q) {
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
				g.setFont(Resources.font(10f));
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

	private Layout getPlayerLayout(int id) {
		if (id == p.id) {
			return new Layout(0.5, 1, 0);
		} else {
			return getLayoutSet(p.others.size())[(p.others.get(id).seat
					- p.seat - 1 + p.others.size())
					% p.others.size()];
		}
	}

	private AffineTransform getOpponentXform(Layout l) {
		double scale = getLayoutScale(p.others.size());

		int x = (int) (l.x * gw);
		int y = (int) (l.y * h);

		AffineTransform xform = new AffineTransform();
		/* translate to drawing location */
		xform.translate(x, y);
		/* rotate */
		xform.quadrantRotate(l.rot);
		/* scale card */
		xform.scale(scale, scale);
		/* translate to centre of card */
		xform.translate(-71 / 2, -96 / 2);

		return xform;
	}

	private AffineTransform getPlayerCardXform(Card c) {
		int pos = p.hand.getCardsSorted().indexOf(c);

		/* amount the card drawing is scaled by */
		double cardScale = 2.5;
		int handwidth = (int) (((p.hand.getNumCards() - 1) * 12 + 71) * cardScale);
		AffineTransform transform = new AffineTransform();

		transform.translate(gw / 2 - handwidth / 2, h);
		transform.scale(cardScale, cardScale);
		/* the size of the cards in pixels */
		transform.translate(0, -96 / 2);
		transform.translate(12 * pos, 0);

		return transform;
	}

	private void drawWaitForQuestion(Graphics2D g) {
		if (p.turn != p.id)
			return;
		String s = "Select a player to ask";

		g.setColor(Color.BLACK);
		g.setFont(Resources.font(30f));
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
		g.setFont(Resources.font(30));
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
		g.setFont(Resources.font(30));
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

	private void drawQuestionAsked(Graphics2D g) {
		int width = 400;
		int height = 200;

		double s = getLayoutScale(p.others.size());
		int lef = (int) (s * 96. / 2 + 60) + width / 2;
		int rit = gw - lef;
		int top = (int) (s * 96. / 2 + 60) + height / 2;
		int bot = h - (int) (2.5 * 96. / 2 + 80) - height / 2;

		/*
		 * the layout of the person asking, set it to ourselves by
		 * default
		 */
		Layout l = new Layout(0.5, 1, 0);
		for (OtherPlayerData d : p.others) {
			if (d.id == p.id)
				continue;
			if (d.id == question.source) {
				l = getPlayerLayout(d.id);
			}
		}

		int lx = (int) (l.x * gw);
		int ly = (int) (l.y * h);

		/* place to point the arrow at */
		int tx = lx;
		int ty = ly;

		switch (l.rot) {
		case 0:
			ty -= 2.5 * 96 / 2;
			break;
		case 1:
			tx += s * 96 / 2;
			break;
		case 2:
			ty += s * 96 / 2;
			break;
		case 3:
			tx -= s * 96 / 2;
			break;
		}

		int cx = Math.max(Math.min(lx, rit), lef);
		int cy = Math.max(Math.min(ly, bot), top);

		AffineTransform xform = new AffineTransform();
		xform.translate(cx, cy);
		xform.translate(-width / 2, -height / 2);

		Shape box = new RoundRectangle2D.Double(0, 0, width, height,
				20, 20);

		/* the vector from the centre of the box to the target */
		double vx = tx - cx;
		double vy = ty - cy;

		double mag = Math.sqrt(vx * vx + vy * vy);

		final double perplen = 50;
		final double shorten = 40;

		double px = vy / mag * perplen;
		double py = -vx / mag * perplen;

		vx *= (mag - shorten) / mag;
		vy *= (mag - shorten) / mag;

		double ox = width / 2;
		double oy = height / 2;

		Path2D triangle = new Path2D.Double();
		triangle.moveTo(ox + vx, oy + vy);
		triangle.lineTo(ox + px, oy + py);
		triangle.lineTo(ox - px, oy - py);
		triangle.closePath();

		Area bubble = new Area(box);

		bubble.add(new Area(triangle));

		g.setTransform(xform);
		/* draw the inside */
		g.setColor(Color.WHITE);
		g.fill(bubble);
		/* draw the outside */
		g.setColor(Color.BLACK);
		Stroke orig = g.getStroke();
		g.setStroke(new BasicStroke(2f));
		g.draw(bubble);
		g.setStroke(orig);

		/* now draw inside the box */

		float qscale = 1.5f;
		g.setFont(Resources.font(135f * qscale));
		FontMetrics fm0 = g.getFontMetrics();
		g.drawString("?", width - fm0.stringWidth("?") - 5, height / 2
				+ fm0.getAscent() / 2);
		g.drawImage(new AffineTransformOp(AffineTransform
				.getScaleInstance(qscale, qscale), null)
				.filter(Resources.CARD_IMGS.get(question.c),
						null),
				(int) (width - fm0.stringWidth("?") - 71
						* qscale - 10),
				(int) (height / 2 - 96 * qscale / 2), null);

		g.setFont(Resources.font(30f));
		FontMetrics fm1 = g.getFontMetrics();
		String uname = p.others.get(question.dest).uname;
		g.drawString(uname, (int) (width - fm0.stringWidth("?") - 71
				* qscale - 10)
				/ 2 - fm1.stringWidth(uname) / 2, height / 2
				+ fm1.getAscent() / 2);

		g.setTransform(new AffineTransform());
	}

	private void drawQuestionResponse(Graphics2D g) {
		if (qcorrect) {
			/* its already been redrawn */
			if (p.hand.contains(question.c))
				return;
			drawQuestionResponseRight(g);
		} else {
			drawQuestionResponseWrong(g);

		}
	}

	private void drawQuestionResponseRight(Graphics2D g) {
		double[] xform = new double[6];
		IntToDoubleFunction f;
		if (t <= 1) {
			f = i -> startX[i] + diff1[i] * t;
		} else if (t <= 2) {
			f = i -> middleX[i];
		} else if (t <= 3) {
			f = i -> middleX[i] + diff2[i] * (t - 2);
		} else {
			throw new IllegalArgumentException(
					"t parameter is too large: " + t);
		}

		for (int i = 0; i < 6; i++) {
			xform[i] = f.applyAsDouble(i);
		}

		BufferedImage img;

		if (t <= 0.5) {
			img = startI;
		} else if (t <= 2.5) {
			img = middleI;
		} else if (t <= 3) {
			img = endI;
		} else {
			throw new IllegalArgumentException(
					"t parameter is too large: " + t);
		}

		g.drawImage(img, new AffineTransform(xform), null);
	}

	private void drawQuestionResponseWrong(Graphics2D g) {
		/* we just draw a big no next to the person being asked */
		/* copied from drawQuestionAsked */
		int width = 100;
		int height = 100;

		double s = getLayoutScale(p.others.size());
		int lef = (int) (s * 96. / 2 + 60) + width / 2;
		int rit = gw - lef;
		int top = (int) (s * 96. / 2 + 60) + height / 2;
		int bot = h - (int) (2.5 * 96. / 2 + 80) - height / 2;

		/*
		 * the layout of the person asking, set it to ourselves
		 * by
		 * default
		 */
		Layout l = new Layout(0.5, 1, 0);
		for (OtherPlayerData d : p.others) {
			if (d.id == p.id)
				continue;
			if (d.id == question.dest) {
				l = getPlayerLayout(d.id);
			}
		}

		int lx = (int) (l.x * gw);
		int ly = (int) (l.y * h);

		/* place to point the arrow at */
		int tx = lx;
		int ty = ly;

		switch (l.rot) {
		case 0:
			ty -= 2.5 * 96 / 2;
			break;
		case 1:
			tx += s * 96 / 2;
			break;
		case 2:
			ty += s * 96 / 2;
			break;
		case 3:
			tx -= s * 96 / 2;
			break;
		}

		int cx = Math.max(Math.min(lx, rit), lef);
		int cy = Math.max(Math.min(ly, bot), top);

		AffineTransform xform = new AffineTransform();
		xform.translate(cx, cy);
		xform.translate(-width / 2, -height / 2);

		Shape box = new RoundRectangle2D.Double(0, 0, width, height,
				20, 20);

		/* the vector from the centre of the box to the target */
		double vx = tx - cx;
		double vy = ty - cy;

		double mag = Math.sqrt(vx * vx + vy * vy);

		final double perplen = 50;
		final double shorten = 40;

		double px = vy / mag * perplen;
		double py = -vx / mag * perplen;

		vx *= (mag - shorten) / mag;
		vy *= (mag - shorten) / mag;

		double ox = width / 2;
		double oy = height / 2;

		Path2D triangle = new Path2D.Double();
		triangle.moveTo(ox + vx, oy + vy);
		triangle.lineTo(ox + px, oy + py);
		triangle.lineTo(ox - px, oy - py);
		triangle.closePath();

		Area bubble = new Area(box);

		bubble.add(new Area(triangle));

		g.setTransform(xform);
		/* draw the inside */
		g.setColor(Color.WHITE);
		g.fill(bubble);
		/* draw the outside */
		g.setColor(Color.BLACK);
		Stroke orig = g.getStroke();
		g.setStroke(new BasicStroke(2f));
		g.draw(bubble);
		g.setStroke(orig);

		/* now draw inside the box */

		float qscale = 0.6f;
		g.setFont(Resources.font(135f * qscale));
		FontMetrics fm = g.getFontMetrics();
		g.drawString("No", width / 2 - fm.stringWidth("No") / 2, height
				/ 2 + fm.getAscent() / 2);

		g.setTransform(new AffineTransform());
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

		/* if we have no cards, draw a grayed image */
		BufferedImage img = (numCards > 0 ? Resources.CARD_BACKS
				: Resources.CARD_BACKS_GRAY).get(t);
		g.drawImage(img, AffineTransform.getScaleInstance(10, 10), null);

		Font f = Resources.font(40f);
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
	public void mouseMoved(int x, int y) {
		mx = x;
		my = y;

		mouseMoved();
	}

	private void mouseMoved() {
		if (declarePressed) {
			movedAskSuit();
			return;
		}

		switch (mode) {
		case WAIT_FOR_Q:
			movedWaitForQuestion();
			break;
		case ASK_QUESTION:
			movedAskQuestion();
			break;
		}

		/* tell buttons */
		for (Button b : buttons) {
			b.mouseMoved(mx, my, gui);
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
	public void mousePressed(int x, int y, int button) {
		this.mx = x;
		this.my = y;
		if (button != MouseEvent.BUTTON1) {
			return;
		}

		/* if we're attempting to declare we should handle that */
		if (declarePressed) {
			clickDeclareSuit();
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

		/* tell buttons */
		for (Button b : buttons) {
			b.mouseLeftClick(mx, my, gui);
		}
	}

	private void clickDeclareSuit() {
		declarePressed = false;
		this.updated();
		if (suitselection == -1) {
			return;
		}

		/* enter full scale declaration mode */
		p.pi.sendDecStart(new Declaration(p.id, suitselection));
	}

	private void clickWaitForQuestion() {
		if (p.turn == p.id && selection != -1) {
			OtherPlayerData d = p.others.get(selection);
			if (d.t == p.team || d.numCards == 0) {
				return;
			}

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
