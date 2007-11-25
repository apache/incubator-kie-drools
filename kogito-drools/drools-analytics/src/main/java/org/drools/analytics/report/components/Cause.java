package org.drools.analytics.report.components;

/**
 * 
 * @author Toni Rikkola
 */
public interface Cause {
	public enum CauseType {
		RULE,
		FIELD,
		GAP,
		PATTERN,
		RESTRICTION,
		PATTERN_POSSIBILITY,
		RULE_POSSIBILITY,
		RANGE_CHECK_CAUSE, 
		REDUNDANCY, 
		EVAL,
		PREDICATE, 
		CONSTRAINT
	}
	
	public int getId();

	public CauseType getCauseType();
}
