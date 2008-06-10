package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class Variable extends AnalyticsComponent {

	private static int index;

	private int ruleId;
	private AnalyticsComponentType objectType;
	private int objectId;
	private String objectName;
	private String name;

	public Variable() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.VARIABLE;
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

	public AnalyticsComponentType getObjectType() {
		return objectType;
	}

	public void setObjectType(AnalyticsComponentType type) {
		// AnalyticsComponentType.CLASS dominates AnalyticsComponentType.FIELD.
		if (objectType == null || objectType != AnalyticsComponentType.CLASS) {
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
