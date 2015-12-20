
package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import math.FractalCalculator;
import util.SizeListener;
import fractals.Fractal;

public class FractalDrawer implements SizeListener {

	private FractalCalculator fractalCalculator;
	private RectangleDrawer drawer;

	private int width;
	private int height;

	public void draw(Graphics2D g) {
		if (fractalCalculator.isUpdateable()) {
			fractalCalculator.update();
		}
		if (width != 0 && height != 0) {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			int[] buf = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
			int[] data = fractalCalculator.getIterationsArray();
			System.arraycopy(data, 0, buf, 0, data.length);
			g.drawImage(image, 0, 0, null);
		}
		if (drawer != null && drawer.rectangleCreated()) {
			g.setColor(Color.WHITE);
			g.draw(drawer.getRect());
		}
	}

	public void setFractal(Fractal f) {
		fractalCalculator.setFractal(f);
	}

	public BufferedImage getImage(FractalCalculator c, int width, int height, double xMin, double xMax, double yMin,
			double yMax) {
		int[] data = FractalCalculator.getColourArray(width, height, c, xMin, xMax, yMin, yMax);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] buf = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		System.arraycopy(data, 0, buf, 0, data.length);
		return img;
	}

	public RectangleDrawer getRectangleDrawer() {
		return drawer;
	}

	public void setRectangleDrawer(RectangleDrawer drawer) {
		this.drawer = drawer;
	}

	public void setFractalCalculator(FractalCalculator fractalCalculator) {
		this.fractalCalculator = fractalCalculator;
	}

	public FractalCalculator getFractalCalculator() {
		return fractalCalculator;
	}

	@Override
	public void setSize(Dimension d) {
		this.width = (int) d.getWidth();
		this.height = (int) d.getHeight();
	}
}
