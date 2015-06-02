package fish.server.messages;

import java.util.List;

public class PMPregameUpdate implements PlayerMessage {

	@Override
	public PMType pmType() {
		return PMType.PREGAME_UPDATE;
	}

	public final List<String> usernames;

	public final List<Boolean> ready;
	
	public PMPregameUpdate(List<String> usernames, List<Boolean> ready) {
		this.usernames = usernames;
		this.ready = ready;
	}
	
	@Override
	public String toString() {
		return "Player Message: PREGAME_UPDATE\nusernames: " + usernames + ",\nready: " + ready;
	}
}
