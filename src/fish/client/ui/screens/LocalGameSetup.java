package fish.client.ui.screens;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import fish.client.ClientMaster;
import fish.client.ui.FishGUI;
import fish.client.ui.Resources;
import fish.client.ui.elements.Button;
import fish.client.ui.elements.RadioButton;
import fish.client.ui.elements.UIElement;

public class LocalGameSetup extends GUIScreen {

	private RadioButton numAI;

	private Button start;

	private UIElement[] elements;

	private FishGUI gui;

	private ClientMaster cm;

	public LocalGameSetup(FishGUI gui, ClientMaster cm) {
		super(gui);
		numAI = new RadioButton(200, 200, "Number of Players:",
				new String[] { "4", "6", "8", "12" },
				this::setSelection);
		start = new Button(490, 600, 300, 100, "START",
				Resources.MENU_OPTION_FONT, this::startGame);
		elements = new UIElement[] { numAI, start };

		this.gui = gui;

		this.cm = cm;

		/* set the initial selection to 6 */
		numAI.setSelection(1);
	}

	public void setSelection(int s) {
		System.out.println("Option chosen: " + s);
	}

	public void startGame() {
		System.out.println("Game started with option: "
				+ numAI.getSelection());
		cm.startLocalGame("Player",
				new int[] { 4, 6, 8, 12 }[numAI.getSelection()]);
	}

	@Override
	public void paintFrame(Graphics2D g, int w, int h) {
		for (UIElement el : elements) {
			el.draw(g);
		}
	}

	@Override
	public void mousePressed(int x, int y, int button) {
		for (UIElement el : elements) {
			if (button == MouseEvent.BUTTON1) {
				el.mouseLeftClick(x, y, gui);
			}
			if (button == MouseEvent.BUTTON2) {
				el.mouseRightClick(x, y, gui);
			}
		}
	}

	@Override
	public void mouseMoved(int x, int y) {
		for (UIElement el : elements) {
			el.mouseMoved(x, y, gui);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}
}
