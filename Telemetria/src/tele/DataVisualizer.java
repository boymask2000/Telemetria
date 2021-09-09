package tele;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import common.DataItem;

public class DataVisualizer extends Composite {
	public DataVisualizer(Composite parent) {
		super(parent,SWT.NONE);
		
		setLayout(new GridLayout(1,false));
	}
	public void setData( List<DataItem> datas) {
		for( DataItem item: datas) {
			Label l = new Label(this, SWT.NONE);
			GridData data2 = new GridData(GridData.BEGINNING);
			data2.horizontalSpan = 1;
		
			l.setLayoutData(data2);
			
			l.setText(""+item.getSpeed());
		}
		
	}

}
