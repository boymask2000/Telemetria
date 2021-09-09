package tele;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import common.Point;

public class ZoomShell extends Composite {
	private ZoomMonitor zoom;
	private int xOrg;
	private int yOrg;
	private int width;
	private int height;
	private int xOrigin;
	private int yOrigin;
	
	private boolean bshowMax = false;
	private boolean bshowMin = false;
	private boolean bshowAscisse = false;
	private boolean bshowOrdinate = false;
//	private DataVisualizer dataVisualizer;

	public ZoomShell(Tele main, Shell s) {
		super(s, SWT.NONE);
		setBounds(0, 0, 600, 500);
		setLayout(new GridLayout(20, false));

		zoom = new ZoomMonitor(this, main);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 18;
		zoom.setLayoutData(data);

		Composite right = createRightButtons(this);
		GridData data2 = new GridData(GridData.BEGINNING);
		data2.horizontalSpan = 2;
		right.setLayoutData(data2);

		main.setZoomMonitor(zoom);

		this.width = getClientArea().width;
		this.height = getClientArea().height;
		
//		dataVisualizer = new DataVisualizer(this);
//		dataVisualizer.setData(zoom.getTelemetria().getDataLoader().getFirstData());
//		GridData data3 = new GridData(GridData.BEGINNING);
//		data3.horizontalSpan = 1;
//		dataVisualizer.setLayoutData(data3);
	}

	private Composite createRightButtons(Composite shell) {

		Composite right = new Composite(shell, SWT.NONE);
		right.setLayout(new FillLayout(SWT.VERTICAL));

	
		Button showMax = new Button(right, SWT.CHECK);
		showMax.setText("Show max");
		Button showMin = new Button(right, SWT.CHECK);
		showMin.setText("Show min");
		Button showAsc = new Button(right, SWT.CHECK);
		showAsc.setText("Show time/km");
		Button showOrd = new Button(right, SWT.CHECK);
		showOrd.setText("Show Speed");

		showMax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				bshowMax = !bshowMax;
				zoom.setShowMax(bshowMax);
			}
		});
		showMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				bshowMin = !bshowMin;
				zoom.setShowMin(bshowMin);
			}
		});
		showAsc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				bshowAscisse = !bshowAscisse;
				zoom.setShowAscisse(bshowAscisse);
			}
		});
		showOrd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				bshowOrdinate = !bshowOrdinate;
				zoom.setShowOrdinate(bshowOrdinate);
			}
		});

		return right;
	}
	public void refreshGraph() {
		zoom.refreshGraph();
	}
	public void setPosition(Point screenPoint, Point dataPoint, Point p100) {
		zoom.setPosition(screenPoint, dataPoint, p100);


	}

}
