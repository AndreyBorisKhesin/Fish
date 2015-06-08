package fish.client;

import fish.client.ui.FishGUI;
import fish.client.ui.Loader;
import fish.client.ui.MainMenu;
import fish.client.ui.Resources;

/**
 * Manages the operation of the client, including UI, game creation, etc.
 *
 */
public class ClientMaster {

	private FishGUI g;

	private MainMenu menu;
	private Loader loader;

	public ClientMaster() {
		g = new FishGUI();
		menu = new MainMenu(this, g);
	}

	public void runClient() {
		loadResources();
		enterMainMenu();
	}
	
	private void loadResources() {
		startLoader();
		g.switchMode(loader);
		g.start();
		Resources.load();
		loader.end();
	}
	
	private void enterMainMenu() {
		g.switchMode(menu);
	}
	
	private void startLoader() {
		loader = new Loader(g);
		loader.go();
		g.switchMode(loader);
	}

	public void localGame() {
		System.out.println("Local game started");
	}
	
	public void onlineGame() {
		System.out.println("Online game started");
	}

	public void settings() {
		System.out.println("Settings opened");
	}
	
	public void quit() {
		System.out.println("Qutting");
		System.exit(0);
	}
}
