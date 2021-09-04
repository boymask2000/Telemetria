package tele;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import telemetria.DataItem;
import telemetria.DataItem4Graph;
import telemetria.DataLimits;
import telemetria.DataType;
import telemetria.Telemetria;

public class Tele {
	private static final String DIR = "C:\\Users\\GiovanniPosabella\\Desktop\\CICCA";
	public final static int HEIGHT = 600;
	public final static double WINFACT = 1.2;
	public final static int WIDTH = (int) (HEIGHT * WINFACT);

	private DataType currentDataType;
	
	private Tele tele;
	
	private int height;
	private int width;
	private DataLimits dataLimits = new DataLimits();
	private DataLoader dataLoader;
	
	private Canvas canvas;
	private Text file1Field;
	private Text file2Field;
	private Text dataType;
	private Text speedVal1;
	private Text speedVal2;
	private Label dataTypeLabel;

	private Shell shell;

	public static void main(String[] args) {
		new Tele();
	}

	public Tele() {
		this.tele = this;
		shell = new Shell(Display.getDefault());
		shell.setBounds(0, 0, WIDTH, HEIGHT);
		shell.setLayout(new GridLayout(20, false));
		this.dataLoader = new DataLoader(this);

		shell.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

				refreshGeometria();

			}

			private void refreshGeometria() {

				height = canvas.getClientArea().height;
				width = canvas.getClientArea().width;

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

				drawData(e.gc, dataLoader.getFirstData(), Display.getDefault().getSystemColor(SWT.COLOR_GREEN), canvas);
				drawData(e.gc, dataLoader.getSecondData(), Display.getDefault().getSystemColor(SWT.COLOR_RED), canvas);
			}
		});

		setButtons();

		initMouseMoveInfo();

		dataLoader.loadData1(DIR + "/" + "pr01.csv", 0, -1);
		dataLoader.loadData2(DIR + "/" + "pr01_m.csv", 0, -1);

		dataItem4Graph = new DataItem4Graph(canvas.getClientArea(), dataLimits, tele);

//		startGraphArea();

		shell.layout(true, true);

		while (!shell.isDisposed())
			Display.getDefault().readAndDispatch();
	}

	private void initMouseMoveInfo() {
		canvas.addMouseMoveListener(new MouseMoveListener() {

			public void mouseMove(MouseEvent e) {
				int height = canvas.getClientArea().height;
				int width = canvas.getClientArea().width;

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
		Label dataTypeLabel = new Label(datatype, SWT.NONE);
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

	public DataLoader getDataLoader() {
		return dataLoader;
	}

}
