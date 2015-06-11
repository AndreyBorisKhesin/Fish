package fish.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AINames {
	private static final List<String> NAMES;
	private static final String NAMES_LOC = "resources/names.txt";

	static {
		NAMES = new ArrayList<String>();

		BufferedReader br = new BufferedReader(
				new InputStreamReader(AINames.class
						.getClassLoader()
						.getResourceAsStream(NAMES_LOC)));

		String s;
		try {
			s = br.readLine();
			while (s != null && !("".equals(s))) {
				NAMES.add(s);
				s = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean[] taken;

	public AINames() {
		taken = new boolean[NAMES.size()];
	}

	public String getName() {
		int i;
		do {
			i = ServerUtil.rand.nextInt(NAMES.size());
		} while (taken[i]);
		taken[i] = true;
		return NAMES.get(i);
	}
}
