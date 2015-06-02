package fish.server;

import static fish.server.Log.log;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Misc utilities for running the game
 *
 */
public class ServerUtil {

	/**
	 * Random object to be used for all randomness in the game
	 */
	public static final Random rand = new SecureRandom();

	/**
	 * This should be run in the thread that will be interrupted on new
	 * messages. No one else should be waiting on this queue.
	 * 
	 * @param q The queue to wait on
	 * @return An object that will be delivered in the queue
	 */
	public static <T> T waitOnQueue(ConcurrentLinkedQueue<T> q) {
		while (q.isEmpty()) {
			synchronized (q) {
				try {
					q.wait();
				} catch (InterruptedException e) {
				}
			}
		}

		/*
		 * there are no other consumers of this queue so we can
		 * safely dequeue
		 */
		T t = q.poll();
		if (t == null) {
			log("attempted to dequeue waiting queue and found it empty");
		}

		return t;
	}
}
