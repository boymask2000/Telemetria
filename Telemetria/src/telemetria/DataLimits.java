package telemetria;

public class DataLimits {

	private double max = 0;
	private DataType dataType;
	private double maxSpeed;

	public double getMax() {
		return max;
	}

	public void setMaxVal(double v) {
		if (v > max) {
			max = v;

		}
	}

	public void clear() {
		max = 0;
	}

	public void setCurrentType(DataType k) {
		dataType = k;

	}

	public void setMaxVal(DataItem d) {

		if (dataType == DataType.KM)
			setMaxVal(d.getKm());
		else
			setMaxVal(d.getTime());

	}

	public void setMaxSpeed(double speed) {
		this.maxSpeed = speed;

	}

	public double getMaxSpeed() {
		return maxSpeed;
	}
}
