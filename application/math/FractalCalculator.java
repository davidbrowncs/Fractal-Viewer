
package math;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import swing.ColorScheme;
import swing.RangeHistory;
import swing.RangeHistory.Range;
import swing.UserMouseListener;
import util.SizeListener;
import fractals.Fractal;

public class FractalCalculator implements SizeListener {

	private volatile int[] iterationsArray;
	private static final int[] EMPTY = {};

	private Fractal fractal;

	private int maxIterations = 50;
	private double divergenceLimit = 10;

	private int width;
	private int height;

	// private boolean initialised = false;
	// private double aspectRatio;

	public static final float DEFAULT_X_MIN = -2f;
	public static final float DEFAULT_Y_MIN = -2f;
	public static final float DEFAULT_X_MAX = 2f;
	public static final float DEFAULT_Y_MAX = 2f;

	// "Displayed" values, padding added to maintain aspect ratio.
	private double xMin = DEFAULT_X_MIN;
	private double xMax = DEFAULT_X_MAX;
	private double yMin = DEFAULT_Y_MIN;
	private double yMax = DEFAULT_Y_MAX;

	// "Target" values, these values must be included in display, some extra may
	// be added
	private double absolutexMin = xMin;
	private double absolutexMax = xMax;
	private double absoluteyMin = yMin;
	private double absoluteyMax = yMax;

	private double perX = 0;
	private double perY = 0;

	private Complex userPoint;
	private UserMouseListener listener;

	private ColorScheme scheme;

	private RangeHistory rangeHistory = new RangeHistory();

	private boolean updateable = true;

	private static final int numThreads = Runtime.getRuntime().availableProcessors();
	private static final ExecutorService executor = Executors.newCachedThreadPool();

	public FractalCalculator(int width, int height, Fractal fractal, UserMouseListener listener, ColorScheme scheme) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("Width and height must be positive, given " + width + ", " + height + ".");
		}
		this.width = width;
		this.height = height;
		if (width == 0 & height == 0) {
			iterationsArray = EMPTY;
		} else {
			iterationsArray = new int[width * height];
		}
		this.fractal = fractal;
		this.listener = listener;
		this.scheme = scheme;
	}

	public void setFractal(Fractal fractal) {
		this.fractal = fractal;
	}

	public Complex convert(int x, int y) {
		return new Complex(xMin + perX * x, yMin + perY * y);
	}

	public void setUpdateable(boolean b) {
		updateable = b;
	}

	public boolean isUpdateable() {
		return updateable;
	}

	public void previousRange() {
		if (rangeHistory.hasPrevious()) {
			Range r = rangeHistory.getPrevious();
			absolutexMin = r.getxMin();
			absolutexMax = r.getxMax();
			absoluteyMin = r.getyMin();
			absoluteyMax = r.getyMax();
			maintainRatio();
		}
	}

	private void maintainRatio() {
		if (height != 0 && width != 0) {
			if (width > height) {
				if (height != 0) {
					double yRange = absoluteyMax - absoluteyMin;
					double xRange = yRange * width / height;
					double difference = Math.abs((xRange - (absolutexMax - absolutexMin)) / 2d);

					xMax = absolutexMax + difference;
					xMin = absolutexMin - difference;
					yMin = absoluteyMin;
					yMax = absoluteyMax;

					perX = (xMax - xMin) / width;
					perY = (yMax - yMin) / height;
				}
			} else {
				if (width != 0) {
					double xRange = absolutexMax - absolutexMin;
					double yRange = xRange * height / width;
					double difference = Math.abs((yRange - (absoluteyMax - absoluteyMin)) / 2d);

					yMax = absoluteyMax + difference;
					yMin = absoluteyMin - difference;
					xMin = absolutexMin;
					xMax = absolutexMax;

					perX = (xMax - xMin) / width;
					perY = (yMax - yMin) / height;
				}
			}
		}
	}

	public void updateRange(int xLoc, int xLocEnd, int yLoc, int yLocEnd) {
		rangeHistory.addRange(absolutexMin, absolutexMax, absoluteyMin, absoluteyMax);
		absolutexMax = xMin + perX * xLocEnd;
		absoluteyMax = yMin + perY * yLocEnd;
		absolutexMin = xMin + perX * xLoc;
		absoluteyMin = yMin + perY * yLoc;
		maintainRatio();
	}

	public void update() {
		if (updateable) {
			List<Callable<Void>> jobs = new ArrayList<>();

			userPoint = listener.getPoint();

			for (int i = 0; i < numThreads - 1; i++) {
				int min = (height / numThreads) * i;
				int max = (height / numThreads) * (i + 1);
				jobs.add(new CalculateJob(min, max, width, maxIterations, divergenceLimit, userPoint.clone(), scheme.clone()));
			}
			int min = (height / numThreads) * (numThreads - 1);
			int max = height;
			jobs.add(new CalculateJob(min, max, width, maxIterations, divergenceLimit, userPoint.clone(), scheme.clone()));

			try {
				executor.invokeAll(jobs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	class CalculateJob implements Callable<Void> {
		private int yStart;
		private int yEnd;
		private int width;
		private int maxIterations;
		private double divergenceLimit;
		private Complex userPoint;
		private ColorScheme scheme;

		public CalculateJob(int yStart, int yEnd, int width, int maxIterations, double divergenceLimit, Complex userPoint,
				ColorScheme scheme) {
			this.yStart = yStart;
			this.yEnd = yEnd;
			this.width = width;
			this.maxIterations = maxIterations;
			this.divergenceLimit = divergenceLimit;
			this.userPoint = userPoint;
			this.scheme = scheme;
		}

		public Void call() {
			for (int y = yStart; y < yEnd; y++) {
				for (int x = 0; x < this.width; x++) {
					Complex c = convert(x, y);
					double iterations = fractal.iterations(c, this.maxIterations, this.divergenceLimit, this.userPoint);
					iterationsArray[y * this.width + x] = this.scheme.getRGB(iterations / this.maxIterations);
				}
			}
			return null;
		}
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMaxIterations(int i) {
		maxIterations = i;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getIterationsArray() {
		return iterationsArray;
	}

	@Override
	public void setSize(Dimension d) {
		this.width = (int) d.getWidth();
		this.height = (int) d.getHeight();
		iterationsArray = new int[width * height];
		maintainRatio();
	}
}
