package fish.client.ui.test;

import fish.client.ClientMaster;
import fish.client.ui.FishGUI;
import fish.client.ui.Resources;
import fish.client.ui.screens.Loader;
import fish.client.ui.screens.MainMenu;

public class GUITest {
	public static void main(String[] args) {
		ClientMaster c = new ClientMaster();
		FishGUI g = new FishGUI();
		MainMenu menu = new MainMenu(c, g);
		Loader l = new Loader(g);
		l.go();
		g.switchMode(l);
		Resources.load();
		g.switchMode(menu);
		l.end();
	}
}
