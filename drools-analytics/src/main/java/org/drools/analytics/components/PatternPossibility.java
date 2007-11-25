package org.drools.analytics.components;

import java.util.HashSet;
import java.util.Set;

import org.drools.analytics.report.components.Cause;

/**
 * Instance of this class represents a possible combination of Constraints under
 * one Pattern. Each possibility returns true if all the Constraints in the
 * combination are true.
 * 
 * @author Toni Rikkola
 */
public class PatternPossibility extends AnalyticsComponent implements
		Possibility {
	private static final long serialVersionUID = 8871361928380977116L;

	private static int index = 0;

	private int patternId;
	private int ruleId;
	private Set<Cause> items = new HashSet<Cause>();

	public PatternPossibility() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.PATTERN_POSSIBILITY;
	}

	public CauseType getCauseType() {
		return Cause.CauseType.PATTERN_POSSIBILITY;
	}

	public Set<Cause> getItems() {
		return items;
	}

	public int getAmountOfItems() {
		return items.size();
	}

	public int getPatternId() {
		return patternId;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}

	public void add(Restriction restriction) {
		items.add(restriction);
	}

	@Override
	public String toString() {
		return "PatternPossibility from rule: " + ruleName
				+ ", amount of items:" + items.size();
	}
}