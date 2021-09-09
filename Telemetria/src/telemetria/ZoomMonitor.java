package telemetria;

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

public class ZoomMonitor extends Canvas {

	private static final int ZOOMFACTOR = 5;
	private Telemetria telemetria;
	private List<DataItem> firstData;
	private List<DataItem> secondData;

	private int xOrg = 0;
	private int yOrg = 0;
	private int width;
	private int height;
	private int xOrigin;
	private int yOrigin;
	private Color color;
	private Point px;

	public ZoomMonitor(Composite s, Telemetria main) {
		super(s, SWT.NONE);
		this.telemetria = main;
		this.firstData = main.getFirstData();
		this.secondData = main.getSecondData();
		this.width = getClientArea().width;
		this.height = getClientArea().height;

		setBounds(0, 0, 600, 500);

		addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				Rectangle clientArea = getClientArea();

				drawLines(e.gc);

				drawData(e.gc, firstData, Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
				drawData(e.gc, secondData, Display.getDefault().getSystemColor(SWT.COLOR_RED));

			}
		});

		redraw();
	}

	protected void drawLines(GC gc) {

		xOrigin = width / 20;
		yOrigin = height / 20;
		gc.drawLine(xOrigin, height - yOrigin, width, height - yOrigin);
		gc.drawLine(xOrigin, height - yOrigin, xOrigin, 0);
	}

	public void drawData(GC gc, List<DataItem> data, Color col) {
		this.color = col;
		this.width = getClientArea().width;
		this.height = getClientArea().height;

		Point prec = new Point(0, 0);

		int height = getClientArea().height;
		gc.setForeground(col);
		gc.setBackground(col);
		DataLimits limi = new DataLimits();
		limi.setCurrentType(telemetria.getCurrentDataType());
		limi.setMaxVal(firstData.get(firstData.size() - 1));
		limi.setMaxVal(secondData.get(secondData.size() - 1));

		DataItem4Graph item = telemetria.getDataItem4Graph();
		px = item.getReversePoint(xOrg, yOrg);
		int moveIndex = telemetria.getIndexFromX(xOrg);
		if (moveIndex < 0)
			moveIndex = 0;
		int startData = 0;

		for (int i = moveIndex; i < firstData.size() && i < secondData.size(); i++) {
			DataItem d = data.get(i);
			if (i == moveIndex)
				startData = i;

			Point p = item.getPoint(d, telemetria.getCurrentDataType());

			int x1 = prec.getX() - xOrg;
			int y1 = prec.getY();// -yOrg;
			int x2 = p.getX() - xOrg;
			int y2 = p.getY();// -yOrg;

			x1 -= px.getX();
			x2 -= px.getX();
			y1 -= px.getY();
			y2 -= px.getY();
			int z = ZOOMFACTOR;

			x1 *= z;
			x2 *= z;
			y1 *= z;
			y2 *= z;

			x1 += xOrigin;
			x2 += xOrigin;
			y1 += yOrigin;
			y2 += yOrigin;
			// System.out.println(x1 + " " + y1 + " -> " + x2 + " " + y1);
			drawLine(gc, x1, y1, x2, y2);

			if (i % 20 == 0) {

				drawString(gc, "" + p.getX(), x2, height - yOrigin + 5);
			}
			// gc.drawString("" + p.getX(), x2, height - yOrigin);

			prec = p;
		}
		Point orig = item.getReversePoint(0, 0);
		drawLine(gc, orig.getX()+xOrigin, orig.getY()+yOrigin,  orig.getX()+xOrigin+40, orig.getY()+yOrigin+40);
		
		
		DataItem d = data.get(startData);
		double startVal = d.getSpeed();
		int sal =height-yOrg;// (int) ((startVal /10)) *10;
	
		
		for (int i = 0; i < 30; i++) {
			Point salPoint = item.getPoint(0, sal);
			
			Point p = item.getReversePoint(0, sal);
	//		p = trans(p);
		//	drawString(gc, "" + sal, xOrigin, height-i* height/30 );
		drawString(gc, "" + p.getY(), 1, height-i* height/30-2*yOrigin);
			sal += 10;
			
		}

	}

	private Point trans(Point p) {
		int x1 = p.getX() - xOrg;
		int y1 = p.getY();
		x1 -= px.getX();
		y1 -= px.getY();

		x1 *= ZOOMFACTOR;
		y1 *= ZOOMFACTOR;
		x1 += xOrigin;
		y1 += yOrigin;
		return new Point(x1, y1);

	}

	private void drawString(GC gc, String s, int xx2, int yy2) {
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		gc.drawString(s, xx2, yy2);
	}

	private void drawLine(GC gc, int xx1, int yy1, int xx2, int yy2) {
		gc.setForeground(color);
		gc.setBackground(color);

		int height = getClientArea().height;
		int xOrigin = 0;
		int yOrigin = 0;
		int x1 = xOrigin + xx1;
		int y1 = -yOrigin + height - yy1;
		int x2 = xOrigin + xx2;
		int y2 = -yOrigin + height - yy2;

		gc.drawLine(x1, y1, x2, y2);

	}

	public void setPosition(int x, int y) {

		xOrg = x;
		yOrg = y;
		DataItem4Graph item = telemetria.getDataItem4Graph();
		px = item.getReversePoint(xOrg, yOrg);
		checkWidget();

		getParent().layout();
		redraw();
	}

}
