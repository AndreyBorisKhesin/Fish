package fish.client.ui.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.function.BooleanSupplier;

import fish.client.ui.FishGUI;

/**
 * An interface for clickable buttons
 *
 */
public class Button implements UIElement {

	private String s;
	private UIClick action;
	private BooleanSupplier draw;

	private int xp, yp;
	private int w, h;

	boolean moused;

	private Font f;

	/**
	 * Creates a new menu button out of the string to show, and what to do
	 * when it is pressed
	 * 
	 * @param x The x location of this button on the screen
	 * @param y The y location of this button on the screen
	 * @param s The string to display on this button
	 * @param clicked The action to perform when clicked
	 */
	public Button(int x, int y, int w, int h, String s, Font f,
			UIClick action) {
		this(x, y, w, h, s, f, action, () -> true);
	}

	/**
	 * Creates a new menu button out of the string to show, and what to do
	 * when it is pressed, and does not draw if draw() returns false
	 * 
	 * @param x The x location of this button on the screen
	 * @param y The y location of this button on the screen
	 * @param s The string to display on this button
	 * @param clicked The action to perform when clicked
	 */
	public Button(int x, int y, int w, int h, String s, Font f,
			UIClick action, BooleanSupplier draw) {
		this.s = s;
		this.action = action;
		this.xp = x;
		this.yp = y;
		this.w = w;
		this.h = h;
		this.f = f;
		this.moused = false;
		this.draw = draw;
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}

	private boolean intersecting(int x, int y) {
		return x >= xp && x < xp + w && y >= yp && y < yp + h;
	}

	@Override
	public void mouseMoved(int x, int y, FishGUI g) {
		if (!this.draw.getAsBoolean())
			return;
		if (intersecting(x, y) != moused) {
			moused = intersecting(x, y);
			g.repaint();
		}
	}

	@Override
	public void mouseLeftClick(int x, int y, FishGUI g) {
		if (!this.draw.getAsBoolean())
			return;
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
		if (!this.draw.getAsBoolean())
			return;
		g.setColor(Color.BLACK);
		g.fillRoundRect(xp, yp, w, h, 20, 20);
		g.setColor(moused ? Color.ORANGE : Color.GREEN);
		g.fillRoundRect(xp + 10, yp + 10, w - 20, h - 20, 20, 20);
		g.setColor(Color.BLACK);
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(s, xp + w / 2 - fm.stringWidth(s) / 2, yp + h / 2
				+ fm.getAscent() / 2);
	}
}
