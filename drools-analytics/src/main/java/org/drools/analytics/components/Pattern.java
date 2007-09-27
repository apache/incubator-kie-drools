package org.drools.analytics.components;

import org.drools.analytics.result.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public class Pattern extends AnalyticsComponent implements Cloneable, Cause {

	private static final long serialVersionUID = 5852308145251025423L;

	private static int index = 0;

	private int ruleId;
	private int classId;
	private AnalyticsComponentType sourceType = AnalyticsComponentType.NOTHING;
	private int sourceId = -1;

	private boolean isPatternNot = false;
	private boolean isPatternExists = false;
	private boolean isPatternForall = false;

	public Pattern() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.PATTERN;
	}

	public CauseType getCauseType() {
		return Cause.CauseType.PATTERN;
	}

	public boolean isPatternNot() {
		return isPatternNot;
	}

	public void setPatternNot(boolean isNot) {
		this.isPatternNot = isNot;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public boolean isPatternExists() {
		return isPatternExists;
	}

	public void setPatternExists(boolean isExists) {
		this.isPatternExists = isExists;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public boolean isPatternForall() {
		return isPatternForall;
	}

	public void setPatternForall(boolean isForall) {
		this.isPatternForall = isForall;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public AnalyticsComponentType getSourceType() {
		return sourceType;
	}

	public void setSourceType(AnalyticsComponentType sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	public Object clone() {
		Pattern clone = new Pattern();
		clone.setRuleId(ruleId);
		clone.setClassId(classId);
		clone.setSourceType(sourceType);
		clone.setSourceId(sourceId);
		clone.setPatternNot(isPatternNot);
		clone.setPatternExists(isPatternExists);
		clone.setPatternForall(isPatternForall);

		return clone;
	}

	@Override
	public String toString() {
		return "Pattern, id: " + id + " from rule '" + ruleName + "'";
	}
}
