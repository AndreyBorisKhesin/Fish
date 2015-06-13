package fish.client.test;

import fish.client.ClientMaster;

public class GameTest {
	/**
	 * Indicates the server should always set turn to 0 when the game starts
	 */
	// FIXME: Remove this
	public static boolean CHEATING = true;

	public static void main(String[] args) {
		new ClientMaster().runClient();
	}
}
