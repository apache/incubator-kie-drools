package org.drools.verifier.report.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.drools.base.evaluators.Operator;
import org.drools.verifier.components.Field;

/**
 * 
 * @author Toni Rikkola
 */
public class MissingNumberPattern extends MissingRange implements
		RangeCheckCause, Comparable<MissingRange> {

	private final Field.FieldType valueType;

	private final String value;

	public int compareTo(MissingRange another) {
		return super.compareTo(another);
	}

	public CauseType getCauseType() {
		return CauseType.RANGE_CHECK_CAUSE;
	}

	public MissingNumberPattern(Field field, Operator operator,
			Field.FieldType valueType, String value) {
		super(field, operator);

		this.valueType = valueType;
		this.value = value;
	}

	/**
	 * Returns alway null, because there is no rule that this is related to.
	 */
	public String getRuleName() {
		return null;
	}

	public String getValueAsString() {
		return value;
	}

	public Object getValueAsObject() {
		if (valueType == Field.FieldType.BOOLEAN) {
			return Boolean.valueOf(value);
		} else if (valueType == Field.FieldType.DATE) {
			try {
				String fmt = System.getProperty("drools.dateformat");
				if (fmt == null) {
					fmt = "dd-MMM-yyyy";
				}

				return new SimpleDateFormat(fmt, Locale.ENGLISH).parse(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (valueType == Field.FieldType.DOUBLE) {
			return Double.valueOf(value);
		} else if (valueType == Field.FieldType.INT) {
			return Integer.valueOf(value);
		}

		return value;
	}

	public Field.FieldType getValueType() {
		return valueType;
	}

	@Override
	public String toString() {
		return "Missing restriction " + operator + " " + value;
	}
}
