package fish.ui.test;

import fish.ui.FishGUI;
import fish.ui.FishGUI.GUIMode;

public class GUITest {
	public static void main(String[] args) {
		FishGUI g = new FishGUI();
		g.switchMode(GUIMode.MENU);
	}
}
