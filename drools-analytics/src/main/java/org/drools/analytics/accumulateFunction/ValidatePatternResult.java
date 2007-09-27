package org.drools.analytics.accumulateFunction;

/**
 * 
 * @author Toni Rikkola
 */
public class ValidatePatternResult {

	private Number value = null;

	public Number getValue() {
		return value;
	}

	public void setValue(Number value) {
		this.value = value;
	}

	public String toString() {
		if (value != null) {
			return value.toString();
		} else {
			return "Empty, value null";
		}
	}
}
