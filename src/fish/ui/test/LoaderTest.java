package fish.ui.test;

import fish.ui.FishGUI;
import fish.ui.FishGUI.GUIMode;
import fish.ui.Loader;

public class LoaderTest {
	public static void main(String[] args) {
		FishGUI gui = new FishGUI();
		Loader l = new Loader(gui);

		gui.addPainter(GUIMode.LOADER, l);
		gui.switchMode(GUIMode.LOADER);

		l.start();
	}
}
