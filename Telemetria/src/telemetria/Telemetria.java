package telemetria;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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
import org.eclipse.swt.widgets.Tracker;

public class Telemetria {
	private Telemetria tele;
	private static final String DIR = "C:\\Users\\GiovanniPosabella\\Desktop\\CICCA";
	private Shell shell;
	private Canvas canvas;
	private List<DataItem> firstData = new ArrayList<>();
	private List<DataItem> secondData = new ArrayList<>();
	private DataType currentDataType;
	private Text dataType;
	private Label dataTypeLabel;
	private DataLimits dataLimits = new DataLimits();
	private Text speedVal2;
	private Text speedVal1;
	private double factor;
	private int xOrigin;

	private int yOrigin;
	private MenuManager menuManager;
	private Text file1Field;
	private Text file2Field;
	protected int moveIndex;
	private DataItem4Graph dataItem4Graph;
	private ZoomMonitor zoomMonitor;
	private boolean zoomActive = false;
	private ZoomShell zoomShell;

	private static final int SIZESELECTOR = 100;
	public final static int HEIGHT = 600;
	public final static double WINFACT = 1.2;
	public final static int WIDTH = (int) (HEIGHT * WINFACT);
	public static final double RATIOSELECTOR = HEIGHT / SIZESELECTOR;

	public static void main(String[] args) {
		new Telemetria();
	}

	public Telemetria() {
		this.tele = this;
		shell = new Shell(Display.getDefault());
		shell.setBounds(0, 0, WIDTH, HEIGHT);
		shell.setLayout(new GridLayout(20, false));

		shell.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

				if (dataLimits != null && dataLimits.getMax() > 0)
					factor = (canvas.getClientArea().width - xOrigin) / dataLimits.getMax();

			}
		});

		shell.open();

		menuManager = new MenuManager(this, shell);

//		dataLimits.setMaxVal(0);

		canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE);
		canvas.setBounds(0, 0, 600, 500);
		canvas.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				Rectangle clientArea = canvas.getClientArea();
				// e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
				// e.gc.fillOval(0, 0, clientArea.width, clientArea.height);

				drawLines(e.gc);

				drawData(e.gc, firstData, Display.getDefault().getSystemColor(SWT.COLOR_GREEN), canvas);
				drawData(e.gc, secondData, Display.getDefault().getSystemColor(SWT.COLOR_RED), canvas);
			}
		});

		setButtons();

		initMouseMoveInfo();

		loadData1(DIR + "/" + "pr01.csv", 0, -1);
		loadData2(DIR + "/" + "pr01_m.csv", 0, -1);

		dataItem4Graph = new DataItem4Graph(canvas.getClientArea(), dataLimits, tele);

//		startGraphArea();

		shell.layout(true, true);

		while (!shell.isDisposed())
			Display.getDefault().readAndDispatch();
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

	private void initMouseMoveInfo() {
		canvas.addMouseMoveListener(new MouseMoveListener() {

			public void mouseMove(MouseEvent e) {
				int height = canvas.getClientArea().height;
				int width = canvas.getClientArea().width;
				int v = height - yOrigin - e.y;
				if (v < 0)
					v = 0;

				int calSp = calcSpeed(canvas, yOrigin, e.y);
				System.out.println("speed="+calSp);

				double max = dataLimits.getMax();

				// moveIndex = (int) (((e.x - xOrigin) * firstData.size()) / (width - xOrigin));
				moveIndex = getIndexFromX(e.x);
				if (moveIndex < 0)
					moveIndex = 0;

				if (moveIndex < firstData.size()) {
					DataItem v1 = firstData.get(moveIndex);

					if (currentDataType == DataType.KM)
						dataType.setText(trimTime("" + v1.getKm()));
					else
						dataType.setText(trimTime("" + v1.getTime()));

					speedVal1.setText("" + v1.getSpeed());
				}
				if (moveIndex < secondData.size()) {
					DataItem v2 = secondData.get(moveIndex);

					speedVal2.setText("" + v2.getSpeed());
				}
				if (isZoomActive()) {
					// zoomMonitor.setPosition(e.x-xOrigin, e.y);
					zoomShell.setPosition(e.x - xOrigin, e.y);
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

	protected int calcSpeed(Canvas canvas2, int yOrigin2, int y) {
		int height = canvas.getClientArea().height;
		double maxSpeed = dataLimits.getMaxSpeed();
		Point maxP = dataItem4Graph.getPoint(0, (int) maxSpeed);
		
		int ordinate=height-yOrigin;
		
	//	int x=(int) ((maxP.getY())/maxSpeed*((height-y)*1.5));
	int	x=(int) ((ordinate-y)*(ordinate/1.5)/maxP.getY());

		return x;
	}

	private void startGraphArea() {
		canvas.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event e) {// x/100=old/d

				Tracker tracker = new Tracker(shell, SWT.NONE);
				tracker.setRectangles(
						new Rectangle[] { new Rectangle(e.x, e.y, (int) (SIZESELECTOR * WINFACT), SIZESELECTOR), });
				if (tracker.open()) {
					int width = canvas.getClientArea().width;
					int height = canvas.getClientArea().height;
					double zoomX = (width - xOrigin) / SIZESELECTOR;
					double zoomY = (height - yOrigin) / SIZESELECTOR;

					Shell s = new Shell(Display.getDefault());
					s.setBounds(0, 0, (int) (500 * WINFACT), 500);
					GraphArea gr = new GraphArea(tele, s, zoomX, zoomY);
					gr.setMoveIndex(moveIndex);
					gr.setX(e.x);
					gr.setY(e.y + SIZESELECTOR);
					gr.setFirstData(firstData);
					gr.setSecondData(secondData);
					s.open();
					s.layout(true, true);
					s.setVisible(true);
				}
			}
		});
	}

	public int getIndexFromX(int x) {
		int width = canvas.getClientArea().width;
		return (int) (((x - xOrigin) * firstData.size()) / (width - xOrigin));
	}

	protected void drawLines(GC gc) {
		int width = canvas.getClientArea().width;
		int height = canvas.getClientArea().height;
		xOrigin = width / 20;
		yOrigin = height / 20;
		gc.drawLine(xOrigin, height - yOrigin, width, height - yOrigin);
		gc.drawLine(xOrigin, height - yOrigin, xOrigin, 0);
	}

	public void drawData(GC gc, List<DataItem> data, Color col, Canvas canvas) {
		Point prec = new Point(0, 0);
		int height = canvas.getClientArea().height;
		gc.setForeground(col);
		gc.setBackground(col);

		for (DataItem d : data) {

			// DataItem4Graph item = new DataItem4Graph(canvas.getClientArea(), dataLimits,
			// xOrigin);
			Point p = dataItem4Graph.getPoint(d, currentDataType);

			int x1 = prec.getX();
			int y1 = prec.getY();
			int x2 = p.getX();
			int y2 = p.getY();

			drawLine(gc, x1, y1, x2, y2);

			prec = p;
		}

	}

	private void drawLine(GC gc, int xx1, int yy1, int xx2, int yy2) {
		int height = canvas.getClientArea().height;
		int x1 = xOrigin + xx1;
		int y1 = -yOrigin + height - yy1;
		int x2 = xOrigin + xx2;
		int y2 = -yOrigin + height - yy2;

		gc.drawLine(x1, y1, x2, y2);

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

		Composite speed1 = new Composite(right, SWT.NONE);
		speed1.setLayout(new FillLayout());
		Label speedLabel = new Label(speed1, SWT.NONE);
		speedLabel.setText("Speed");

		speedVal1 = new Text(speed1, SWT.BORDER);
		speedVal1.setEditable(false);

		Composite speed2 = new Composite(right, SWT.NONE);
		speed2.setLayout(new FillLayout());
		Label speedLabel2 = new Label(speed2, SWT.NONE);
		speedLabel2.setText("Speed");

		speedVal2 = new Text(speed2, SWT.BORDER);
		speedVal2.setEditable(false);

		return right;
	}

	private void createRadioDataType(Composite parent) {
		Group group1 = new Group(parent, SWT.SHADOW_IN);
		group1.setText("Data type");
		group1.setLayout(new RowLayout(SWT.HORIZONTAL));
		Button b_time = new Button(group1, SWT.RADIO);
		b_time.setText("Time");
		Button b_km = new Button(group1, SWT.RADIO);
		b_km.setText("m");

		b_time.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				dataLimits.setCurrentType(DataType.TIME);
				setdataTypeToShow(DataType.TIME);
				dataItem4Graph = new DataItem4Graph(canvas.getClientArea(), dataLimits, tele);
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
				dataItem4Graph = new DataItem4Graph(canvas.getClientArea(), dataLimits, tele);
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
		// dataLimits.clear();

		DataItem dataFirst = firstData.get(firstData.size() - 1);
		DataItem dataSec = secondData.get(secondData.size() - 1);
		dataLimits.clear();
		if (d == DataType.KM) {
			dataLimits.setMaxVal(dataFirst.getKm());
			dataLimits.setMaxVal(dataSec.getKm());
		} else {
			dataLimits.setMaxVal(dataFirst.getTime());
			dataLimits.setMaxVal(dataSec.getTime());
		}
		factor = canvas.getClientArea().width / dataLimits.getMax();
	}

	private Composite createlowerButtons() {
		Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
//		Button b1 = new Button(comp, SWT.PUSH);
//		b1.setText("ok");
//		Button b2 = new Button(comp, SWT.PUSH);
//		b2.setText("ok");

		file1Field = fileDescriptor(comp);
		file2Field = fileDescriptor(comp);

		/*
		 * GridData dataf1 = new GridData(GridData.FILL_BOTH); dataf1.horizontalSpan =
		 * 2; dataf1.verticalSpan = 1; f1.setLayoutData(dataf1);
		 * 
		 * GridData dataf2 = new GridData(GridData.FILL_BOTH); dataf2.horizontalSpan =
		 * 8; dataf2.verticalSpan = 1; f1.setLayoutData(dataf2);
		 */
		return comp;
	}

	private Text fileDescriptor(Composite parent) {
		Composite f1 = new Composite(parent, SWT.NONE);
		f1.setLayout(new GridLayout(5, false));
		Label f1Label = new Label(f1, SWT.NONE);
		Text file1Field = new Text(f1, SWT.BORDER);

		GridData data1 = new GridData(GridData.FILL_BOTH);
		data1.horizontalSpan = 1;
		data1.verticalSpan = 1;
		f1.setLayoutData(data1);

		GridData data2 = new GridData(GridData.FILL_BOTH);
		data2.horizontalSpan = 4;
		data2.verticalSpan = 1;
		file1Field.setLayoutData(data2);

		f1Label.setText("File 1");

		file1Field.setEditable(false);

		return file1Field;
	}

	private void loadData(String fineName, List<DataItem> data, int start, int end) {
		int f = 1;
		data.clear();
		try {
			try (BufferedReader br = new BufferedReader(new FileReader(fineName))) {
				int count = 0;
				String line;
				while ((line = br.readLine()) != null) {
					count++;
					if (count == 15)
						f = analizeSpace(line);
					if (count < 18)
						continue;
					DataItem dd = new DataItem(line, f);
					data.add(dd);

					dataLimits.setMaxVal(dd.getKm());
					dataLimits.setMaxVal(dd.getTime());

					dataLimits.setMaxSpeed(dd.getSpeed());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int analizeSpace(String line) {

		String[] vals = line.split(",");

		boolean isKm = vals[1].indexOf("k") != -1;

		if (isKm)
			return 1000;
		return 1;
	}

	public void loadData1(String file1, int start, int end) {
		loadData(file1, firstData, start, end);
		file1Field.setText(file1);
		shell.layout(true, true);
	}

	public void loadData2(String file2, int start, int end) {
		loadData(file2, secondData, start, end);
		file2Field.setText(file2);
		shell.layout(true, true);
	}

	public int getxOrigin() {
		return xOrigin;
	}

	public DataType getCurrentDataType() {
		return currentDataType;
	}

	public DataLimits getDataLimits() {
		return dataLimits;
	}

	public DataItem4Graph getDataItem4Graph() {
		return dataItem4Graph;
	}

	public int getyOrigin() {
		return yOrigin;
	}

	public ZoomMonitor getZoomMonitor() {
		return zoomMonitor;
	}

	public void setZoomMonitor(ZoomMonitor zoomMonitor) {
		this.zoomMonitor = zoomMonitor;
	}

	public boolean isZoomActive() {
		return zoomActive;
	}

	public void setZoomActive(boolean zoomActive) {
		this.zoomActive = zoomActive;
	}

	public List<DataItem> getFirstData() {
		return firstData;
	}

	public List<DataItem> getSecondData() {
		return secondData;
	}

	public void setZoomShell(ZoomShell gr) {
		this.zoomShell = gr;

	}

}
