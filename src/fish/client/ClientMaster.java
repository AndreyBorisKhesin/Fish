package fish.client;

import fish.client.ui.FishGUI;
import fish.client.ui.Resources;
import fish.client.ui.screens.GameGUI;
import fish.client.ui.screens.Loader;
import fish.client.ui.screens.LocalGameSetup;
import fish.client.ui.screens.MainMenu;
import fish.players.DummyHuman;
import fish.players.Human;
import fish.server.Server;
import fish.server.messages.MStartGame;
import fish.server.messages.SMConnection;
import fish.server.messages.SMReady;
import fish.server.playerinterface.LocalInterface;

/**
 * Manages the operation of the client, including UI, game creation, etc.
 *
 */
public class ClientMaster {

	private FishGUI g;

	private MainMenu menu;
	private Loader loader;

	private GameGUI game;

	private Human player;

	/* unused if connecting to a non-local game */
	private Server server;

	public ClientMaster() {
		g = new FishGUI();
		menu = new MainMenu(this, g);

		game = new GameGUI(g);
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
		g.switchMode(new LocalGameSetup(g, this));
	}

	public void startLocalGame(String uname, int numPlayers) {
		player = new DummyHuman(game, uname);
		game.setPlayer(player);

		/* start server */
		server = new Server();

		/* connect the player */
		server.insertMessage(new SMConnection(new LocalInterface(
				server, player)));
		server.insertMessage(new SMReady(0, true));

		/* connect the ais */
		for (int i = 1; i < numPlayers; i++) {
			server.addAI();
		}

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		server.insertMessage(new MStartGame(0));

		/* switch to the game's point of view */
		g.switchMode(game);
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
