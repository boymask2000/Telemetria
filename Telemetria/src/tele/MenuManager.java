package tele;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MenuManager {
	private Tele main;
	private Shell shell;

	public MenuManager(Tele main, Shell shell) {
		this.main = main;
		this.shell = shell;

		Menu menuBar = new Menu(shell, SWT.BAR);

//		MenuItem itemHello = new MenuItem(menuBar, SWT.PUSH);
//	    itemHello.setText("&File");
//	    itemHello.addListener(SWT.Selection, new Listener() {
//			
//			@Override
//			public void handleEvent(Event arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});

		menuBar = new Menu(shell, SWT.BAR);
		MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText("&File");

		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);

		MenuItem load1 = new MenuItem(fileMenu, SWT.PUSH);
		load1.setText("Load 1");

		MenuItem load2 = new MenuItem(fileMenu, SWT.PUSH);
		load2.setText("Load 2");

		MenuItem helpMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		helpMenuHeader.setText("&About");

		load1.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String file1 = getFile();
				main.getDataLoader().loadData1(file1, 0, -1);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		load2.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String file2 = getFile();
				main.getDataLoader().loadData2(file2, 0, -1);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		MenuItem toolsMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		toolsMenuHeader.setText("&Tools");

		Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN);
		toolsMenuHeader.setMenu(toolsMenu);

		MenuItem zoom = new MenuItem(toolsMenu, SWT.PUSH);
		zoom.setText("Zoom");

		zoom.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {

//				Shell s = new Shell(Display.getDefault());
//				s.setBounds(0, 0, 600, 500);
//				ZoomMonitor gr = new ZoomMonitor(s, main);
//				main.setZoomMonitor(gr);
//
//				s.layout(true, true);
//				s.setVisible(true);
//				main.setZoomActive(true);
				

				
				Shell s = new Shell(Display.getDefault());
				s.setBounds(0, 0, 600, 500);
				ZoomShell gr = new ZoomShell(main,s);
				main.setZoomShell(gr);
			

				s.layout(true, true);
				s.setVisible(true);
				main.setZoomActive(true);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		shell.setMenuBar(menuBar);
	}

	protected String getFile() {
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Open");
		fd.setFilterPath("C:/");
		String[] filterExt = { "*.csv" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		System.out.println(selected);
		return selected;
	}

}
