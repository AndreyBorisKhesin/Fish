package fish.client.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;

import fish.Card;
import fish.Team;
import fish.Util;

/**
 * Contains resources such as fonts, cards, etc.
 *
 */
public class Resources {

	public static final Map<Card, BufferedImage> CARD_IMGS;
	public static final Map<Team, BufferedImage> CARD_BACKS;
	public static final Map<Integer, BufferedImage> SUIT_CARDS;
	public static final Map<Integer, BufferedImage> SUIT_CARDS_GRAY;

	static {
		CARD_IMGS = new HashMap<Card, BufferedImage>();
		CARD_BACKS = new HashMap<Team, BufferedImage>();
		SUIT_CARDS = new HashMap<Integer, BufferedImage>();
		SUIT_CARDS_GRAY = new HashMap<Integer, BufferedImage>();
	}

	private static final String FONT_LOC = "resources/gecko.ttf";
	public static final Font MENU_FONT = UIUtil.loadFont(FONT_LOC)
			.deriveFont(200f);
	public static final Font MENU_OPTION_FONT = MENU_FONT.deriveFont(45f);
	public static final Font RADIO_BUTTON_FONT = MENU_FONT.deriveFont(25f);

	public static final Font GAME_FONT = MENU_FONT.deriveFont(20f);

	public static final Color GLOW = new Color(255, 215, 0);

	/**
	 * Load all the resources
	 */
	public static void load() {
		/* the static initializers above will run as well */
		for (Card c : Util.deck()) {
			CARD_IMGS.put(c,
					UIUtil.loadImage("resources/cards/"
							+ c.suit + "" + c.rank
							+ ".png"));
		}
		CARD_BACKS.put(Team.BLU,
				UIUtil.loadImage("resources/cards/bb.png"));
		CARD_BACKS.put(Team.RED,
				UIUtil.loadImage("resources/cards/br.png"));

		float s = 0.25f;
		float o = 256 * (1 - s);
		RescaleOp grayOp = new RescaleOp(new float[] { s, s, s, 1 },
				new float[] { o, o, o, 0 }, null);

		for (int i = 0; i < 8; i++) {
			SUIT_CARDS.put(i, CARD_IMGS.get(new Card(i,
					i % 2 == 0 ? 0 : 5)));
			SUIT_CARDS_GRAY.put(i,
					grayOp.filter(SUIT_CARDS.get(i), null));
		}
	}

	public static BufferedImage copyImage(BufferedImage img) {
		ColorModel cm = img.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = img.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static BufferedImage scaleImage(BufferedImage img, double scale) {
		return new AffineTransformOp(AffineTransform.getScaleInstance(
				scale, scale), null).filter(img, null);
	}

}
