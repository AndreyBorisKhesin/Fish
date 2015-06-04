package fish.server.playerinterface;

import fish.server.Server;
import fish.server.messages.PMConnected;
import fish.server.messages.PMPregameUpdate;
import fish.server.messages.PlayerMessage;

public class DummyInterface extends PlayerInterface {
	private static int DIindex = 0;

	public DummyInterface(Server s) {
		super("DUMMY " + DIindex++, s);
		launch();
	}

	@Override
	protected void processMessage(PlayerMessage pm) {
		System.out.println(uname + " received: \n" + pm);

		switch (pm.pmType()) {
		case CONNECTED:
			connected((PMConnected) pm);
			break;
		case PREGAME_UPDATE:
			readyUpdate((PMPregameUpdate) pm);
		}
	}

	private void connected(PMConnected pm) {
		this.id = pm.id;
	}

	private void readyUpdate(PMPregameUpdate pm) {
		System.out.println(pm);
	}

	@Override
	public String toString() {
		return "Dummy player interface: " + uname;
	}
}
