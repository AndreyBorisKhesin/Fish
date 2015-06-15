package fish.server.playerinterface;

import fish.players.Human;
import fish.players.Player;
import fish.server.Server;
import fish.server.messages.MDecStart;
import fish.server.messages.MDecUpdate;
import fish.server.messages.MQuestion;
import fish.server.messages.PMConnected;
import fish.server.messages.PMGameState;
import fish.server.messages.PMResponse;
import fish.server.messages.PlayerMessage;

/**
 * A player interface for a player connected locally. It is passed a player
 * object to connect to the server.
 *
 */
public class LocalInterface extends PlayerInterface {

	/**
	 * The player we are interfacing with
	 */
	private Player p;

	/**
	 * Connects the given player to the server
	 * 
	 * @param s The server object we wish to connect this player to
	 * @param p The player wishing to join the game
	 */
	public LocalInterface(Server s, Player p) {
		super(p.getName(), s);

		this.p = p;
		p.setPlayerInterface(this);

		launch();
	}

	protected void processMessage(PlayerMessage pm) {
		switch (pm.pmType()) {
		case Q_ASKED:
			p.questionAsked(((MQuestion) pm).q);
			break;
		case Q_RESPONSE:
			p.questionResponse(((PMResponse) pm).q,
					((PMResponse) pm).res);
			break;
		case DEC_STARTED:
			p.decStarted(((MDecStart) pm).d);
			break;
		case DEC_UPDATE:
			p.decStarted(((MDecUpdate) pm).d);
			break;
		case DEC_ENDED:
			p.decStarted(((MDecStart) pm).d);
			break;
		case GAME_STATE:
			PMGameState gs = (PMGameState) pm;
			p.updateGameState(gs.pstate, gs.otherplayers,
					gs.tricks, gs.turn, gs.dec);
			break;
		case CONNECTED:
			this.id = ((PMConnected) pm).id;
			p.connected();
			break;
		default:
			System.out.println("Unsupported message received: "
					+ pm);
			break;
		}
	}
}
