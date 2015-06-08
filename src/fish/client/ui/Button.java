package fish.client.ui;

import java.awt.Graphics;

/**
 * An interface for clickable buttons
 *
 */
public interface Button {

	public int getWidth();

	public int getHeight();

	public void mouseMoved(int x, int y, FishGUI g);

	public void mouseLeftClick(int x, int y, FishGUI g);

	public void mouseRightClick(int x, int y, FishGUI g);

	/**
	 * Draw the button graphics
	 * 
	 * @param g The graphics to draw it to
	 */
	public void draw(Graphics g);

	public interface ButtonAction {
		public void clicked();
	}
}
