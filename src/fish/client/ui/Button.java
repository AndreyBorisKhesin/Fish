package fish.client.ui;

import java.awt.Graphics;

/**
 * An interface for clickable buttons
 *
 */
public interface Button {

	public int getWidth();

	public int getHeight();

	public void mouseMoved(int x, int y);

	public void mouseLeftClick(int x, int y);

	public void mouseRightClick(int x, int y);

	/**
	 * Draw the button graphics
	 * 
	 * @param g The graphics to draw it to
	 * @param x The x location to draw at
	 * @param y The y location to draw at
	 */
	public void draw(Graphics g, int x, int y);

	public interface ButtonAction {
		public void clicked();
	}
}
