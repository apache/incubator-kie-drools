package org.drools.analytics.components;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.DataFormatException;

import org.drools.analytics.report.components.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public class LiteralRestriction extends Restriction implements Cause {

	private Field.FieldType valueType;

	private boolean booleanValue;

	private int intValue;

	private double doubleValue;

	private String stringValue;

	private Date dateValue;

	public RestrictionType getRestrictionType() {
		return Restriction.RestrictionType.LITERAL;
	}

	/**
	 * Compares two LiteralRestrictions by value.
	 * 
	 * @param restriction
	 *            Restriction that this object is compared to.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 * @throws DataFormatException
	 *             If data was not supported.
	 */
	public int compareValues(LiteralRestriction restriction)
			throws DataFormatException {
		if (restriction.getValueType() != valueType) {
			throw new DataFormatException(
					"Value types did not match. Value type "
							+ restriction.getValueType() + " was compared to "
							+ valueType);
		}
		switch (valueType) {
		case DATE:
			return dateValue.compareTo(restriction.getDateValue());
		case DOUBLE:
			if (doubleValue > restriction.getDoubleValue()) {
				return 1;
			} else if (doubleValue < restriction.getDoubleValue()) {
				return -1;
			} else {
				return 0;
			}
		case INT:
			if (intValue > restriction.getIntValue()) {
				return 1;
			} else if (intValue < restriction.getIntValue()) {
				return -1;
			} else {
				return 0;
			}
		case STRING:
			return stringValue.compareTo(restriction.getValueAsString());
		default:
			throw new DataFormatException(
					"Value types did not match. Value type "
							+ restriction.getValueType() + " was compared to "
							+ valueType);
		}
	}

	public Object getValueAsObject() {
		switch (valueType) {
		case BOOLEAN:
			return Boolean.valueOf(booleanValue);
		case DATE:
			return dateValue;
		case DOUBLE:
			return Double.valueOf(doubleValue);
		case INT:
			return Integer.valueOf(intValue);

		default:
			return stringValue;
		}
	}

	public String getValueAsString() {
		return stringValue;
	}

	public double getDoubleValue() {
		return doubleValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public Field.FieldType getValueType() {
		return valueType;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setValue(String value) {

		if ("true".equals(value) || "false".equals(value)) {
			booleanValue = value.equals("true");
			valueType = Field.FieldType.BOOLEAN;
			stringValue = value;
		}

		try {
			intValue = Integer.parseInt(value);
			valueType = Field.FieldType.INT;
			stringValue = value;
			return;
		} catch (NumberFormatException e) {
			// Not int.
		}

		try {
			doubleValue = Double.parseDouble(value);
			valueType = Field.FieldType.DOUBLE;
			stringValue = value;
			return;
		} catch (NumberFormatException e) {
			// Not double.
		}

		try {
			String fmt = System.getProperty("drools.dateformat");
			if (fmt == null) {
				fmt = "dd-MMM-yyyy";
			}

			dateValue = new SimpleDateFormat(fmt, Locale.ENGLISH).parse(value);
			valueType = Field.FieldType.DATE;
			stringValue = value;
			return;
		} catch (Exception e) {
			// Not a date.
		}

		stringValue = value;
		valueType = Field.FieldType.STRING;
	}

	public boolean isBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	@Override
	public String toString() {
		return "LiteralRestriction from rule '" + ruleName + "' value '"
				+ operator.getOperatorString() + " " + stringValue + "'";
	}
}
