package fish.client.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fish.Team;

public class GameLog {

	public final int w, h;

	private BufferedImage buf;
	private Graphics2D bufg;

	private LogEntry head;

	private Font f;
	private FontMetrics fm;

	private int spaceWidth;

	public GameLog(int w, int h) {
		this.w = w;
		this.h = h;
		buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		bufg = buf.createGraphics();
		bufg.setBackground(new Color(0, 0, 0, 0));
		f = Resources.GAME_FONT.deriveFont(15f);
		bufg.setFont(f);
		fm = bufg.getFontMetrics();
		spaceWidth = fm.stringWidth(" ");
	}

	public void addString(String s) {
		int idx = 0;
		String[] words = s.split(" ");
		while (idx < words.length) {
			String line = "";
			while (idx < words.length
					&& fm.stringWidth(line + words[idx]) < w) {
				line += words[idx++] + ' ';
			}

			if (!"".equals(line)) {
				line = line.substring(0, line.length() - 1);
			}

			head = new LogEntry(line, head);
		}

		update();
	}

	private void update() {
		bufg.clearRect(0, 0, w, h);
		int y = h;
		LogEntry entry = head;

		while (y >= 0 && entry != null) {
			int x = 0;
			for (String s : entry.words) {
				if (s.charAt(0) == Team.BLU.delim()) {
					bufg.setColor(Team.BLU.getColor());
					s = s.substring(1);
				} else if (s.charAt(0) == Team.RED.delim()) {
					bufg.setColor(Team.RED.getColor());
					s = s.substring(1);
				} else {
					bufg.setColor(Color.BLACK);
				}

				bufg.drawString(s, x, y - fm.getDescent());
				x += fm.stringWidth(s) + spaceWidth;
			}
			y -= fm.getHeight();
			entry = entry.next;
		}

		/* drop invisible entries off the log */
		if (entry != null) {
			entry.next = null;
		}
	}

	public BufferedImage getImg() {
		return buf;
	}

	private static class LogEntry {
		private String[] words;

		private LogEntry next;

		public LogEntry(String s, LogEntry next) {
			this.words = s.split(" ");
			this.next = next;
		}
	}
}
