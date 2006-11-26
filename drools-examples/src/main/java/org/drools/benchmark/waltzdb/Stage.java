package org.drools.benchmark.waltzdb;

public class Stage {
	final public static String DUPLICATE = "A";
	final public static String DETECT_JUNCTIONS = "B";
	final public static String FIND_INITIAL_BOUNDARY = "C";
	final public static String FIND_SECOND_BOUDARY = "D";
	final public static String LABELING = "E";
	final public static String VISITING_3J = "F";
	final public static String VISITING_2J = "G";
	final public static String MARKING = "H";
	final public static String CHECKING = "I";
	final public static String REMOVE_LABEL = "J";
	final public static String PRINTING = "K";
	
	private String value;

	public Stage() {
		super();
	}

	public Stage(String value) {
		super();
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Stage other = (Stage) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	} 
}
