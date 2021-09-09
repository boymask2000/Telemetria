package common;

public class DataItem {
	private double time;
	private double km;
	private double speed;

	private boolean valoreMin = false;
	private boolean valoreMax = false;

	public DataItem() {

	}
	
	public static DataItem copy(DataItem d) {
		DataItem newDt=new DataItem();
		newDt.setKm(d.getKm());
		newDt.setSpeed(d.getSpeed());
		newDt.setTime(d.getTime());
		newDt.setValoreMax(d.isValoreMax());
		newDt.setValoreMin(d.isValoreMin());
		return newDt;
	}

	public DataItem(String line, int f) {
		String[] vals = line.split(",");
		String s_time = clean(vals[0]);
		String s_km = clean(vals[1]);
		String s_speed = clean(vals[2]);

		time = Double.parseDouble(s_time);
		km = f * Double.parseDouble(s_km);
		speed = Double.parseDouble(s_speed);
	}

	@Override
	public String toString() {
		return "DataItem [time=" + time + ", km=" + km + ", speed=" + speed + "]";
	}

	private String clean(String s) {
		if (s.startsWith("\""))
			s = s.substring(1);
		if (s.endsWith("\""))
			s = s.substring(0, s.length() - 1);
		return s;
	}

	public double getTime() {
		return time;
	}

	public double getKm() {
		return km;
	}

	public double getSpeed() {
		return speed;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public void setKm(double km) {
		this.km = km;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public boolean isValoreMin() {
		return valoreMin;
	}

	public void setValoreMin(boolean valoreMin) {
		this.valoreMin = valoreMin;
	}

	public boolean isValoreMax() {
		return valoreMax;
	}

	public void setValoreMax(boolean valoreMax) {
		this.valoreMax = valoreMax;
	}

}
