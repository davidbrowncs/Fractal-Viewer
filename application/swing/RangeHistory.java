
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

	public static class Range {
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

		/**
		 * @return the xMin
		 */
		public double getxMin() {
			return xMin;
		}

		/**
		 * @return the yMin
		 */
		public double getyMin() {
			return yMin;
		}

		/**
		 * @return the xMax
		 */
		public double getxMax() {
			return xMax;
		}

		/**
		 * @return the yMax
		 */
		public double getyMax() {
			return yMax;
		}
	}
}
