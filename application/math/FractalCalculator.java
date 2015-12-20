
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
import util.DataObserver;
import util.DataSource;
import util.SizeListener;
import fractals.Fractal;

public class FractalCalculator implements SizeListener, Cloneable, DataSource {

	private List<DataObserver> dataObservers = new ArrayList<>();

	private volatile int[] iterationsArray;
	private static final int[] EMPTY = {};
	private Object iterationsLock = new Object();

	private Fractal fractal;

	private int maxIterations = 50;
	private double divergenceLimit = 10;

	private int width;
	private int height;

	public static final double DEFAULT_X_MIN = -2f;
	public static final double DEFAULT_Y_MIN = -2f;
	public static final double DEFAULT_X_MAX = 2f;
	public static final double DEFAULT_Y_MAX = 2f;

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

	public double getXMin() {
		return xMin;
	}

	public double getXMax() {
		return xMax;
	}

	public double getYMin() {
		return yMin;
	}

	public double getYMax() {
		return yMax;
	}

	public void setXMin(double d) {
		rangeHistory.addRange(absolutexMin, absolutexMax, absoluteyMin, absoluteyMax);
		xMin = d;
		absolutexMin = d;
		maintainRatio();
	}

	public void setXMax(double d) {
		rangeHistory.addRange(absolutexMin, absolutexMax, absoluteyMin, absoluteyMax);
		xMax = d;
		absolutexMax = d;
		maintainRatio();
	}

	public void setYMin(double d) {
		rangeHistory.addRange(absolutexMin, absolutexMax, absoluteyMin, absoluteyMax);
		yMin = d;
		absoluteyMin = d;
		maintainRatio();
	}

	public void setYMax(double d) {
		rangeHistory.addRange(absolutexMin, absolutexMax, absoluteyMin, absoluteyMax);
		yMax = d;
		absoluteyMax = d;
		maintainRatio();
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

					updateData();
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

					updateData();
				}
			}
		}
	}

	public void updateRange(int xLoc, int xLocEnd, int yLoc, int yLocEnd) {
		rangeHistory.addRange(absolutexMin, absolutexMax, absoluteyMin, absoluteyMax);
		absolutexMin = convert(xLoc, 0).getReal();
		absolutexMax = convert(xLocEnd, 0).getReal();
		absoluteyMin = convert(0, yLoc).getImaginary();
		absoluteyMax = convert(0, yLocEnd).getImaginary();
		maintainRatio();
	}

	public void update() {
		update(executor);
	}

	private void update(ExecutorService s) {
		synchronized (iterationsLock) {
			if (updateable) {
				List<Callable<Void>> jobs = new ArrayList<>();

				userPoint = listener.getPoint();

				for (int i = 0; i < numThreads - 1; i++) {
					int min = (height / numThreads) * i;
					int max = (height / numThreads) * (i + 1);
					jobs.add(new CalculateJob(iterationsArray, fractal, min, max, xMin, perX, yMin, perY, width,
							maxIterations, divergenceLimit, userPoint.clone(), scheme.clone()));
				}
				int min = (height / numThreads) * (numThreads - 1);
				int max = height;
				jobs.add(new CalculateJob(iterationsArray, fractal, min, max, xMin, perX, yMin, perY, width, maxIterations,
						divergenceLimit, userPoint.clone(), scheme.clone()));

				try {
					s.invokeAll(jobs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Calculator should be a fresh instance, not the same one that is being
	// used to draw the display. Clone the FractalCalculator on the edt, then
	// call this method on a new thread.
	public static int[] getColourArray(int width, int height, FractalCalculator calculator, double xMin, double xMax,
			double yMin, double yMax) {
		return calculator.colourArrayJob(width, height, xMin, xMax, yMin, yMax);
	}

	private int[] colourArrayJob(int width, int height, double xMin, double xMax, double yMin, double yMax) {
		this.width = width;
		this.height = height;
		this.absolutexMin = xMin;
		this.absolutexMax = xMax;
		this.absoluteyMin = yMin;
		this.absoluteyMax = yMax;
		
		ExecutorService e = Executors.newFixedThreadPool(numThreads);
		
		maintainRatio();
		
//		double xMiddle = (xMax - xMin) / 2 + xMin;
//		double yMiddle = (yMax - yMin) / 2 + yMin;
		
//		absolutexMin = xMiddle - (width / 2) * perX;
//		if ((width % 2) == 0) {
//			absolutexMax = xMiddle + (width / 2) * perX;
//		} else {
//			absolutexMax = xMiddle + ((width / 2) + 1) * perX;
//		}
//
//		absoluteyMin = yMiddle - (height / 2) * perY;
//		if ((height % 2) == 0) {
//			absoluteyMax = yMiddle + (height / 2) * perY;
//		} else {
//			absoluteyMin = yMiddle - (height / 2) * perY;
//		}
//		maintainRatio();

		iterationsArray = new int[width * height];
		update(e);
		e.shutdown();
		return iterationsArray;
	}

	class CalculateJob implements Callable<Void> {
		private int yStart;
		private int yEnd;
		private int width;
		private int maxIterations;
		private double divergenceLimit;
		private Complex userPoint;
		private ColorScheme scheme;
		private int[] iterationsArray;
		private Fractal fractal;
		private double xMin;
		private double perX;
		private double yMin;
		private double perY;

		public CalculateJob(int[] iterationsArray, Fractal fractal, int yStart, int yEnd, double xMin, double perX,
				double yMin, double perY, int width, int maxIterations, double divergenceLimit, Complex userPoint,
				ColorScheme scheme) {
			this.yStart = yStart;
			this.yEnd = yEnd;
			this.width = width;
			this.maxIterations = maxIterations;
			this.divergenceLimit = divergenceLimit;
			this.userPoint = userPoint;
			this.scheme = scheme;
			this.iterationsArray = iterationsArray;
			this.fractal = fractal;
			this.xMin = xMin;
			this.perX = perX;
			this.yMin = yMin;
			this.perY = perY;
		}

		public Complex convert(int x, int y) {
			return new Complex(xMin + perX * x, yMin + perY * y);
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

	public FractalCalculator clone() {
		FractalCalculator c = new FractalCalculator(width, height, fractal, listener, scheme.clone());
		c.iterationsArray = iterationsArray.clone();
		c.fractal = fractal;
		c.maxIterations = maxIterations;
		c.divergenceLimit = divergenceLimit;
		c.width = width;
		c.height = height;
		c.xMin = xMin;
		c.xMax = xMax;
		c.yMin = yMin;
		c.yMax = yMax;
		c.absolutexMin = absolutexMin;
		c.absolutexMax = absolutexMax;
		c.absoluteyMin = absoluteyMin;
		c.absoluteyMax = absoluteyMax;
		c.perX = perX;
		c.perY = perY;
		c.userPoint = userPoint.clone();
		c.rangeHistory = rangeHistory.clone();
		c.updateable = updateable;
		return c;
	}

	private void updateData() {
		for (DataObserver o : dataObservers) {
			o.updated(this);
		}
	}

	@Override
	public void addDataObserver(DataObserver observer) {
		if (!dataObservers.contains(observer)) {
			dataObservers.add(observer);
		}
	}

	@Override
	public void removeDataObserver(DataObserver observer) {
		if (dataObservers.contains(observer)) {
			dataObservers.remove(observer);
		}
	}
}
