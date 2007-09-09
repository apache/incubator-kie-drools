package org.drools.analytics.result;

/**
 * 
 * @author Toni Rikkola
 */
public class MissingNumberPattern implements RangeCheckCause {

	private static int index = 0;

	private int id = index++;

	private int fieldId;
	private String evaluator;
	private String value;

	public MissingNumberPattern(int fieldId, String evaluator, String value) {
		this.fieldId = fieldId;
		this.evaluator = evaluator;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	/**
	 * Returns alway null, because there is no rule that this is related to.
	 */
	public String getRuleName() {
		return null;
	}

	public String getEvaluator() {
		return evaluator;
	}

	public String getValueAsString() {
		return value;
	}

	@Override
	public String toString() {
		return "Missing restriction " + evaluator + " " + value;
	}
}
