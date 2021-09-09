package tele;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import common.DataItem;
import common.DataLimits;

public class DataManager {
	private List<DataItem> firstData = new ArrayList<>();
	private List<DataItem> secondData = new ArrayList<>();
	private Tele tele;
	private DataLimits dataLimits;

	public DataManager(Tele tele) {
		this.tele = tele;
		this.dataLimits = tele.getDataLimits();

	}

	public void dumpData1() {
		for (DataItem d : firstData)
			System.out.println(d.toString());
	}

	public void loadData1(String file1, int start, int end) {
		loadData(file1, firstData, start, end);

		tele.setVal_file1Field(file1);
		processMaxMin(firstData);
	}

	public void loadData2(String file2, int start, int end) {
		loadData(file2, secondData, start, end);

		tele.setVal_file2Field(file2);
		processMaxMin(secondData);
	}

	private void processMaxMin(List<DataItem> datas) {
		double precVal = 0;

		for (int i = 0; i < datas.size() - 1; i++) {
			if (i == 0) {
				precVal = datas.get(i).getSpeed();

				continue;
			}
			double val = datas.get(i).getSpeed();
			double nextVal = datas.get(i + 1).getSpeed();

			if (val > precVal && val > nextVal)
				datas.get(i).setValoreMax(true);

			if (val < precVal && val < nextVal)
				datas.get(i).setValoreMin(true);

			precVal = val;
		}

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

					dataLimits.setMaxValSpace(dd.getKm());
					dataLimits.setMaxValTime(dd.getTime());

					dataLimits.setMaxSpeed(dd.getSpeed());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int calcDataShift() {

		double minVal = 1000000;
		int index = -1;
		for (int shift = 1; shift < firstData.size(); shift++) {
			List<DataItem> ll = shiftList(firstData, shift);
			double val = calcSpreadIntern(ll, secondData);
			if (val < minVal) {
				minVal = val;
				index = shift;
				// System.out.println("val= " + val + " index=" + index);
			}
		}
		return index;
	}

	private static List<DataItem> shiftList(List<DataItem> ll, int shift) {
		List<DataItem> newList = new ArrayList<>();
		for (int i = 0; i < ll.size(); i++)
			newList.add(ll.get((i + shift) % ll.size()));

		return newList;
	}

	private static double calcSpreadIntern(List<DataItem> l1, List<DataItem> l2) {
		double diff = 0;
		for (int i = 0; i < l1.size() && i < l2.size(); i++) {
			diff += abs(l1.get(i).getSpeed() - l2.get(i).getSpeed());
		}
		return diff;
	}

	public double calcSpread() {
		double diff = 0;
		for (int i = 0; i < firstData.size() && i < secondData.size(); i++) {
			diff += abs(firstData.get(i).getSpeed() - secondData.get(i).getSpeed());
		}
		return diff;
	}

	private static double abs(double c) {
		if (c < 0)
			return -c;
		return c;
	}

	private static int analizeSpace(String line) {

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

	public void shiftData(int s) {
		List<DataItem> newList = new ArrayList<>();
		for (int i = 0; i < firstData.size(); i++) {
			newList.add(DataItem.copy(firstData.get(i)));
		}

		for (int i = 0; i < firstData.size(); i++) {
			int val=i+s;
			if(val<0)val=-val;
			DataItem d = firstData.get(val % firstData.size());

			newList.get(i).setSpeed(d.getSpeed());
			newList.get(i).setValoreMax(d.isValoreMax());
			newList.get(i).setValoreMin(d.isValoreMin());
		}
		firstData = newList;
	}

}
