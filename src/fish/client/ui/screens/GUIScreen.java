package fish.client.ui.screens;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import fish.client.ui.FishGUI;

public abstract class GUIScreen implements MouseListener, MouseMotionListener {

	public abstract void paintFrame(Graphics2D g, int w, int h);

	private FishGUI gui;

	public GUIScreen(FishGUI gui) {
		this.gui = gui;
	}

	private Point2D invert(MouseEvent e) {
		Point2D inverse = gui.inverseScale.transform(
				new Point2D.Double(e.getX(), e.getY()), null);

		return inverse;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point2D p = invert(e);
		mouseClicked((int) p.getX(), (int) p.getY(), e.getButton());
	}

	public void mouseClicked(int x, int y, int button) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point2D p = invert(e);
		mousePressed((int) p.getX(), (int) p.getY(), e.getButton());
	}

	public void mousePressed(int x, int y, int button) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point2D p = invert(e);
		mouseReleased((int) p.getX(), (int) p.getY(), e.getButton());
	}

	public void mouseReleased(int x, int y, int button) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D p = invert(e);
		mouseDragged((int) p.getX(), (int) p.getY(), e.getButton());
	}

	public void mouseDragged(int x, int y, int button) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		Point2D p = invert(e);
		mouseEntered((int) p.getX(), (int) p.getY());
	}

	public void mouseEntered(int x, int y) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Point2D p = invert(e);
		mouseExited((int) p.getX(), (int) p.getY());
	}

	public void mouseExited(int x, int y) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point2D p = invert(e);
		mouseMoved((int) p.getX(), (int) p.getY());
	}

	public void mouseMoved(int x, int y) {
	}
}
