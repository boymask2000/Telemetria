package tele;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import telemetria.DataItem;
import telemetria.DataLimits;

public class DataLoader {
	private List<DataItem> firstData = new ArrayList<>();
	private List<DataItem> secondData = new ArrayList<>();
	private Tele tele;
	private DataLimits dataLimits;

	public DataLoader(Tele tele) {
		this.tele = tele;
		this.dataLimits = tele.getDataLimits();
	}

	public void loadData1(String file1, int start, int end) {
		loadData(file1, firstData, start, end);

		tele.setVal_file1Field(file1);

	}

	public void loadData2(String file2, int start, int end) {
		loadData(file2, secondData, start, end);

		tele.setVal_file2Field(file2);

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

	public List<DataItem> getFirstData() {
		return firstData;
	}

	public List<DataItem> getSecondData() {
		return secondData;
	}
}
