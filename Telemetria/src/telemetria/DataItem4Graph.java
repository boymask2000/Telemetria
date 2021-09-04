package telemetria;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class DataItem4Graph {
	private int time;
	private int km;
	private int speed;

	private double factorX = 1;// d/max=nv/km
	private double factorY = 1;// d/max=nv/km
	private Rectangle rectangle;
	private Telemetria telemetria;

	public DataItem4Graph(Rectangle rectangle, DataLimits dataLimits, Telemetria telemetria) {
		int xOrigin = telemetria.getxOrigin();
		int yOrigin = telemetria.getyOrigin();
		this.telemetria=telemetria;
		this.rectangle = rectangle;
		if (dataLimits.getMax() > 0)
			factorX = (rectangle.width - xOrigin) / dataLimits.getMax();

		if (dataLimits.getMaxSpeed() > 0)
			factorY = (rectangle.height - yOrigin) / (dataLimits.getMaxSpeed()* 1.5);
	}

	public Point getPoint(DataItem data, DataType d) {
		time = (int) (factorX * data.getTime());
		km = (int) (factorX * data.getKm());
		speed = (int) (factorY *data.getSpeed());

		if (d == DataType.KM)
			return new Point(getKm(), getSpeed());

		return new Point(getTime(), getSpeed());

	}

	public Point getPoint(int x, int y) {
		int xx = (int) (factorX * x);
		int yy = (int) (factorY * y);
		return new Point(xx, yy);
	}

	public Point getReversePoint(int x, int y) {
		int xx = (int) (x / factorX);
	//	int yy = (int) ((rectangle.height - y) / factorY);
		int yy = (int) ((y) / factorY);
		return new Point(xx, yy);
	}
	
	protected int calcSpeed( int y) {
		int height = rectangle.height;
		double maxSpeed = telemetria.getDataLimits().getMaxSpeed();
		Point maxP =telemetria.getDataItem4Graph().getPoint(0, (int) maxSpeed);
		int yOrigin=telemetria.getyOrigin();
		
	//	int x=(int) ((maxP.getY())/maxSpeed*((height-y)*1.5));
		int ordinate=height-yOrigin;
		
		//	int x=(int) ((maxP.getY())/maxSpeed*((height-y)*1.5));
		int	x=(int) ((ordinate-y)*(ordinate/1.5)/maxP.getY());

		return x;
	}

	public int getTime() {
		return time;
	}

	public int getKm() {
		return km;
	}

	public int getSpeed() {
		return speed;
	}

}
