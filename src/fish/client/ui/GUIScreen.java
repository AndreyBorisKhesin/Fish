package fish.client.ui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface GUIScreen extends MouseListener, MouseMotionListener {
	public void paintFrame(Graphics2D g, int w, int h);

	@Override
	public default void mouseClicked(MouseEvent e) {
	}

	@Override
	public default void mousePressed(MouseEvent e) {
	}

	@Override
	public default void mouseReleased(MouseEvent e) {
	}

	@Override
	public default void mouseDragged(MouseEvent e) {
	}

	@Override
	public default void mouseEntered(MouseEvent e) {
	}

	@Override
	public default void mouseExited(MouseEvent e) {
	}

	@Override
	public default void mouseMoved(MouseEvent e) {
	}
}
