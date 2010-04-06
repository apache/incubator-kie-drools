package org.drools.verifier.components;

/**
 * 
 * @author trikkola
 * 
 */
public class StringRestriction extends LiteralRestriction {

	private String value;

	public StringRestriction(Pattern pattern) {
		super(pattern);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String getValueAsString() {
		if (value == null) {
			return "";
		} else {
			return value;
		}
	}

	@Override
	public String getValueType() {
		if (value == null) {
			return Field.UNKNOWN;
		} else {
			return value.getClass().getName();
		}
	}
}
