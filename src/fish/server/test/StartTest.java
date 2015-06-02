package fish.server.test;

import java.util.ArrayList;
import java.util.List;

import fish.server.Server;
import fish.server.messages.SMReady;
import fish.server.messages.SMStartGame;
import fish.server.playerinterface.DummyInterface;

public class StartTest {
	public static void main(String[] args) {
		Server s = new Server();
		List<DummyInterface> dummies = new ArrayList<DummyInterface>();

		final int num = 6;
		for (int i = 0; i < num; i++) {
			dummies.add(new DummyInterface(s));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
		}

		for (int i = 0; i < num; i++) {
			s.insertMessage(new SMReady(i, true));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
		}
		
		s.insertMessage(new SMStartGame(0));
	}
}
