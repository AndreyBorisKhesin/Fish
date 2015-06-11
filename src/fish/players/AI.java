package fish.players;

import java.util.*;

import fish.*;
import fish.server.OtherPlayerData;
import fish.server.PlayerState;

import static fish.Util.isZero;

public class AI extends Player {
	private static int nameCounter = 0;

	public AI() {
		/*FIXME need an argument for number of players
		hands = new QuantumHand[THAT NUMBER];
		hands[id] = new QuantumHand(hand);*/
		synchronized (AI.class) {
			this.name = "AI# " + ++nameCounter;
		}
	}

	private QuantumHand[] hands;

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
					if (hands[i].getQuantumHand()[j].containsKey(new Card(k))
							&& !isZero(hands[i].getProb(new Card(k)))) {
						array[9 * i + j][k] = hands[i].getProb(new Card(k));
					}
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
			matrix = matrix.x(Matrix.diagonalReciprocal(matrix.sum(), units));
			matrix = matrix.t().x
					(Matrix.diagonalReciprocal(matrix.t().sum())).t();
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
					if (!isZero(matrix._[9 * i + j][k])) {
						hands[i].getQuantumHand()[j].put
								(new Card(k), matrix._[9 * i + j][k]);
					}
				}
			}
		}
		for (QuantumHand hand : hands) {
			hand.check();
		}
//		Set<Card> known = new HashSet<>();
//		known.addAll(hand.getCards());
//		List<Integer> units = new ArrayList<>();
//		for (int i = 0; i < hands.length; i++) {
//			for (int j = 0; j < 9; j++) {
//				units.add(hands[i].getBounds()[j][0]);
//			}
//		}
//		double[][] array = new double[hands.length][48];
//		for (int i = 0, j = 0; i < 48; i++) {
//			if (!known.contains(new Card(i))) {
//				for (int k = 0; k < units.size(); k++) {
//					array[j][k] = hands[players.get(j)].getProb(new Card(i));
//				}
//				j++;
//			}
//		}
		//TODO list
		/*
		iterate through quantum hands
		adjust bounds
		update hands to remove cards
		sum decided cards
		construct a _ (p * 6 tall) (48 - decided cards wide)
		apply hocus pocus twice
		average
		rebuild all of the quantum hands
		*/
//		for (int i = 0; i < 8; i++) {
//			for (int j = 0; j < hands.length; j++) {
//				if (j != id) {
//					int min = 0;
//					int max = 0;
//					for (int k = 0; k < hands.length; k++) {
//						if (k != j) {//FIXME what about my cards?????
//							min += hands[k].getBounds()[i][0];
//							max += hands[k].getBounds()[i][1];
//						} else {
//							min += hand.getSuit(i)//FIXME maybe i was drunk?
//									.size();
//							max += hand.getSuit(i)
//									.size();
//						}
//					}
//					hands[j].getBounds()[i][0] = Math
//							.max(hands[j].getBounds()[i][0],
//									6 - max);
//					hands[j].getBounds()[i][1] = Math
//							.min(hands[j].getBounds()[i][1],
//									6 - min);
//				}
//			}
//		}
//		int source = q.source;
//		int dest = q.dest;
//		Card c = q.c;
//		if (ans) {
//
//		} else {
//			QuantumHand sQh = hands[source];
//			/* FIXME battles and bluffing */
//			sQh.zero(c);
//			/* FIXME battles and bluffing */
//			hands[dest].zero(c);
//			if (sQh.getHand().getSuit(c.suit).size() == 0) {//FIXME still drunk
//				sQh.getBounds()[c.suit][0] = Math.max(
//						sQh.getBounds()[c.suit][0], 1);
//			}
//		}
//		for (QuantumHand hand : hands) {
//			hand.update();
//		}
	}

	@Override
	public void updateGameState(PlayerState p,
			List<OtherPlayerData> others,
			Map<Integer, Team> tricks, int turn, Declaration dec) {
		// TODO implement
	}

	@Override
	public void questionAsked(Question q) {
		// TODO implement

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

	/**
	 * Allow the clientmaster to ready us up
	 */
	@Override
	public void connected() {
	}
}
