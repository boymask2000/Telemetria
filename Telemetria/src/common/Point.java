package telemetria;

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

	
}
