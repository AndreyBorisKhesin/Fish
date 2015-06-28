package fish.client.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import fish.client.ui.screens.GUIScreen;

@SuppressWarnings("serial")
public class FishGUI extends JPanel {
	private JFrame frame;
	private Container cpane;

	public Resolution size;

	private BufferedImage buf;
	private Graphics2D bufg;
	public GUIScreen guiScreen;

	public AffineTransform scale;
	public AffineTransform inverseScale;

	public FishGUI() {
		frame = new JFrame("Fish");
		cpane = frame.getContentPane();

		/* jframe management */
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		frame.add(this);

		/* set up the drawing environment */
		buf = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
		bufg = (Graphics2D) buf.getGraphics();
		bufg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		bufg.setBackground(new Color(0x00, 0xce, 0xd1));

		setSize(Resolution._ANDREYS_SCREEN);

		/* placing of things */
		cpane.setLayout(null);
		frame.repaint();
		frame.validate();
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	/**
	 * Makes the frame visible
	 */
	public void start() {
		frame.setVisible(true);
	}

	public void switchMode(GUIScreen screen) {
		this.removeMouseListener(guiScreen);
		this.removeMouseMotionListener(guiScreen);
		guiScreen = screen;
		this.addMouseListener(guiScreen);
		this.addMouseMotionListener(guiScreen);
		this.repaint();
	}

	public void setSize(Resolution r) {
		size = r;
		cpane.setSize(r.d);
		cpane.setPreferredSize(r.d);
		this.setBounds(0, 0, r.d.width, r.d.height);
		this.scale = AffineTransform.getScaleInstance(r.d.width
				/ (double) buf.getWidth(), r.d.height
				/ (double) buf.getHeight());
		try {
			this.inverseScale = scale.createInverse();
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		frame.pack();
	}

	@Override
	public void paintComponent(Graphics g) {
		bufg.clearRect(0, 0, buf.getWidth(), buf.getHeight());

		if (guiScreen != null) {
			guiScreen.paintFrame(bufg, buf.getWidth(),
					buf.getHeight());
		}

		((Graphics2D) g).drawImage(buf, scale, null);
	}

	public void redraw() {
		frame.repaint();
	}

	public Dimension bufferDimensions() {
		return new Dimension(buf.getWidth(), buf.getHeight());
	}
}
