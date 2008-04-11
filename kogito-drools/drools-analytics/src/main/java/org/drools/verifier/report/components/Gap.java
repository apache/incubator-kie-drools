package org.drools.verifier.report.components;

import org.drools.base.evaluators.Operator;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.LiteralRestriction;

/**
 *
 * @author Toni Rikkola
 */
public class Gap extends MissingRange implements RangeCheckCause,
		Comparable<MissingRange> {

	private LiteralRestriction restriction;

	public int compareTo(MissingRange another) {
		return super.compareTo(another);
	}

	public CauseType getCauseType() {
		return CauseType.GAP;
	}

	/**
	 *
	 * @param field
	 *            Field from where the value is missing.
	 * @param evaluator
	 *            Evaluator for the missing value.
	 * @param cause
	 *            The restriction that the gap begins from.
	 */
	public Gap(Field field, Operator operator, LiteralRestriction restriction) {
		this.field = field;
		this.operator = operator;
		this.restriction = restriction;
	}

	public String getRuleName() {
		return restriction.getRuleName();
	}

	public LiteralRestriction getRestriction() {
		return restriction;
	}

	public void setRestriction(LiteralRestriction restriction) {
		this.restriction = restriction;
	}

	public String getValueAsString() {
		return restriction.getValueAsString();
	}

	public Object getValueAsObject() {
		return restriction.getValueAsObject();
	}

	@Override
	public String toString() {
		return "Gap: (" + field + ") " + getOperator() + " "
				+ getValueAsString() + " from rule: [" + getRuleName() + "]";
	}
}
