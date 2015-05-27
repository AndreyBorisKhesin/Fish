package fish;

/**
 * General utility class
 *
 */
public class Util {
	
	/**
	 * Determines if a given number is a valid number of players
	 * 
	 * @return true if it is a valid number of players, false otherwise
	 */
	public static boolean validPlayerNum(int n) {
		return n == 4 || n == 6 || n == 8 || n == 12;
	}
}
