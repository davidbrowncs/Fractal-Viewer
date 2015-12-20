
package fractals;

public enum FractalType {
	MANDELBROT(Mandelbrot.class.getSimpleName(), new Mandelbrot()),
	CUBIC_MANDELBROT(CubicMandelbrot.class.getSimpleName(), new CubicMandelbrot()),
	BURNING_SHIP(BurningShip.class.getSimpleName(), new BurningShip()),
	BUFFALO(Buffalo.class.getSimpleName(), new Buffalo()),
	QUADRATIC_MANDELBROT(QuadraticMandelbrot.class.getSimpleName(), new QuadraticMandelbrot()),
	MANDELBROT_6(Mandelbrot6.class.getSimpleName(), new Mandelbrot6()),
	TRICORN(Tricorn.class.getSimpleName(), new Tricorn());

	private String name;
	private Fractal fractal;

	private FractalType(String name, Fractal fractal) {
		this.name = name;
		this.fractal = fractal;
	}

	public String getName() {
		return name;
	}

	public Fractal getFractal() {
		return fractal;
	}
	
	public static FractalType getFractalType(String name) {
		for (FractalType t : FractalType.values()) {
			if (t.getName().equals(name)){
				 return t;
			}
		}
		return null;
	}
}
