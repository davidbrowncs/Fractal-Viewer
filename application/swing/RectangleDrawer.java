
package swing;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import math.FractalCalculator;

public class RectangleDrawer implements MouseListener, MouseMotionListener {

	private DrawingRectangle rect = new DrawingRectangle();
	private FractalPanel panel;
	private FractalCalculator calculator;

	public RectangleDrawer(FractalPanel panel) {
		this.panel = panel;
	}
	
	public DrawingRectangle getDrawingRectangle() {
		return rect;
	}

	public void setCalculator(FractalCalculator calculator) {
		this.calculator = calculator;
	}

	public boolean rectangleCreated() {
		return rect.isCreated();
	}

	public Rectangle getRect() {
		return rect.getRect();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			rect.update(e.getX(), e.getY());
			panel.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			rect.create(e.getX(), e.getY());
			calculator.setUpdateable(false);
			panel.repaint();
		} else if (SwingUtilities.isRightMouseButton(e)) {
			calculator.previousRange();
			calculator.setUpdateable(true);
			panel.repaint();
			rect.destroy();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (rect.isCreated()) {
				calculator.setUpdateable(true);
				Rectangle bounds = rect.getRect();
				calculator.updateRange(bounds.x, bounds.x + bounds.width, bounds.y, bounds.y + bounds.height);
				rect.destroy();
				panel.repaint();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

}
