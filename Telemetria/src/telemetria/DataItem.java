package telemetria;

public class DataItem {
	private double time;
	private double km;
	private double speed;

	public DataItem(String line, int f) {
		String[] vals = line.split(",");
		String s_time = clean(vals[0]);
		String s_km = clean(vals[1]);
		String s_speed = clean(vals[2]);
		
		time=Double.parseDouble(s_time);
		km=f*Double.parseDouble(s_km);
		speed=Double.parseDouble(s_speed);
	}
//	public static void main(String s[]) {
//		DataItem dt = new DataItem("0.300,0.013,159.995010");
//		DataItem dt1 = new DataItem("\"0.300\",\"14\",\"169.1\"");
//		System.out.println(dt.toString());
//		System.out.println(dt1.toString());
//	}

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

}
