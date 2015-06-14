package fish.players;

import java.util.*;

import fish.*;
import fish.server.OtherPlayerData;
import fish.server.PlayerState;

import static fish.Util.isZero;

public class AI extends Player {
	private QuantumHand[] hands;

	private static int nameCounter = 0;

	private List<Question> questions;

	private Question q;

	public AI(String s) {
		this.name = s;
	}

	private void instantiate(int numPlayers) {
		hands = new QuantumHand[numPlayers];
		for(int i = 0; i < numPlayers; i++) {
			if(i == this.id) continue;
			hands[i] = new QuantumHand(numPlayers);
		}
		hands[id] = new QuantumHand(this.hand);
		
		// FIXME: rebalance();
	}
	
	@Override
	public void questionResponse(Question q, boolean ans) {
		if (hands[q.source].getHand().getSuit(q.c.suit).size() == 0) {
			hands[q.source].getBounds()[q.c.suit][0] =
					Math.max(hands[q.source].getBounds()[q.c.suit][0], 1);
		}
		if (ans) {
			for (QuantumHand hand : hands) {
				hand.zero(q.c);
			}
			hands[q.dest].fix(q.c);
		} else {
			hands[q.source].zero(q.c);//TODO FIXME FIXME FIXME
			//FIXME battles and bluffing
			hands[q.dest].zero(q.c);
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < hands.length; j++) {
				if (j != id) {
					int min = 0;
					int max = 0;
					for (int k = 0; k < hands.length; k++) {
						if (k == id) {
							min += hand.getSuit(i).size();
							max += hand.getSuit(i).size();
						} else if (k != j) {
							min += hands[k].getHand().getSuit(i).size() +
									hands[k].getBounds()[i][0];
							max += hands[k].getHand().getSuit(i).size() +
									hands[k].getBounds()[i][1];
						}
					}
					hands[j].getBounds()[i][0] =
							Math.max(hands[j].getBounds()[i][0], 8 - max);
					hands[j].getBounds()[i][1] =
							Math.min(hands[j].getBounds()[i][1], 8 - min);
				}
				hands[j].move(i);
			}
		}
		double[][] array = new double[hands.length * 9][48];
		int[] units = new int[hands.length * 9];
		for (int i = 0; i < hands.length; i++) {
			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < 48; k++) {
					array[9 * i + j][k] = hands[i].get(k);
				}
				if (j == 8 || hands[i].getMoved()[j]) {
					units[9 * i + j] = hands[i].getBounds()[j][0];
				} else {
					units[9 * i + j] = 0;
				}
			}
		}
		Set<Card> known = new HashSet<>();
		known.addAll(hand.getCards());
		for (QuantumHand hand : hands) {
			known.addAll(hand.getHand().getCards());
		}
		for (int i = 0; i < 48; i++) {
			if (known.contains(new Card(i))) {
				for (int j = 0; j < array.length; j++) {
					array[j][i] = 0;
				}
			}
		}
		Matrix matrix = new Matrix(array);
		for (int i = 0; i < 20; i++) {
			matrix = matrix.diagonalReciprocal(matrix.sum(), units);
			matrix = matrix.t().diagonalReciprocal(matrix.t().sum()).t();
		}
		for (int i = 0; i < hands.length; i++) {
			for (int j = 0; j < 9; j++) {
				hands[i].getQuantumHand()[j].clear();
				for (int k = 0; k < 48; k++) {
					if (j == 8 && hands[i].getMoved()[new Card(k).suit]) {
						continue;
					}
					if (j != 8 && !hands[i].getMoved()[new Card(k).suit]) {
						continue;
					}
					if (!isZero(matrix.m[9 * i + j][k])) {
						hands[i].getQuantumHand()[j].put
								(new Card(k), matrix.m[9 * i + j][k]);
					}
				}
			}
		}
		for (int i = 0; i < hands.length; i++) {
			for (Card c : hands[i].check()) {
				for (int j = 0; j < hands.length; j++) {
					hands[j].zero(c);
				}
			}
		}
		q = null;
		l: for (int i = 0; i < hands.length; i++) {
			if (i == id) {
				continue;
			}
			for (Card c : hands[i].getHand().getCards()) {
				if (hand.getSuit(c.suit).size() > 0) {
					q = new Question(id, i, c);
					break l;
				}
			}
		}
		//FIXME battles and bluffing
		if (q == null) {
			double max = 0;
			for (int i = 0; i < hands.length; i++) {
				if (i == id) {
					continue;
				}
				for (int j = 0; j < 48; j++) {
					if (hands[i].get(j) > max
							&& hand.getSuit(j / 6).size() > 0) {
						max = hands[i].get(j);
						q = new Question(id, i, new Card(j));
					}
				}
			}
		}
	}

	@Override
	public void updateGameState(PlayerState p,
			List<OtherPlayerData> others,
			Map<Integer, Team> tricks, int turn, Declaration dec) {
		super.updateGameState(p, others, tricks, turn, dec);
		
		if(this.hands == null) {
			instantiate(others.size());
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
