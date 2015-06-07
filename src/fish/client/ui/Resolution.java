package fish.client.ui;

import java.awt.Dimension;

public enum Resolution {
	_1280_720(1280, 720), _1980_1080(1980, 1080), _576_1024(576, 1024);

	public final Dimension d;
	public final String name;

	private Resolution(int w, int h) {
		d = new Dimension(w, h);
		name = d.width + " by " + d.height;
	}
}
