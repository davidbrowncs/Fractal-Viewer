
package swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import math.FractalCalculator;
import util.SizeListener;
import fractals.Fractal;

public class FractalPanel extends JPanel {

	private static final long serialVersionUID = -2893642519163535876L;

	private List<SizeListener> sizeListeners = new ArrayList<>();
	private FractalDrawer drawer;
	private ColorScheme scheme;

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (drawer != null) {
			drawer.draw((Graphics2D) g);
		}
//		g.setColor(Color.CYAN);
//		g.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2 );
//		g.drawLine(this.getWidth() / 2, 0, this.getWidth() / 2, this.getHeight());
	}

	public void setColorScheme(ColorScheme scheme) {
		this.scheme = scheme;
	}
	
	public void setFractal(Fractal f) {
		drawer.setFractal(f);
	}
	
	public ColorScheme getColorScheme() {
		return scheme;
	}

	public FractalCalculator getFractalCalculator() {
		return drawer.getFractalCalculator();
	}

	public RectangleDrawer getRectangleDrawer() {
		return drawer.getRectangleDrawer();
	}
	
	FractalPanel() {
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				for (SizeListener l : sizeListeners) {
					l.setSize(FractalPanel.this.getSize());
				}
				repaint();
			}

			@Override
			public void componentMoved(ComponentEvent e) {}

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {}

		});
	}

	public void addSizeListener(SizeListener listener) {
		sizeListeners.add(listener);
	}

	public void setDrawer(FractalDrawer drawer) {
		if (this.drawer != null) {
			sizeListeners.remove(this.drawer);
		}
		this.drawer = drawer;
		repaint();
	}
}
