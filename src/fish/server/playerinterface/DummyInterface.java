package fish.server.playerinterface;

import java.util.Random;

import fish.server.Server;
import fish.server.messages.PMConnected;
import fish.server.messages.PMPregameUpdate;
import fish.server.messages.PlayerMessage;
import fish.server.messages.SMConnection;
import fish.server.messages.SMReady;
import fish.server.messages.ServerMessage;

public class DummyInterface extends PlayerInterface {
	private static int DIindex = 0;

	private volatile boolean running;

	public DummyInterface(Server s) {
		super("DUMMY " + DIindex++, s);
		launch();
	}

	@Override
	public void run() {
		addServerMessage(new SMConnection(this));
		running = true;

		while (running) {
			PlayerMessage pm = Server.waitOnQueue(mqueue);

			System.out.println(uname + " received: \n" + pm);

			boolean a = false;
			if(a) {
				s.insertMessage(new SMReady(id, true));
			}
			
			switch (pm.getType()) {
			case CONNECTED:
				connected((PMConnected) pm);
				break;
			case PREGAME_UPDATE:
				readyUpdate((PMPregameUpdate) pm);
			}
		}
	}

	private void connected(PMConnected pm) {
		this.id = pm.id;
	}

	private void readyUpdate(PMPregameUpdate pm) {
		System.out.println(pm);
	}

	private void stop() {
		running = false;
	}

	private void addServerMessage(ServerMessage m) {
		System.out.println(m);
		s.insertMessage(m);
	}

	@Override
	public String toString() {
		return "Dummy player interface: " + uname;
	}
}
