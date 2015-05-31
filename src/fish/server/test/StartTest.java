package fish.server.test;

import java.util.ArrayList;
import java.util.List;

import fish.server.Server;
import fish.server.playerinterface.DummyInterface;

public class StartTest {
	public static void main(String[] args) {
		Server s = new Server();
		List<DummyInterface> dummies = new ArrayList<DummyInterface>();
		
		final int num = 6;
		for(int i = 0; i < num; i++) {
			dummies.add(new DummyInterface(s));
			try {
				Thread.sleep(20);
			} catch(InterruptedException e) {
				
			}
		}
	}
}
