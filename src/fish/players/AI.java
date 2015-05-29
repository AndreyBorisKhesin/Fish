package fish.players;

import fish.QuantumHand;
import fish.events.Question;

public class AI extends Player {
	private QuantumHand[] hands;

	public void update(Question q) {
		for (int i = 0; i < hands.length; i++) {
			hands[i].update();
		}
	}
}
