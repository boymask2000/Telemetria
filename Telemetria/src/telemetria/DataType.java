package telemetria;

public enum DataType {
	KM("Km"), TIME("Sec");

	private String type;

	public String getType() {
		return type;
	}

	private DataType(String val) {
		this.type = val;
	}

}
