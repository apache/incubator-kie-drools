package org.drools.analytics.result;

/**
 * 
 * @author Toni Rikkola
 */
public interface RangeCheckCause extends Cause {

	public String getValueAsString();

	public String getEvaluator();
}
