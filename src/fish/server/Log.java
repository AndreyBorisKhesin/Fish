package fish.server;

import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Log implements Runnable {

	private static final Log L = new Log();
	{
		(new Thread(L)).run();
	}
	
	/**
	 * Log a message
	 */
	public static void log(String s) {
		L.queue.add(new LogMessage(s));
		L.logThread.interrupt();
	}
	
	/**
	 * The queue of messages to output
	 */
	private ConcurrentLinkedQueue<LogMessage> queue;
	
	/**
	 * Thread to be notified about new messages
	 */
	private Thread logThread;

	public Log() {
		queue = new ConcurrentLinkedQueue<LogMessage>();
	}
	
	@Override
	public void run() {
		logThread = Thread.currentThread();
		while(true) {
			while(queue.isEmpty()) {
				try {
					Thread.sleep(1000);
				} catch(InterruptedException e) {
					/* message added */
				}
			}
			
			/* we are the only consumer so we can safely dequeue */
			LogMessage lm = queue.poll();
			output("[" + lm.toString() + "]: " + lm.s);
		}
	}
	
	private void output(String s) {
		System.err.println(s);
	}
	
	private static class LogMessage {
		private String s;
		private OffsetDateTime t;
		
		LogMessage(String s) {
			this.s = s;
			this.t = OffsetDateTime.now();
		}
	}
}
