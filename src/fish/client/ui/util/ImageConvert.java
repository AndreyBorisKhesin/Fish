package fish.client.ui.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageConvert {
	public static void main(String[] args) {
		// cards();
		backs();
	}

	public static void backs() {
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new File(
					"resources/cards/olds/bb.png"));
			ImageIO.write(transformBack(bi), "png", new File(
					"resources/cards/bb.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bi = ImageIO.read(new File(
					"resources/cards/olds/br.png"));
			ImageIO.write(transformBack(bi), "png", new File(
					"resources/cards/br.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void cards() {
		for (int i = 1; i <= 52; i++) {
			if (i > 24 && i < 29) {
				continue;
			}

			int suit = 0;
			switch ((i - 1) % 4) {
			case 0:
				suit = 0;
				break;
			case 1:
				suit = 6;
				break;
			case 2:
				suit = 2;
				break;
			case 3:
				suit = 4;
				break;
			}
			if (i <= 24) {
				suit++;
			}

			int rank = 5 - ((i - 1) % 28) / 4;

			BufferedImage bi = null;
			try {
				bi = ImageIO.read(new File(
						"resources/cards/olds/" + i
								+ ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			transform(bi, suit + "" + rank + ".png");
		}
		/*
		 * BufferedImage bi = null;
		 * try {
		 * bi = ImageIO.read(new File("resources/cards/olds/bb1.png"));
		 * } catch (IOException e) {
		 * e.printStackTrace();
		 * }
		 * transform(bi, "bb.png");
		 * bi = null;
		 * try {
		 * bi = ImageIO.read(new File("resources/cards/olds/br1.png"));
		 * } catch (IOException e) {
		 * e.printStackTrace();
		 * }
		 * transform(bi, "br.png");
		 */
	}

	public static void transform(BufferedImage bi, String s) {
		BufferedImage n = new BufferedImage(bi.getWidth() - 1,
				bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < bi.getWidth() - 1; i++) {
			for (int j = 0; j < bi.getHeight(); j++) {
				n.setRGB(i, j, bi.getRGB(i, j));
			}
		}

		try {
			ImageIO.write(n, "png", new FileOutputStream(
					"resources/cards/" + s));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static BufferedImage transformBack(BufferedImage bi) {
		BufferedImage n = new BufferedImage(bi.getWidth(),
				bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < bi.getWidth(); i++) {
			for (int j = 0; j < bi.getHeight(); j++) {
				n.setRGB(i, j, bi.getRGB(i, j));
			}
		}

		return n;
	}
}
