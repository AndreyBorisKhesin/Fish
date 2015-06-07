package fish.client.ui.test;

import fish.client.ui.FishGUI;
import fish.client.ui.Loader;
import fish.client.ui.MainMenu;
import fish.client.ui.Resources;
import fish.client.ui.FishGUI.GUIMode;

public class GUITest {
	public static void main(String[] args) {
		FishGUI g = new FishGUI();
		MainMenu menu = new MainMenu();
		Loader l = new Loader(g);
		l.start();
		g.addPainter(GUIMode.LOADER, l);
		g.switchMode(GUIMode.LOADER);
		g.addPainter(GUIMode.MENU, menu);
		Resources.load();
		g.switchMode(GUIMode.MENU);
	}
}
