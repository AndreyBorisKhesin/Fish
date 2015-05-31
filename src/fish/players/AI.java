package fish.players;

import fish.Card;
import fish.QuantumHand;
import fish.events.Question;

public class AI extends Player {
	private QuantumHand[] hands;

	public void update(Question q, boolean ans) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < hands.length; j++) {
				if (j != id) {
					int min = 0;
					int max = 0;
					for (int k = 0; k < hands.length; k++) {
						if (k != j) {
							min += hands[k].getBounds()[i][0];
							max += hands[k].getBounds()[i][1];
						} else {
							min += hand.getSuit(i).size();
							max += hand.getSuit(i).size();
						}
					}
					hands[j].getBounds()[i][0] =
							Math.max(hands[j].getBounds()[i][0], 6 - max);
					hands[j].getBounds()[i][1] =
							Math.min(hands[j].getBounds()[i][1], 6 - min);
				}
			}
		}
		int source = q.source;
		int dest = q.dest;
		Card c = q.c;
		if (ans) {

		} else {
			QuantumHand sQh = hands[source];
			/* FIXME battles and bluffing */
			sQh.zero(c);
			/* FIXME battles and bluffing */
			hands[dest].zero(c);
			if (sQh.getHand().getSuit(c.suit).size() == 0) {
				sQh.getBounds()[c.suit][0] =
						Math.max(sQh.getBounds()[c.suit][0], 1);
			}
		}
		for (QuantumHand hand : hands) {
			hand.update();
		}
	}
}
