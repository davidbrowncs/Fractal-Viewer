package fractals;

import math.Complex;

public class Mandelbrot6 implements Fractal {

	@Override
	public int iterations(Complex c, int maxNumIterations, double divergenceLimit, Complex userPoint) {
		Complex c2 = c.clone();
		for (int i = 0; i < maxNumIterations; i++) {
			c2 = c2.fastPow(6).add(c);
			if (c2.modulusSquared() > divergenceLimit) {
				return i;
			}
		}
		return maxNumIterations;
	}

	@Override
	public Fractal getJulia() {
		return new Mandelbrot6Julia();
	}

	private static class Mandelbrot6Julia implements Fractal {

		@Override
		public int iterations(Complex c, int maxNumIterations, double divergenceLimit, Complex userPoint) {
			Complex c2 = c.clone();
			for (int i = 0; i < maxNumIterations; i++) {
				c2 = c2.fastPow(6).add(userPoint);
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
