package common;

import telemetria.DataType;

public class DataLimits {

	private double max = 0;
	private DataType dataType;
	private double maxSpeed = 0;

	private double maxValSpace;
	private double maxValTime;

	public double getMax() {
		return max;
	}

	public void setMaxVal(double v) {
		if (v > max) {
			max = v;

		}
	}

	public void setMaxVal(DataItem d) {

		if (dataType == DataType.KM)
			setMaxVal(d.getKm());
		else
			setMaxVal(d.getTime());

	}

	public void clear() {
		max = 0;
	}

	public void setCurrentType(DataType k) {
		dataType = k;

	}

	public void setMaxSpeed(double speed) {
		if (speed > maxSpeed)
			this.maxSpeed = speed;

	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxValSpace(double km) {
		if (km > maxValSpace)
			maxValSpace = km;

	}

	public void setMaxValTime(double time) {
		if (time > maxValTime)
			maxValTime = time;

	}

	public double getMaxValSpace() {
		return maxValSpace;
	}

	public double getMaxValTime() {
		return maxValTime;
	}
}
