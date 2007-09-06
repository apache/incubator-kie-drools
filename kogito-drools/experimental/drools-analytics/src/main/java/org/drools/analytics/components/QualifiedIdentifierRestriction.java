package org.drools.analytics.components;

/**
 * 
 * @author Toni Rikkola
 */
public class QualifiedIdentifierRestriction extends Restriction {

	private int variableId;
	private String variableName;
	private String variablePath;

	@Override
	public RestrictionType getRestrictionType() {
		return RestrictionType.QUALIFIED_IDENTIFIER;
	}

	@Override
	public String getValueAsString() {
		return variablePath + "." + variableName;
	}

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

	public String getVariablePath() {
		return variablePath;
	}

	public void setVariablePath(String variablePath) {
		this.variablePath = variablePath;
	}

	@Override
	public String toString() {
		return "QualifiedIdentifierRestriction name: " + variableName
				+ variablePath;
	}
}
