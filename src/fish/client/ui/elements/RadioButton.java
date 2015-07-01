package fish.client.ui.elements;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import fish.client.ui.FishGUI;
import fish.client.ui.Resources;

/**
 * A radio button for selecting between options
 *
 */
public class RadioButton implements UIElement {
	private String[] buttons;

	private UISelection action;

	private int selection;
	private int moused;

	private int xp, yp;

	/* height per line */
	private int h;

	/* radius of buttons */
	private int r;

	private String title;
	
	public RadioButton(int x, int y, String title, String[] buttons,
			UISelection action) {
		this.xp = x;
		this.yp = y;
		this.title = title;
		this.buttons = buttons;
		this.action = action;
		this.selection = -1;
		this.moused = -1;

		this.h = 40;
		this.r = 35;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int s) {
		selection = s;
	}

	@Override
	public void mouseMoved(int x, int y, FishGUI g) {
		for (int i = 0; i < buttons.length; i++) {
			if (x >= xp && x < xp + r && y >= yp + i * h
					&& y < yp + (i + 1) * h) {
				if (i != moused) {
					moused = i;
					g.repaint();
				}
				return;
			}
		}
		if (moused != -1) {
			moused = -1;
			g.repaint();
		}
	}

	@Override
	public void mouseLeftClick(int x, int y, FishGUI g) {
		for (int i = 0; i < buttons.length; i++) {
			if (x >= xp && x < xp + r && y >= yp + i * h
					&& y < yp + (i + 1) * h) {
				if (selection != i) {
					selection = i;
					action.selected(i);
					g.repaint();
				}
			}
		}
	}

	@Override
	public void mouseRightClick(int x, int y, FishGUI g) {
		return;
	}

	@Override
	public void draw(Graphics g) {
		/* draw the circles and names */
		g.setFont(Resources.font(25));
		FontMetrics fm = g.getFontMetrics();
		g.drawString(title, xp, yp - 5);
		for (int i = 0; i < buttons.length; i++) {
			g.setColor(Color.BLACK);
			g.fillOval(xp, yp + i * h, r, r);
			g.setColor(i == moused ? Color.RED : Color.CYAN);
			g.fillOval(xp + 3, yp + i * h + 3, r - 6, r - 6);

			if (selection == i) {
				g.setColor(Color.BLACK);
				g.fillOval(xp + 8, yp + i * h + 8, r - 16,
						r - 16);
			}

			g.setColor(Color.BLACK);
			g.drawString(buttons[i], xp + r + 5, yp + i * h + r / 2 + fm.getAscent() / 2);
		}
	}
}
