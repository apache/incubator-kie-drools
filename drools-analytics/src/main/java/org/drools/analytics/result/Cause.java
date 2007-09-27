package org.drools.analytics.result;

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
		POSSIBILITY,
		RANGE_CHECK_CAUSE
	}
	
	public int getId();
	
	public String getRuleName();

	public CauseType getCauseType();
}
