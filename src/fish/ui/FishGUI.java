package fish.ui;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class FishGUI extends JPanel {
	private JFrame frame;
	private Container cpane;

	public Resolution size;

	public BufferedImage buf;
	public Painter p;

	public FishGUI() {
		frame = new JFrame("Fish");
		cpane = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(this);

		setSize(Resolution._1280_720);
		
		cpane.setLayout(null);
		frame.setVisible(true);
		frame.repaint();
	}

	public void setSize(Resolution r) {
		size = r;
		cpane.setSize(r.d);
		frame.setSize(r.d);
		this.setBounds(0, 0, r.d.width, r.d.height);
	}

	@Override
	public void paintComponent(Graphics g) {

	}
}
