package fish.events;

import java.time.Instant;

/**
 * Represents a game event.
 */
public abstract class Event {
	public final Instant timestamp;

	public Event() {
		timestamp = Instant.now();
	}

	public enum EventType {
		QUESTION, DEC_START, DEC_END;
	}
}
