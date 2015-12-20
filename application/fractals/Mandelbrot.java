
package fractals;

import math.Complex;

public class Mandelbrot implements Fractal {

	@Override
	public int iterations(Complex c, int maxNumIterations, double divergenceLimit, Complex userPoint) {
		Complex c2 = c.clone();
		for (int i = 0; i < maxNumIterations; i++) {
			c2 = c2.fastPow(2).add(c);
			if (c2.modulusSquared() > divergenceLimit) {
				return i + 1;
			}
		}
		return maxNumIterations;
	}

	@Override
	public Fractal getJulia() {
		return new MandelbrotJulia();
	}

	private class MandelbrotJulia implements Fractal {
		
		@Override
		public int iterations(Complex c, int maxNumIterations, double divergenceLimit, Complex userPoint) {
			Complex c2 = c.clone();
			for (int i = 0; i < maxNumIterations; i++) {
				c2 = c2.fastPow(2).add(userPoint);
				if (c2.modulusSquared() > divergenceLimit) {
					return i + 1;
				}
			}
			return maxNumIterations;
		}

		@Override
		public Fractal getJulia() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
