package fish.client.ui.elements;

import fish.client.ui.Resources;

/**
 * Buttons found on the main menu
 *
 */
public class MainMenuButton extends Button {

	/**
	 * Creates a new menu button out of the string to show, and what to do
	 * when it is pressed
	 * 
	 * @param x The x location of this button on the screen
	 * @param y The y location of this button on the screen
	 * @param s The string to display on this button
	 * @param clicked The action to perform when clicked
	 */
	public MainMenuButton(int x, int y, String s, UIClick action) {
		super(x, y, 295, 150, s, Resources.MENU_OPTION_FONT, action);
	}
}
