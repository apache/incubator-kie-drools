package org.drools.analytics.components;

import org.drools.analytics.result.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public class VariableRestriction extends Restriction implements Cause {

	protected String variableName;
	protected int variableId;

	public int getVariableId() {
		return variableId;
	}

	public void setVariableId(int variableId) {
		this.variableId = variableId;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public RestrictionType getRestrictionType() {
		return Restriction.RestrictionType.VARIABLE;
	}

	@Override
	public String toString() {
		return "VariableRestriction from rule '" + ruleName + "' name '"
				+ variableName + "'";
	}
}