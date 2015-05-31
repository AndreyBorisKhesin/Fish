package fish.server.playerinterface;

import fish.server.Server;
import fish.server.messages.PMConnected;
import fish.server.messages.PlayerMessage;
import fish.server.messages.SMConnection;
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

			switch (pm.getType()) {
			case CONNECTED:
				connected((PMConnected) pm);
				break;
			}
			System.out.println(id);
		}
	}

	private void connected(PMConnected pm) {
		this.id = pm.id;
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
