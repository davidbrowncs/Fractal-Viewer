
package swing;

import java.awt.Color;
import java.awt.Dimension;
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
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			int[] buf = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
			int[] data = fractalCalculator.getIterationsArray();
			System.arraycopy(data, 0, buf, 0, data.length);
			g.drawImage(img, 0, 0, null);
		}
		if (drawer != null && drawer.rectangleCreated()) {
			g.setColor(Color.WHITE);
			g.draw(drawer.getRect());
		}
	}
	
	public void setFractal(Fractal f) {
		fractalCalculator.setFractal(f);
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
	public synchronized void setSize(Dimension d) {
		this.width = (int) d.getWidth();
		this.height = (int) d.getHeight();
	}
}
