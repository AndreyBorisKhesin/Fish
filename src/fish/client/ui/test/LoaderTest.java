package fish.client.ui.test;

import fish.client.ui.FishGUI;
import fish.client.ui.Loader;
import fish.client.ui.FishGUI.GUIMode;

public class LoaderTest {
	public static void main(String[] args) {
		FishGUI gui = new FishGUI();
		Loader l = new Loader(gui);

		gui.addPainter(GUIMode.LOADER, l);
		gui.switchMode(GUIMode.LOADER);

		l.start();
	}
}
