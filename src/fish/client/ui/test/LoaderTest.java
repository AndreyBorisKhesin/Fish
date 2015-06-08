package fish.client.ui.test;

import fish.client.ui.FishGUI;
import fish.client.ui.Loader;

public class LoaderTest {
	public static void main(String[] args) {
		FishGUI gui = new FishGUI();
		Loader l = new Loader(gui);

		gui.switchMode(l);

		l.start();
	}
}
