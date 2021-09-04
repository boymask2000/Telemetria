package telemetria;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ZoomShell extends Composite {
	private ZoomMonitor zoom;
	private int xOrg;
	private int yOrg;
	private int width;
	private int height;
	private int xOrigin;
	private int yOrigin;

	public ZoomShell(Telemetria main, Shell s) {
		super(s, SWT.NONE);
		setBounds(0, 0, 600, 500);
		zoom = new ZoomMonitor(this, main);
		main.setZoomMonitor(zoom);
		
		this. width = getClientArea().width;
		this.height = getClientArea().height;
	}
	


	public void setPosition(int x, int y) {
		xOrg = x;
		yOrg = y;

		zoom.setPosition(x, y);

	}

}
