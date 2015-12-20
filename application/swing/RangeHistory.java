
package swing;

import java.util.Stack;

public class RangeHistory {

	private Stack<Range> ranges = new Stack<>();

	public void addRange(double xMin, double xMax, double yMin, double yMax) {
		ranges.push(new Range(xMin, xMax, yMin, yMax));
	}

	public boolean hasPrevious() {
		return !ranges.isEmpty();
	}

	public Range getPrevious() {
		return ranges.pop();
	}

	public RangeHistory clone() {
		RangeHistory h = new RangeHistory();
		for (int i = 0; i < ranges.size(); i++) {
			h.ranges.add(ranges.get(i).clone());
		}
		return h;
	}

	public static class Range implements Cloneable {
		private double xMin;
		private double yMin;
		private double xMax;
		private double yMax;

		private Range(double xMin, double xMax, double yMin, double yMax) {
			this.xMin = xMin;
			this.xMax = xMax;
			this.yMin = yMin;
			this.yMax = yMax;
		}

		public double getxMin() {
			return xMin;
		}

		public double getyMin() {
			return yMin;
		}

		public double getxMax() {
			return xMax;
		}

		public double getyMax() {
			return yMax;
		}

		public Range clone() {
			return new Range(xMin, xMax, yMin, yMax);
		}
	}
}
