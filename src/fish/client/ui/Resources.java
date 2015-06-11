package fish.client.ui;

import java.awt.Font;
import java.awt.image.BufferedImage;
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

	static {
		CARD_IMGS = new HashMap<Card, BufferedImage>();
		CARD_BACKS = new HashMap<Team, BufferedImage>();
	}

	private static final String FONT_LOC = "resources/gecko.ttf";
	public static final Font MENU_FONT = UIUtil.loadFont(FONT_LOC)
			.deriveFont(200f);
	public static final Font MENU_OPTION_FONT = MENU_FONT.deriveFont(45f);
	public static final Font RADIO_BUTTON_FONT = MENU_FONT.deriveFont(25f);

	public static final Font GAME_FONT = MENU_FONT.deriveFont(20f);
	
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
		CARD_BACKS.put(Team.BLU, UIUtil.loadImage("resources/cards/bb.png"));
		CARD_BACKS.put(Team.RED, UIUtil.loadImage("resources/cards/br.png"));
	}

}
