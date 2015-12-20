package fractals;

import math.Complex;

public class BurningShip implements Fractal {

	@Override
	public int iterations(Complex c, int maxNumIterations, double divergenceLimit, Complex userPoint) {
		Complex c2 = c.clone();
		for (int i = 0; i < maxNumIterations; i++) {
			c2 = new Complex(Math.abs(c2.getReal()), Math.abs(c2.getImaginary())).fastPow(2).add(c);
			if (c2.modulusSquared() > divergenceLimit) {
				return i;
			}
		}
		return maxNumIterations;
	}

	@Override
	public Fractal getJulia() {
		return new BurningShipJulia();
	}
	
	private static class BurningShipJulia implements Fractal {
		@Override
		public int iterations(Complex c, int maxNumIterations, double divergenceLimit, Complex userPoint) {
			Complex c2 = c.clone();
			for (int i = 0; i < maxNumIterations; i++) {
				c2 = new Complex(Math.abs(c2.getReal()), Math.abs(c2.getImaginary())).fastPow(2).add(userPoint);
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
