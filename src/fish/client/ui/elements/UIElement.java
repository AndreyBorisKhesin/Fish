package fish.client.ui.elements;

import java.awt.Graphics;

import fish.client.ui.FishGUI;

public interface UIElement {

	public void mouseMoved(int x, int y, FishGUI g);

	public void mouseLeftClick(int x, int y, FishGUI g);

	public void mouseRightClick(int x, int y, FishGUI g);

	/**
	 * Draw the button graphics
	 * 
	 * @param g The graphics to draw it to
	 */
	public void draw(Graphics g);
}
