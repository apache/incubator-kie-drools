package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class Variable extends VerifierComponent {

	private static int index;

	private int ruleId;
	private VerifierComponentType objectType;
	private int objectId;
	private String objectName;
	private String name;

	public Variable() {
		super(index++);
	}

	@Override
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.VARIABLE;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public int getObjectId() {
		return objectId;
	}

	public void setObjectId(int variableId) {
		this.objectId = variableId;
	}

	public VerifierComponentType getObjectType() {
		return objectType;
	}

	public void setObjectType(VerifierComponentType type) {
		// VerifierComponentType.CLASS dominates VerifierComponentType.FIELD.
		if (objectType == null || objectType != VerifierComponentType.CLASS) {
			this.objectType = type;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	@Override
	public String toString() {
		return "Variable name: " + name;
	}
}
