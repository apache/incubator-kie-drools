package org.drools.verifier.components;

import org.drools.base.evaluators.Operator;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

/**
 *
 * @author Toni Rikkola
 */
public abstract class Restriction extends VerifierComponent implements Cause {

	public static class RestrictionType {
		public static final RestrictionType LITERAL = new RestrictionType(0);
		public static final RestrictionType VARIABLE = new RestrictionType(1);
		public static final RestrictionType QUALIFIED_IDENTIFIER = new RestrictionType(
				2);
		public static final RestrictionType RETURN_VALUE_RESTRICTION = new RestrictionType(
				3);

		private final int index;

		private RestrictionType(int i) {
			index = i;
		}
	}

	private static int index = 0;

	private int patternId;
	private boolean patternIsNot;
	private int constraintId;

	// Id of the field that this restriction is related to.
	private int fieldId;

	protected Operator operator;

	public Restriction() {
		super(index++);
	}

	@Override
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.RESTRICTION;
	}

	public CauseType getCauseType() {
		return CauseType.RESTRICTION;
	}

	public abstract RestrictionType getRestrictionType();

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public int getConstraintId() {
		return constraintId;
	}

	public void setConstraintId(int constraintId) {
		this.constraintId = constraintId;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public int getPatternId() {
		return patternId;
	}

	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public boolean isPatternIsNot() {
		return patternIsNot;
	}

	public void setPatternIsNot(boolean patternIsNot) {
		this.patternIsNot = patternIsNot;
	}
}
