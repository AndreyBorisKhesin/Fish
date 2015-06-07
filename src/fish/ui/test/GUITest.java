package fish.ui.test;

import fish.ui.FishGUI;
import fish.ui.FishGUI.GUIMode;
import fish.ui.MainMenu;

public class GUITest {
	public static void main(String[] args) {
		FishGUI g = new FishGUI();
		g.addPainter(GUIMode.MENU, new MainMenu());
		g.switchMode(GUIMode.MENU);
	}
}
