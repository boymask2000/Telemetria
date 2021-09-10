package tele;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import common.DataItem;
import common.DataLimits;
import common.Point;
import telemetria.DataType;

public class ZoomMonitor extends Canvas {

	private static final int ZOOMFACTOR = 5;
	private Tele telemetria;
	private List<DataItem> firstData;
	private List<DataItem> secondData;

	private int xOrg = 0;
	private int yOrg = 0;
	private int width;
	private int height;
	private int xOrigin;
	private int yOrigin;
	private Color color;
	private Point px = new Point(0, 0);
	private Matrix matrix;
	private ZoomMonitor canvas;
	private Point screenPoint;
	private DataType dataType;
	private DataLimits dataLimits;
	private Point p100;
	private SpaceAllocator spaceAllocator;

	private boolean showMax = false;
	private boolean showMin = false;
	private boolean showAscisse = false;
	private boolean showOrdinate = false;

	public ZoomMonitor(Composite s, Tele main) {
		super(s, SWT.NONE);
		this.canvas = this;
		this.telemetria = main;
		this.firstData = main.getDataLoader().getFirstData();
		this.secondData = main.getDataLoader().getSecondData();
		this.dataType = main.getCurrentDataType();
		this.dataLimits = telemetria.getDataLimits();
		this.width = getClientArea().width;
		this.height = getClientArea().height;
		matrix = telemetria.getMatrix();

		Rectangle parentBound = s.getBounds();
		setBounds(0, 0, parentBound.width - 50, parentBound.height - 120);

		addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				firstData = main.getDataLoader().getFirstData();
				secondData = main.getDataLoader().getSecondData();
				Rectangle clientArea = getClientArea();

				drawLines(e.gc);

				drawData(e.gc, firstData, Tele.COLOR_1, canvas, 1);
				drawData(e.gc, secondData, Tele.COLOR_2, canvas, 2);

			}
		});
		refreshDataScreen();
		redraw();
	}

	private void refreshDataScreen() {
		this.width = getClientArea().width;
		this.height = getClientArea().height;
		xOrigin = width / 20 + 20;
		yOrigin = height / 20;
	}

	protected void drawLines(GC gc) {
		gc.drawLine(xOrigin, height - yOrigin, width, height - yOrigin);
		gc.drawLine(xOrigin, height - yOrigin, xOrigin, 0);

	}

	public void drawData(GC gc, List<DataItem> data, Color col, Canvas canvas, int n) {
		Point prec = new Point(0, 0);
		this.dataType = telemetria.getCurrentDataType();
		gc.setForeground(col);
		gc.setBackground(col);
		if (screenPoint == null)
			return;

		int moveIndex = matrix.getIndexFromPosition(screenPoint);
		int lastIndex = matrix.getIndexFromPosition(p100);

//		if (showMaxMin)
//			minMax(gc, data, moveIndex, lastIndex);
		this.spaceAllocator = new SpaceAllocator();
		int counter = 0;
	
		for (int i = moveIndex; i < data.size() && i < lastIndex; i++, counter++) {
			DataItem d = data.get(i);

			Point q = matrix.dataToScreen(d);
			Point p1 = matrix.movePoint(q, screenPoint);
			Point p = matrix.scale(p1, 5);
			// matrix.drawLineData(gc, p, prec);
			gc.setForeground(col);
			gc.setBackground(col);
			matrix.drawLineScreen(gc, p, prec);

			showMaxMin(gc, d, n);

			prec = p;

			if (showAscisse && counter % 20 == 0)
				drawUnits(gc, d, p);

		}

		if (moveIndex >= data.size())
			return;
		if (showOrdinate) {
			DataItem d = data.get(moveIndex);
			Point p = Point.copy(screenPoint);
			DataItem tmp = matrix.pixelToDataItem(screenPoint);

			Point qp = matrix.dataToScreen(p);

			int gap = (height - yOrigin) / 10;

			for (int i = 0; i < 10; i++) {
				double ss = matrix.screenToSpeed(p);
				DataItem sd = matrix.pixelToDataItem(p);
				gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
				gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
				gc.drawString(trimVal("" + ss), xOrigin - 40, height - yOrigin - 20 - i * gap);
				p.setY(p.getY() - 10);
			}
		}
	}

	private boolean showMaxMin(GC gc, DataItem d, int n) {
		if ((showMax && d.isValoreMax()) || (showMin && d.isValoreMin())) {
			double val = d.getTime();
			if (dataType == DataType.KM)
				val = d.getKm();
			final int OPT = 2;

			Point qMin = matrix.dataToScreen(d);
			Point mMin = matrix.movePoint(qMin, screenPoint);
			Point pMin = matrix.scale(mMin, 5);
			int delta = pMin.getX() + 100;

			gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
			gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

			if (OPT == 1) {
				gc.drawString(trimVal("" + d.getSpeed()), delta, pMin.getY());
				int dd = (n - 1) * 20;
				gc.drawLine(pMin.getX(), pMin.getY(), pMin.getX(), height - yOrigin + 50 + dd);

				gc.drawString("" + val, pMin.getX(), height - yOrigin + 50 + dd);
			} else {
				String str = "(" + val + ",  " + trimVal("" + d.getSpeed()) + ")";
				Point rr = drawStringOnFreeSpace(gc, str, delta, pMin.getY());
				// gc.drawString("(" +val+","+ trimVal(""+d.getSpeed())+")", delta,
				// pMin.getY());
				gc.drawLine(pMin.getX(), pMin.getY(), rr.getX(), rr.getY());
			}
			return true;
		}
		return false;
	}

	private Point drawStringOnFreeSpace(GC gc, String s, int x, int y) {
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		Point p = new Point(x, y);
		Point pOk = spaceAllocator.calcPosition(gc, s, p);
		gc.drawString(s, pOk.getX(), pOk.getY());
		return pOk;
	}

	private static String trimVal(String s) {
		int index = s.indexOf(".");
		if (index == -1)
			return s;
		int len = s.length();
		if (len - index <= 4)
			return s;
		return s.substring(0, index + 2);
	}

	int tmp = 0;

	private void drawUnits(GC gc, DataItem d, Point c) {
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		if (dataType == DataType.KM)
			gc.drawString("" + fmtKm(d.getKm()), c.getX(), height - yOrigin + 5);// +20*(tmp%3));
		else
			gc.drawString("" + d.getTime(), c.getX(), height - yOrigin + 5);
		tmp++;

	}

//public static void main(String s[]) {
//	System.out.println(ZoomMonitor.fmtKm(2453.55));
//}
	private static String fmtKm(double km) {
		double c = km / 100;
		String s = "" + c;
		int index = s.indexOf(".");
		int pos = s.length() - index;
		if (pos > 2)
			s = s.substring(0, index + 3);
		return s;
	}

	private void drawString(GC gc, String s, int xx2, int yy2) {
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		gc.drawString(s, xx2, yy2);
	}

	public void setPosition(Point screenPoint, Point dataPoint, Point p100) {
		this.screenPoint = screenPoint;

		px = dataPoint;
		this.p100 = p100;
		checkWidget();

		getParent().layout();
		redraw();
	}

	public void refreshGraph() {
		getParent().layout();
		redraw();
	}

	public SpaceAllocator getSpaceAllocator() {
		return spaceAllocator;
	}

	public void setSpaceAllocator(SpaceAllocator spaceAllocator) {
		this.spaceAllocator = spaceAllocator;
	}

	public boolean isShowMax() {
		return showMax;
	}

	public void setShowMax(boolean showMax) {
		this.showMax = showMax;
		getParent().layout();
		redraw();
	}

	public boolean isShowMin() {
		return showMin;
	}

	public void setShowMin(boolean showMin) {
		this.showMin = showMin;
		getParent().layout();
		redraw();
	}

	public boolean isShowOrdinate() {
		return showOrdinate;
	}

	public void setShowOrdinate(boolean showOrdinate) {
		this.showOrdinate = showOrdinate;
		getParent().layout();
		redraw();
	}

	public boolean isShowAscisse() {
		return showAscisse;
	}

	public void setShowAscisse(boolean showAscisse) {
		this.showAscisse = showAscisse;
		getParent().layout();
		redraw();
	}

	public Tele getTelemetria() {
		return telemetria;
	}

}
