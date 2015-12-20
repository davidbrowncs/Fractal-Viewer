package fractals;

import math.Complex;

public interface Fractal {
	
	public int iterations(Complex c, int maxNumIterations, double divergenceLimit, Complex userPoint);
	
	public Fractal getJulia();
}
