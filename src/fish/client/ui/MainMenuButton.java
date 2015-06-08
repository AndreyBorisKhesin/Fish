package fish.client.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Buttons found on the main menu
 *
 */
public class MainMenuButton implements Button {

	private String s;
	private ButtonAction action;

	private int xp, yp;
	private int w, h;

	boolean moused;

	/**
	 * Creates a new menu button out of the string to show, and what to do
	 * when it is pressed
	 * 
	 * @param x The x location of this button on the screen
	 * @param y The y location of this button on the screen
	 * @param s The string to display on this button
	 * @param clicked The action to perform when clicked
	 */
	public MainMenuButton(int x, int y, String s, ButtonAction action) {
		this.s = s;
		this.action = action;
		this.xp = x;
		this.yp = y;
		this.w = 295;
		this.h = 150;
		this.moused = false;
	}

	@Override
	public int getWidth() {
		return w;
	}

	@Override
	public int getHeight() {
		return h;
	}

	private boolean intersecting(int x, int y) {
		return x >= xp && x < xp + w && y >= yp && y < yp + h;
	}

	@Override
	public void mouseMoved(int x, int y, FishGUI g) {
		if(intersecting(x, y) != moused) {
			moused = intersecting(x, y);
			g.repaint();
		}
	}

	@Override
	public void mouseLeftClick(int x, int y, FishGUI g) {
		if (intersecting(x, y)) {
			action.clicked();
		}
	}

	@Override
	public void mouseRightClick(int x, int y, FishGUI g) {
		return;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRoundRect(xp, yp, w, h, 20, 20);
		g.setColor(moused ? Color.ORANGE : Color.GREEN);
		g.fillRoundRect(xp + 10, yp + 10, w - 20, h - 20, 20, 20);
		g.setColor(Color.BLACK);
		g.setFont(Resources.MENU_OPTION_FONT);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(s, xp + w / 2 - fm.stringWidth(s) / 2, yp + h / 2
				+ fm.getAscent() / 2);
	}

}
