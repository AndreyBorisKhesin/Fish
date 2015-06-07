package fish.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class FishGUI extends JPanel {
	private JFrame frame;
	private Container cpane;

	public Resolution size;

	private BufferedImage buf;
	private Graphics2D bufg;
	public Painter painter;

	private Map<GUIMode, Painter> painters;

	public FishGUI() {
		frame = new JFrame("Fish");
		cpane = frame.getContentPane();

		/* jframe management */
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		frame.add(this);

		setSize(Resolution._1280_720);

		/* set up the drawing environment */
		buf = new BufferedImage(size.d.width, size.d.height,
				BufferedImage.TYPE_INT_ARGB);
		bufg = (Graphics2D) buf.getGraphics();
		bufg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		bufg.setBackground(new Color(0x00, 0xce, 0xd1));

		painters = new HashMap<GUIMode, Painter>();

		/* placing of things */
		cpane.setLayout(null);
		frame.repaint();
		frame.validate();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void addPainter(GUIMode mode, Painter p) {
		painters.put(mode, p);
	}

	public void switchMode(GUIMode mode) {
		painter = painters.get(mode);
	}

	public enum GUIMode {
		LOADER, MENU, GAME, SETTINGS;
	}

	public void setSize(Resolution r) {
		// TODO Auto-generated method stub

		size = r;
		cpane.setSize(r.d);
		cpane.setPreferredSize(r.d);
		this.setBounds(0, 0, r.d.width, r.d.height);
		frame.pack();
	}

	@Override
	public void paintComponent(Graphics g) {
		bufg.clearRect(0, 0, buf.getWidth(), buf.getHeight());

		if (painter != null) {
			painter.paintFrame(bufg, buf.getWidth(),
					buf.getHeight());
		}

		g.drawImage(buf.getScaledInstance(size.d.width, size.d.height,
				Image.SCALE_FAST), 0, 0, null);
	}

	public void redraw() {
		frame.repaint();
	}
}
