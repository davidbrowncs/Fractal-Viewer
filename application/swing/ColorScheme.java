
package swing;

public class ColorScheme implements Cloneable {

	private int redComp = 0;
	private int greenComp = 100;
	private int blueComp = 250;

	public int getRGB(double fraction) {
		return (colourNum(fraction, redComp) & 0xFF) << 16 | (colourNum(fraction, greenComp) & 0xFF) << 8
				| (colourNum(fraction, blueComp) & 0xFF);
	}

	public int getRGBContinuous(double num) {
		return getRGB((double) (num / Math.random()));
		// return getRGB(num % 0xFFFFFF);
		// return ((int) num) & 0xFFFFFF;
		// int val = (int) (0xFFFFFF % num);
		// return (val & 0xFF0000) << 16 | (val & 0xFF00) << 8 | (val & 0xFF);
	}

	private double convert(double fraction) {
		return (fraction);
	}

	private int colourNum(double fraction, int comp) {
		return (int) (comp * convert(fraction));
	}

	public ColorScheme clone() {
		try {
			return (ColorScheme) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
