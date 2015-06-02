package fish.server;

import java.time.Instant;

/**
 * Represents a game event.
 */
public class Event {
	public final Instant timestamp;
	public final String message;

	public Event(String s) {
		timestamp = Instant.now();
		this.message = s;
	}
}
