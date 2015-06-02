package fish.server;

import fish.server.messages.ServerMessage;

/**
 * Defines an interface for the server to use to control part of the game
 *
 */
public interface Controller {

	/**
	 * Indicate to the controller that we are entering its control
	 * 
	 * @param s The server object controlling it
	 */
	public void enter(Server s);

	/**
	 * Handle an incoming message
	 * 
	 * @param sm The incoming message
	 */
	public void handleMessage(ServerMessage sm);

	/**
	 * Indicate to the controller that it is losing control
	 */
	public void exit();
}
