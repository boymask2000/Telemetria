package tele;

import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;

import common.DataItem;
import common.DataLimits;
import common.Point;
import telemetria.DataType;

public class Matrix {
	private Tele tele;
	private int height;
	private int xOrigin;
	private int yOrigin;
	private int width;
	private DataLimits dataLimits;
	private double factorX = 1;
	private double factorY = 1;
	private DataType dataType;

	public Matrix(Tele tele) {
		this.tele = tele;
		this.height = tele.getCanvas().getClientArea().height;
		this.width = tele.getCanvas().getClientArea().width;

		this.xOrigin = tele.getxOrigin();
		this.yOrigin = tele.getyOrigin();
		this.dataType = tele.getCurrentDataType();
		this.dataLimits = tele.getDataLimits();

		if (dataLimits.getMaxValSpace() > 0 && dataType == DataType.KM)
			factorX = (width - xOrigin) / dataLimits.getMaxValSpace();

		if (dataLimits.getMaxValTime() > 0 && dataType == DataType.TIME)
			factorX = (width - xOrigin) / dataLimits.getMaxValTime();

		if (dataLimits.getMaxSpeed() > 0)
			factorY = (height - yOrigin) / (dataLimits.getMaxSpeed() * 1.1);

	}

	public Point dataToScreen(DataItem data) {

		int time = (int) (factorX * data.getTime());
		int km = (int) (factorX * data.getKm());
		int speed = (int) (factorY * data.getSpeed());

		speed = height - speed;

		if (dataType == DataType.KM)
			return new Point(xOrigin + km, speed - yOrigin);

		return new Point(xOrigin + time, speed - yOrigin);
	}

	public DataItem pixelToDataItem(Point p) {
		int x = p.getX();
		int speed = p.getY();

		x = x - xOrigin;
		speed = speed + yOrigin;

		speed = height - speed;
	
		double xx = x / factorX;
		double yy = speed  / factorY;

		DataItem data = new DataItem();
		data.setSpeed(yy);
		if (dataType == DataType.KM)
			data.setKm(xx);
		else
			data.setTime(xx);

		return data;
	}

	public double screenToSpeed(Point p) {
		DataItem d = pixelToDataItem(p);

		return d.getSpeed();

	}

	public Point dataToScreen(Point p) {

		int time = (int) (factorX * p.getX());

		int speed = (int) (factorY * p.getY());
		speed = height - speed;
		return new Point(xOrigin + time, speed - yOrigin);
	}

	public Point screenToData(Point p) {
		int xx = p.getX() - xOrigin;
		int yy = p.getY();
		yy = height - yOrigin - yy;

		xx = (int) (xx / factorX);
		yy = (int) (yy / factorY);

		return new Point(xx, yy);
	}

	public Point screenToData(int x, int y) {

		return screenToData(new Point(x, y));
	}

	public void drawLineData(GC gc, Point p1, Point p2) {
		Point d_p1 = dataToScreen(p1);
		Point d_p2 = dataToScreen(p2);

		gc.drawLine(d_p1.getX(), d_p1.getY(), d_p2.getX(), d_p2.getY());

	}

	public Point scale(Point p, double fact) {
		int xx = (int) (p.getX() * fact);
		int yy = (int) (p.getY() * fact);
		return new Point(xx, yy);
	}

	public void drawLineScreen(GC gc, Point p1, Point p2) {

		gc.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

	}

	public int getIndexFromPosition(Point screenPoint) {
		if (screenPoint == null)
			return 0;
		List<DataItem> ll = tele.getDataLoader().getFirstData();
		int x = screenPoint.getX() - xOrigin;
		int pos = ll.size() * x / width;

		if (pos >= ll.size())
			return ll.size() - 1;
		if (pos < 0)
			return 0;
		return pos;
	}

	public Point movePoint(Point q, Point mp) {
		if (mp == null)
			return q;
		return new Point(q.getX() - mp.getX(), q.getY() - (mp.getY() - 100));

	}
}
