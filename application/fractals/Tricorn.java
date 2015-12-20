
package fractals;

import math.Complex;

public class Tricorn implements Fractal {

	@Override
	public int iterations(Complex c, int maxNumIterations, double divergenceLimit, Complex userPoint) {
		Complex c2 = c.clone();
		for (int i = 0; i < maxNumIterations; i++) {
			c2 = new Complex(c2.getReal() * c2.getReal() - c2.getImaginary() * c2.getImaginary() + c.getReal(), -2
					* c2.getReal() * c2.getImaginary() + c.getImaginary());
			if (c2.modulusSquared() > divergenceLimit) {
				return i;
			}
		}
		return maxNumIterations;
	}

	@Override
	public Fractal getJulia() {
		return new TricornJulia();
	}
	
	private class TricornJulia implements Fractal {

		@Override
		public int iterations(Complex c, int maxNumIterations, double divergenceLimit, Complex userPoint) {
			Complex c2 = c.clone();
			for (int i = 0; i < maxNumIterations; i++) {
				c2 = new Complex(c2.getReal() * c2.getReal() - c2.getImaginary() * c2.getImaginary() + userPoint.getReal(), -2
						* c2.getReal() * c2.getImaginary() + userPoint.getImaginary());
				if (c2.modulusSquared() > divergenceLimit) {
					return i;
				}
			}
			return maxNumIterations;
		}

		@Override
		public Fractal getJulia() {
			return this;
		}
		
	}

}
