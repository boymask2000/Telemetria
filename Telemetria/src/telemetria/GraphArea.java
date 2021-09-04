package telemetria;

import java.util.ArrayList;
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

public class GraphArea extends Canvas {

	private List<DataItem> firstData = new ArrayList<>();
	private List<DataItem> secondData = new ArrayList<>();
	private Telemetria telemetria;
	private int moveIndex;
	private int x;
	private int y;
	private int start;
	private double zoomX;
	private int end;
	private double zoomY;

	public GraphArea(Telemetria telemetria, Composite parent, double zoomX, double zoomY) {
		super(parent, SWT.NONE);
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		this.telemetria = telemetria;

//		addPaintListener(e->draw());

//		addPaintListener(new PaintListener() {
//
//			@Override
//			public void paintControl(PaintEvent arg0) {
//				draw();
//
//			}
//		});
		draw();

	}
	public GraphArea(Composite parent,Telemetria telemetria) {
		super(parent, SWT.NONE);
	
		this.telemetria = telemetria;


		draw();

	}
	public void draw() {

		// canvas = new Canvas(this, SWT.NO_REDRAW_RESIZE);
		setBounds(0, 0, 600, 500);
		addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				Rectangle clientArea = getClientArea();
				// e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
				// e.gc.fillOval(0, 0, clientArea.width, clientArea.height);

				telemetria.drawLines(e.gc);

				drawData(e.gc, firstData, Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
				drawData(e.gc, secondData, Display.getDefault().getSystemColor(SWT.COLOR_RED));
			}
		});

		redraw();
	}

	public void drawData(GC gc, List<DataItem> data, Color col) {
		Point prec = new Point(0, 0);

		int height = getClientArea().height;
		gc.setForeground(col);
		gc.setBackground(col);
		DataLimits limi = new DataLimits();
		limi.setCurrentType(telemetria.getCurrentDataType());
		limi.setMaxVal(firstData.get(firstData.size()-1));
		limi.setMaxVal(secondData.get(secondData.size()-1));
		
		DataItem4Graph item = telemetria.getDataItem4Graph();
		int count = -1;
		
		
		System.out.println("(x,y)="+x+ " "+ y);
		Point p0 = item.getReversePoint(x, y);
		System.out.println("(x1,y1)="+p0.getX()+ " "+ p0.getY());
		
		
		
		for (DataItem d : data) {
			count++;
//			if (count < start)
//				continue;
//			if (count > end)
//				break;

			if (count >= firstData.size())
				continue;

			Point p = item.getPoint(d, telemetria.getCurrentDataType());

			Point px = item.getReversePoint(x, y);

			int x1 = prec.getX() ;//- px.getX();
			int y1 = prec.getY() ;//- px.getY();
			int x2 = p.getX();// - px.getX();
			int y2 = p.getY() ;//- px.getY();

//			x1 += telemetria.getxOrigin();
//			x2 += telemetria.getxOrigin();
//			y1 += telemetria.getyOrigin();
//			y2 += telemetria.getyOrigin();
//
			x1 *= Telemetria.RATIOSELECTOR;
			x2 *= Telemetria.RATIOSELECTOR;
			y1 *= Telemetria.RATIOSELECTOR;
			y2 *= Telemetria.RATIOSELECTOR;

	//		System.out.println(x1 + " " + y1 + " -> " + x2 + " " + y1);
			drawLine(gc, x1, y1, x2, y2);

			prec = p;
		}
	}

	private void drawLine(GC gc, int xx1, int yy1, int xx2, int yy2) {
		int height = getClientArea().height;
		int xOrigin = 0;
		int yOrigin = 0;
		int x1 = xOrigin + xx1;
		int y1 = -yOrigin + height - yy1;
		int x2 = xOrigin + xx2;
		int y2 = -yOrigin + height - yy2;

		gc.drawLine(x1, y1, x2, y2);

	}

//	private void drawLine(GC gc, int xx1, int yy1, int xx2, int yy2) {
//		int height = canvas.getClientArea().height;
//		int x1 = xOrigin + xx1;
//		int y1 = -yOrigin + height - yy1;
//		int x2 = xOrigin + xx2;
//		int y2 = -yOrigin + height - yy2;
//
//		gc.drawLine(x1, y1, x2, y2);
//
//	}
	public void setFirstData(List<DataItem> firstData) {
		this.firstData = firstData;
		refresh();
	}

	public void setSecondData(List<DataItem> secondData) {
		this.secondData = secondData;
		refresh();
	}

	public void setMoveIndex(int moveIndex) {
		this.moveIndex = moveIndex;
		refresh();
	}

	public void setX(int x) {
		this.x = x;
		start = telemetria.getIndexFromX(x);

		end = telemetria.getIndexFromX(x + 100);
		refresh();
	}

	public void setY(int y) {
		this.y = y;
		refresh();

	}

	private void refresh() {
		checkWidget();

		getParent().layout();
		redraw();
	}

	public void setStart(int start) {
		this.start = start;
	}

}
