
package swing;

import java.awt.Rectangle;

public class DrawingRectangle {

	private int x1;
	private int x2;
	private int y1;
	private int y2;

	private boolean isCreated = false;
	private boolean maintainAspectRatio = true;

	public void create(int x, int y) {
		x1 = x;
		y1 = y;
	}

	public void update(int x, int y) {
		x2 = x;
		y2 = y;

		isCreated = true;
	}

	public void destroy() {
		isCreated = false;
	}

	public boolean isCreated() {
		return isCreated;
	}

	public Rectangle getRect() {
		if (maintainAspectRatio) {
			int xMax = Math.max(x1, x2);
			int xMin = Math.min(x1, x2);
			int yMin = Math.min(y1, y2);
			int yMax = Math.max(y1, y2);

			int range = Math.min(xMax - xMin, yMax - yMin);

			int x1;
			int x2;
			int y1;
			int y2;
			if (this.x2 < this.x1) {
				x1 = this.x1 - range;
				x2 = this.x1;
			} else {
				x1 = this.x1;
				x2 = this.x1 + range;
			}
			if (this.y2 < this.y1) {
				y1 = this.y1 - range;
				y2 = this.y1;
			} else {
				y1 = this.y1;
				y2 = this.y1 + range;
			}
			return new Rectangle(x1, y1, x2 - x1, y2 - y1);
		} else {
			int xMin = Math.min(x1, x2);
			int xMax = Math.max(x1, x2);
			int yMin = Math.min(y1, y2);
			int yMax = Math.max(y1, y2);
			return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
		}
	}
}
