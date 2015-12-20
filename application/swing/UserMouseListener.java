package swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import math.Complex;
import math.FractalCalculator;

public class UserMouseListener implements MouseListener, MouseMotionListener{

	private int x;
	private int y;
	private List<FractalPanel> repaintPanels = new ArrayList<>();
	private FractalCalculator converter;
	private FractalPanel panel;

	public void setFractalCalculator(FractalCalculator c) {
		this.converter = c;
	}
	
	public void addUpdateListener(FractalPanel panel) {
		repaintPanels.add(panel);
	}
	
	public boolean removeUpdateListener(FractalPanel panel) {
		return repaintPanels.remove(panel);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public synchronized Complex getPoint() {
		return converter.convert(x, y);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		this.x = e.getX();
		this.y = e.getY();
		for (FractalPanel p : repaintPanels) {
			p.repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		JPanel p = (JPanel) e.getSource();
		p.requestFocus();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


}
