package swing;

import math.FractalCalculator;
import fractals.Fractal;

public final class FractalPanelGenerator {

	private FractalPanelGenerator() {}

	public static FractalPanel newFractalPanel(Fractal fractal, UserMouseListener listener) {
		FractalPanel panel = new FractalPanel();
		FractalDrawer drawer = new FractalDrawer();
		RectangleDrawer rectDrawer = new RectangleDrawer(panel);
		drawer.setRectangleDrawer(rectDrawer);
		ColorScheme scheme = new ColorScheme();
		panel.setColorScheme(scheme);
		FractalCalculator fractalCalculator = new FractalCalculator(panel.getWidth(), panel.getHeight(), fractal, listener, scheme);
		rectDrawer.setCalculator(fractalCalculator);
		drawer.setFractalCalculator(fractalCalculator);
		panel.setDrawer(drawer);
		panel.addSizeListener(fractalCalculator);
		panel.addSizeListener(drawer);
		panel.addSizeListener(rectDrawer.getDrawingRectangle());
		return panel;
	}
}
