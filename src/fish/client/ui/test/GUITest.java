package fish.client.ui.test;

import fish.client.ClientMaster;
import fish.client.ui.FishGUI;
import fish.client.ui.Loader;
import fish.client.ui.MainMenu;
import fish.client.ui.Resources;

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
