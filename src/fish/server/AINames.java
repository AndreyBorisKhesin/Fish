package fish.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AINames {
	private static final List<String> NAMES;
	private static final String NAMES_LOC = "resources/names.txt";

	static {
		NAMES = new ArrayList<String>();

		InputStream in = AINames.class.getClassLoader()
				.getResourceAsStream(NAMES_LOC);
		if (in == null) {
			try {
				in = new FileInputStream(NAMES_LOC);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		BufferedReader br = new BufferedReader(
				new InputStreamReader(in));

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
