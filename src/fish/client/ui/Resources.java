package fish.client.ui;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import fish.Card;
import fish.Util;

/**
 * Contains resources such as fonts, cards, etc.
 *
 */
public class Resources {

	public static final Map<Card, BufferedImage> CARD_IMGS;
	public static final BufferedImage CARD_BACK_RED;
	public static final BufferedImage CARD_BACK_BLU;

	private static final String FONT_LOC = "resources/gecko.ttf";
	public static final Font MENU_FONT = UIUtil.loadFont(FONT_LOC)
			.deriveFont(200f);
	public static final Font MENU_OPTION_FONT = MENU_FONT.deriveFont(45f);

	static {
		CARD_IMGS = new HashMap<Card, BufferedImage>();
		CARD_BACK_RED = UIUtil.loadImage("resources/cards/br.png");
		CARD_BACK_BLU = UIUtil.loadImage("resources/cards/bb.png");
	}

	/**
	 * Load all the resources
	 */
	public static void load() {
		for (Card c : Util.deck()) {
			CARD_IMGS.put(c,
					UIUtil.loadImage("resources/cards/"
							+ c.suit + "" + c.rank
							+ ".png"));
		}
	}
	
}
