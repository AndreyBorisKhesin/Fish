package fish.players;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fish.Declaration;
import fish.Hand;
import fish.Question;
import fish.Team;
import fish.server.OtherPlayerData;
import fish.server.PlayerState;
import fish.server.playerinterface.PlayerInterface;

/**
 * An interface for all Humans and AI's playing the game.
 */
public abstract class Player {
	public Hand hand;

	public Team team;

	public String name;

	public int id;

	public int seat;

	public Map<Integer, Team> tricks;

	public List<OtherPlayerData> others;

	public int turn;

	public Declaration dec;

	public PlayerInterface pi;

	public String getName() {
		return name;
	}

	public void setPlayerInterface(PlayerInterface pi) {
		this.pi = pi;
	}

	public void updateGameState(PlayerState p,
			List<OtherPlayerData> others,
			Map<Integer, Team> tricks, int turn, Declaration dec) {
		this.hand = p.hand;
		this.team = p.team;
		this.id = p.id;
		this.seat = p.seat;
		this.tricks = tricks;
		this.others = others;
		this.tricks = tricks;
		this.turn = turn;
		this.dec = dec;
	}

	public abstract void questionAsked(Question q);

	public abstract void questionResponse(Question q, boolean works);

	public abstract void decStarted(Declaration d);

	public abstract void decUpdated(Declaration d);

	public abstract void decEnded(Declaration d, int[] locs, boolean succeeded);

	public abstract void connected();
}
