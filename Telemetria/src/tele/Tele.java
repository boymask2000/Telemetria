package tele;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import common.DataItem;
import common.DataLimits;
import common.Point;
import telemetria.DataItem4Graph;
import telemetria.DataType;
import telemetria.Telemetria;

public class Tele {

	public static final Color COLOR_1 = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	public static final Color COLOR_2 = Display.getDefault().getSystemColor(SWT.COLOR_RED);

	public final static int HEIGHT = 500;

	public final static int WIDTH = 800;

	private DataType currentDataType = DataType.TIME;

	private boolean zoomActive;
	private Tele tele;
	private ZoomShell zoomShell;
	private int height;
	private int width;
	private DataLimits dataLimits = new DataLimits();
	private DataManager dataLoader;
	private boolean moveTrackActive = true;

	private Canvas canvas;
	private Text file1Field;
	private Text file2Field;
	private Text dataType;
	private Text speedVal1;
	private Text speedVal2;
	private Label dataTypeLabel;

	private Shell shell;
	private ZoomMonitor zoomMonitor;
	private int xOrigin;
	private int yOrigin;
	private Matrix matrix;

	public static void main(String[] args) {
		new Tele();
	}

	public Tele() {
		this.tele = this;
		shell = new Shell(Display.getDefault());
		shell.setBounds(0, 0, WIDTH, HEIGHT);
		shell.setLayout(new GridLayout(20, false));
		this.dataLoader = new DataManager(this);

		shell.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

				refreshGeometria();

			}

		});

		shell.open();

		MenuManager menuManager = new MenuManager(this, shell);

//		dataLimits.setMaxVal(0);

		canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE);
		canvas.setBounds(0, 0, 600, 500);
		canvas.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				Rectangle clientArea = canvas.getClientArea();
				// e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
				// e.gc.fillOval(0, 0, clientArea.width, clientArea.height);

				drawLines(e.gc);

				drawData(e.gc, dataLoader.getFirstData(), COLOR_1, canvas);
				drawData(e.gc, dataLoader.getSecondData(), COLOR_2, canvas);
			}
		});

		setButtons();

		initMouseMoveInfo();

		// dataLoader.loadData1(DIR + "/" + "pr01.csv", 0, -1);
		// dataLoader.loadData2(DIR + "/" + "pr01_m.csv", 0, -1);

		matrix = new Matrix(this);

		shell.layout(true, true);
		refreshGeometria();

		initClickEvents();

		while (!shell.isDisposed())
			Display.getDefault().readAndDispatch();
	}

	private void initClickEvents() {
		canvas.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event e) {
				moveTrackActive = !moveTrackActive;
			}
		});
	}

	public void drawData(GC gc, List<DataItem> data, Color col, Canvas canvas) {
		if (data.size() == 0)
			return;
		// dataLoader.dumpData1();
		// Point prec = new Point(0, 0);
		DataItem first = data.get(0);
		Point prec = matrix.dataToScreen(first);

		gc.setForeground(col);
		gc.setBackground(col);

		for (DataItem d : data) {

			Point p = matrix.dataToScreen(d);

			matrix.drawLineScreen(gc, p, prec);

			prec = p;
		}
	}

	protected void drawLines(GC gc) {
		Point mX;

		if (currentDataType == DataType.KM)
			mX = new Point((int) dataLimits.getMaxValSpace(), 0);
		else
			mX = new Point((int) dataLimits.getMaxValTime(), 0);

		Point origin = new Point(0, 0);

		Point mY = new Point(0, (int) dataLimits.getMaxSpeed());

		matrix.drawLineData(gc, origin, mX);
		matrix.drawLineData(gc, origin, mY);

	}

	private void initMouseMoveInfo() {
		canvas.addMouseMoveListener(new MouseMoveListener() {

			public void mouseMove(MouseEvent e) {

				if (zoomShell != null && zoomShell.isDisposed()) {
					setZoomActive(false);
					zoomShell = null;
				}

				Point screenPoint = new Point(e.x, e.y);
				int moveIndex = matrix.getIndexFromPosition(screenPoint);
				if (isZoomActive() && moveTrackActive) {

					Point p100 = new Point(e.x + 200, e.y);
					Point p = matrix.screenToData(screenPoint);

					// zoomMonitor.setPosition(e.x-xOrigin, e.y);
					zoomShell.setPosition(screenPoint, p, p100);

				}
				double asc;
				if (dataLoader.getFirstData().size() > 0) {
					if (currentDataType == DataType.KM)
						asc = dataLoader.getFirstData().get(moveIndex).getKm();
					else
						asc = dataLoader.getFirstData().get(moveIndex).getTime();
					dataType.setText("" + asc);
				}
			}

			private String trimTime(String s) {
				int index = s.indexOf(".");
				if (index == -1)
					return s;
				if (s.length() - index < 3)
					return s;
				return s.substring(0, index + 3);

			}

		});

	}

	public void refreshGeometria() {
		if (canvas == null)
			return;

		height = canvas.getClientArea().height;
		width = canvas.getClientArea().width;

		height = canvas.getBounds().height;
		width = canvas.getBounds().width;

		xOrigin = width / 20;
		yOrigin = height / 20;
		matrix = new Matrix(this);
		canvas.redraw();
	}

	private void setButtons() {
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 19;
		data.verticalSpan = 6;
		canvas.setLayoutData(data);

		Composite right = createRightButtons();
		GridData data2 = new GridData(GridData.BEGINNING);
		data2.horizontalSpan = 1;
		data2.verticalSpan = 6;
		right.setLayoutData(data2);

		Composite down = createlowerButtons();
		GridData data1 = new GridData(GridData.BEGINNING);
		data1.horizontalSpan = 1;
		data1.verticalSpan = 1;
		down.setLayoutData(data1);
	}

	private Composite createRightButtons() {

		Composite right = new Composite(shell, SWT.NONE);
		right.setLayout(new FillLayout(SWT.VERTICAL));

		createRadioDataType(right);

//		Button br = new Button(right, SWT.PUSH);
//		br.setText("br");
//		Button br2 = new Button(right, SWT.PUSH);
//		br2.setText("br2");

		Composite datatype = new Composite(right, SWT.NONE);
		dataTypeLabel = new Label(datatype, SWT.NONE);
		datatype.setLayout(new FillLayout());
		dataType = new Text(datatype, SWT.BORDER);
		dataType.setEditable(false);

//		Composite speed1 = new Composite(right, SWT.NONE);
//		speed1.setLayout(new FillLayout());
//		Label speedLabel = new Label(speed1, SWT.NONE);
//		speedLabel.setText("Speed");
//
//		speedVal1 = new Text(speed1, SWT.BORDER);
//		speedVal1.setEditable(false);
//	
//
//		Composite speed2 = new Composite(right, SWT.NONE);
//		speed2.setLayout(new FillLayout());
//		Label speedLabel2 = new Label(speed2, SWT.NONE);
//		speedLabel2.setText("Speed");
//
//		speedVal2 = new Text(speed2, SWT.BORDER);
//		speedVal2.setEditable(false);
		// dataType = infoField(right,5,"Speed 1", COLOR_2);
//		speedVal2 = infoField(right, 5, "Speed 1", COLOR_1);
//		speedVal2 = infoField(right, 5, "Speed 2", COLOR_2);

		return right;
	}

	private Composite createlowerButtons() {
		Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));

		file1Field = infoField(comp, 5, "File 1", COLOR_1);
		file2Field = infoField(comp, 5, "File 2", COLOR_2);

		Composite shiftArea = shiftArea(comp);
		GridData data1 = new GridData(GridData.FILL_BOTH);
		data1.horizontalSpan = 1;
		data1.verticalSpan = 1;
		shiftArea.setLayoutData(data1);

		return comp;
	}

	private Composite shiftArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new RowLayout());
		Button calc = new Button(comp, SWT.PUSH);
		calc.setText("AutoShift");

		calc.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int s = dataLoader.calcDataShift();
				dataLoader.shiftData(s);
				shell.redraw();
				canvas.redraw();
				if (zoomShell != null && !zoomShell.isDisposed())
					zoomShell.refreshGraph();
			}

		});
		Button left = new Button(comp, SWT.PUSH);
		left.setText("<");
		Button right = new Button(comp, SWT.PUSH);
		right.setText(">");

		left.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int s = dataLoader.calcDataShift();
				dataLoader.shiftData(1);
				shell.redraw();
				canvas.redraw();
				if (zoomShell != null && !zoomShell.isDisposed())
					zoomShell.refreshGraph();
			}

		});
		right.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int s = dataLoader.calcDataShift();
				dataLoader.shiftData(-1);
				shell.redraw();
				canvas.redraw();
				if (zoomShell != null && !zoomShell.isDisposed())
					zoomShell.refreshGraph();
			}

		});

		return comp;
	}

	private Text infoField(Composite parent, int layoutSize, String str, Color col) {
		Composite f1 = new Composite(parent, SWT.NONE);

		GridData dataf1 = new GridData(GridData.FILL_BOTH);
		dataf1.horizontalSpan = 1;
		dataf1.verticalSpan = 1;
		f1.setLayoutData(dataf1);

		f1.setLayout(new GridLayout(layoutSize, false));
		Text bColor = new Text(f1, SWT.NONE);

		bColor.setEnabled(false);
		bColor.setBackground(col);
		bColor.setText(" ");

		GridData data0 = new GridData(GridData.FILL_BOTH);
		data0.horizontalSpan = 1;
		data0.verticalSpan = 1;
		bColor.setLayoutData(data0);

		Label f1Label = new Label(f1, SWT.NONE);
		GridData data1 = new GridData(GridData.FILL_BOTH);
		data1.horizontalSpan = 1;
		data1.verticalSpan = 1;
		f1Label.setLayoutData(data1);

		Text fileField = new Text(f1, SWT.BORDER);
		GridData data2 = new GridData(GridData.FILL_BOTH);
		data2.horizontalSpan = layoutSize - 2;
		data2.verticalSpan = 1;
		fileField.setLayoutData(data2);

		f1Label.setText(str);

		fileField.setEditable(false);

		return fileField;
	}

	private void createRadioDataType(Composite parent) {
		Group group1 = new Group(parent, SWT.SHADOW_IN);
		group1.setText("Data type");
		group1.setLayout(new RowLayout(SWT.HORIZONTAL));
		Button b_time = new Button(group1, SWT.RADIO);
		b_time.setText("Time");
		Button b_km = new Button(group1, SWT.RADIO);
		b_km.setText("m");
		b_time.setSelection(true);
		b_time.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				dataLimits.setCurrentType(DataType.TIME);

				setdataTypeToShow(DataType.TIME);
				refreshGeometria();
				shell.redraw();
				canvas.redraw();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		b_km.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				dataLimits.setCurrentType(DataType.KM);

				setdataTypeToShow(DataType.KM);
				refreshGeometria();
				shell.redraw();
				canvas.redraw();
				shell.layout(true, true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	protected void setdataTypeToShow(DataType d) {
		currentDataType = d;
		dataTypeLabel.setText(d.getType());
		shell.layout(true, true);
	}

	public DataLimits getDataLimits() {
		return dataLimits;
	}

	public void setVal_file1Field(String s) {
		file1Field.setText(s);
		shell.layout(true, true);

	}

	public void setVal_file2Field(String s) {
		file2Field.setText(s);
		shell.layout(true, true);

	}

	public DataManager getDataLoader() {
		return dataLoader;
	}

	public DataType getCurrentDataType() {
		return currentDataType;
	}

	public ZoomMonitor getZoomMonitor() {
		return zoomMonitor;
	}

	public void setZoomMonitor(ZoomMonitor zoomMonitor) {
		this.zoomMonitor = zoomMonitor;
	}

	public ZoomShell getZoomShell() {
		return zoomShell;
	}

	public void setZoomShell(ZoomShell zoomShell) {
		this.zoomShell = zoomShell;
	}

	public boolean isZoomActive() {
		return zoomActive;
	}

	public void setZoomActive(boolean zoomActive) {
		this.zoomActive = zoomActive;
	}

	public int getxOrigin() {
		return xOrigin;
	}

	public int getyOrigin() {
		return yOrigin;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public Matrix getMatrix() {
		return matrix;
	}
}
