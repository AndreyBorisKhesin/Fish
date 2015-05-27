package fish;

/**
 * General class for utility variables and methods.
 */
public class Util {

	/**
	 * Determines if a given number is a valid number of players.
	 *
	 * @param n The proposed number of players.
	 * @return True if n is a valid number of players, false otherwise.
	 */
	public static boolean validPlayerNum(int n) {
		return n == 4 || n == 6 || n == 8 || n == 12;
	}
}
