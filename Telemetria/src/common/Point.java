package common;

public class Point {
	private int x;
	private int y;

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public static Point copy(Point s) {
		return new Point(s.getX(), s.getY() );
	
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	
}
