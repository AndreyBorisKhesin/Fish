package fish.client.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import fish.client.ui.FishGUI;

/**
 * Displays while cards and other similar things are loading into the game
 *
 */
public class Loader implements Runnable, GUIScreen {

	private final double INCREMENT = 0.625 * 2;
	private final int FLIP_INCREMENT = 5 * 2;
	private final int DELAY = 30;

	private final int size = 200;
	private final int thickness = 20;
	private final int radius = size / 2 - thickness / 2;

	private final Color[] orig_colors = { Color.BLACK.brighter(),
			Color.GREEN.darker(), Color.GREEN.brighter(),
			Color.WHITE };

	private final Color[] colors = new Color[8];

	private BufferedImage buf;
	private Graphics2D bufg;

	private Thread thread;
	
	/**
	 * Position around the octagon, in degrees
	 */
	private double pos;

	private int flipIdx;
	private int flipPos;

	private FishGUI gui;

	public Loader(FishGUI gui) {
		buf = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		bufg = (Graphics2D) buf.getGraphics();
		bufg.setBackground(new Color(0, 0, 0, 0));

		pos = 0;
		flipIdx = 0;
		flipPos = 0;

		for (int i = 0; i < 8; i++) {
			colors[i] = orig_colors[i % 4];
		}

		drawBuf();

		this.gui = gui;
	}

	public void go() {
		if(thread != null) {
			throw new RuntimeException("Already running loader was told to run");
		}
		thread = new Thread(this);
		thread.start();
	}
	
	public void end() {
		thread.interrupt();
		thread = null;
	}
	
	private void drawBuf() {
		bufg.setTransform(new AffineTransform());
		bufg.clearRect(0, 0, size, size);
		bufg.setTransform(AffineTransform.getTranslateInstance(
				size / 2, size / 2));
		for (int i = 0; i < 8; i++) {
			int or = radius + thickness / 2;
			int ir = radius - thickness / 2;
			bufg.setColor(colors[i]);

			if (flipIdx == i) {
				or = radius
						+ (int) (thickness / 2 * Math
								.abs(Math.cos(rad(flipPos))));
				ir = radius
						- (int) (thickness / 2 * Math
								.abs(Math.cos(rad(flipPos))));
			}

			double al = rad(i * 45 + 22.5 - 90);
			double ar = rad(i * 45 - 22.5 - 90);
			int ltx = (int) (or * Math.cos(al));
			int lty = (int) (or * Math.sin(al));
			int lbx = (int) (ir * Math.cos(al));
			int lby = (int) (ir * Math.sin(al));
			int rtx = (int) (or * Math.cos(ar));
			int rty = (int) (or * Math.sin(ar));
			int rbx = (int) (ir * Math.cos(ar));
			int rby = (int) (ir * Math.sin(ar));

			bufg.fillPolygon(new int[] { ltx, lbx, rbx, rtx },
					new int[] { lty, lby, rby, rty }, 4);
		}
	}

	private double rad(double a) {
		return Math.toRadians(a);
	}

	@Override
	public void paintFrame(Graphics2D g, int w, int h) {
		g.clearRect(w / 2 - size / 2, h / 2 - size / 2, size, size);
		if (buf != null) {
			AffineTransform transform = AffineTransform
					.getTranslateInstance(w / 2 - size / 2,
							h / 2 - size / 2);
			transform.rotate(rad(pos), size / 2, size / 2);
			g.drawImage(buf, transform, null);
		}
	}

	private void tick() {
		pos = (pos + INCREMENT) % 360;
		flipPos += FLIP_INCREMENT;
		if(flipPos >= 90 && flipPos - FLIP_INCREMENT < 90) {
			colors[flipIdx] = colors[(flipIdx + 7) % 8];
		}
		if (flipPos >= 180) {
			flipPos = 0;
			flipIdx = (flipIdx + 7) % 8;
		}
		drawBuf();
	}

	@Override
	public void run() {
		/* runs the loader */
		while (true) {
			tick();
			gui.repaint();
			if (Thread.interrupted()) {
				return;
			}
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
