package fish.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fish.Card;
import fish.Declaration;
import fish.Question;
import fish.Team;
import fish.server.OtherPlayerData;
import fish.server.PlayerState;
import fish.server.ServerUtil;

public class RandAI extends Player {
	private Map<Card, Integer> known;
	private List<Set<Card>> knownnot;

	private List<Question> questions;

	private Question q;

	public RandAI(String s) {
		this.name = s;
		this.questions = new ArrayList<Question>();
		known = new HashMap<Card, Integer>();
	}

	private Question ask() {
		{
			List<Card> possible = new ArrayList<Card>();
			for (Card c : known.keySet()) {
				int pos = known.get(c);
				if (others.get(pos).t != this.team
						&& hand.getSuit(c.suit).size() > 0) {
					possible.add(c);
				}
			}

			if (possible.size() > 0) {
				int num = ServerUtil.rand.nextInt(possible
						.size());
				return new Question(this.id, known.get(possible
						.get(num)), possible.get(num));
			}
		}
		int askee;
		int card;
		do {
			askee = ServerUtil.rand.nextInt(others.size());
			card = ServerUtil.rand.nextInt(48);
		} while (others.get(askee).t == this.team
				|| hand.getCards().contains(new Card(card))
				|| hand.getSuit(new Card(card).suit).size() == 0
				|| knownnot.get(askee).contains(new Card(card)));

		return new Question(this.id, askee, new Card(card));
	}

	private void instantiate(int numPlayers) {
		knownnot = new ArrayList<Set<Card>>();
		for (int i = 0; i < numPlayers; i++) {
			knownnot.add(new HashSet<Card>());
		}
	}

	@Override
	public void questionResponse(Question q, boolean ans) {
		if (ans == true) {
			known.put(q.c, q.source);
			knownnot.get(q.source).remove(q.c);
		}
		knownnot.get(q.dest).add(q.c);
	}

	@Override
	public void updateGameState(PlayerState p,
			List<OtherPlayerData> others,
			Map<Integer, Team> tricks, int turn, Declaration dec) {
		super.updateGameState(p, others, tricks, turn, dec);
		if (knownnot == null) {
			instantiate(others.size());
		}
		// decides on a question
		if (this.id == this.turn) {
			pi.sendQuestion(ask());
		}
	}

	@Override
	public void questionAsked(Question q) {
		questions.add(q);
	}

	@Override
	public void decStarted(Declaration d) {
		// TODO implement
	}

	@Override
	public void decUpdated(Declaration d) {
		// TODO implement
	}

	@Override
	public void decEnded(Declaration d, int[] locs, boolean succeeded) {
		// TODO implement
	}

	@Override
	public void connected() {
		pi.ready(true);
	}
}
