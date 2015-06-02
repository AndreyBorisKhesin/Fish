package fish.server;

import java.util.ArrayList;
import java.util.List;

import fish.Util;
import fish.server.Server.ServerState;
import fish.server.messages.PMConnected;
import fish.server.messages.PMPregameUpdate;
import fish.server.messages.SMConnection;
import fish.server.messages.SMReady;
import fish.server.messages.SMStartGame;
import fish.server.messages.ServerMessage;
import fish.server.playerinterface.PlayerInterface;

public class PregameController implements Controller {

	/**
	 * List of readied players
	 */
	private List<Boolean> readied;

	private Server s;

	@Override
	public void enter(Server s) {
		/* we must handle connections and readying */
		this.s = s;
		readied = new ArrayList<Boolean>();
	}

	@Override
	public void handleMessage(ServerMessage sm) {
		switch (sm.getType()) {
		case CONNECTION:
			connection((SMConnection) sm);
			break;
		case READY_UPDATE:
			readyUpdate((SMReady) sm);
			break;
		case START_GAME:
			startGame((SMStartGame) sm);
			break;
		}
	}

	private void connection(SMConnection sm) {
		s.clients.add(sm.pi);
		sm.pi.insertMessage(new PMConnected(s.clients.size() - 1));
		readied.add(false);
		sendPregameUpdate();
	}

	private void readyUpdate(SMReady sm) {
		readied.set(sm.id, sm.ready);
		sendPregameUpdate();
	}
	
	private void startGame(SMStartGame sm) {
		if(sm.getId() != 0 || !Util.validPlayerNum(s.clients.size())) {
			return;
		}
		for(Boolean b : readied) {
			if(!b) {
				return;
			}
		}
		
		s.switchState(ServerState.GAME);
	}

	private void sendPregameUpdate() {
		List<String> usernames = new ArrayList<String>();
		for (PlayerInterface p : s.clients) {
			usernames.add(p.getUname());
		}

		PMPregameUpdate pm = new PMPregameUpdate(usernames, readied);

		for (PlayerInterface p : s.clients) {
			p.insertMessage(pm);
		}
	}

	@Override
	public void exit() {
		// TODO: implement
	}
}