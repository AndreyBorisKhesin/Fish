package fish.server;

import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Log implements Runnable {

	private static final Log L = new Log();
	static {
		Thread t = new Thread(L);
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Log a message
	 */
	public static void log(String s) {
		L.queue.add(new LogMessage(s));
		synchronized (L.queue) {
			L.queue.notify();
		}
	}

	/**
	 * The queue of messages to output
	 */
	private ConcurrentLinkedQueue<LogMessage> queue;

	public Log() {
		queue = new ConcurrentLinkedQueue<LogMessage>();
	}

	@Override
	public void run() {
		while (true) {
			LogMessage lm = ServerUtil.waitOnQueue(queue);
			output("[" + lm.t.toString() + "]: " + lm.s);
		}
	}

	private void output(String s) {
		System.err.println(s);
	}

	private static class LogMessage {
		private String s;
		private Instant t;

		LogMessage(String s) {
			this.s = s;
			this.t = Instant.now();
		}
	}
}
