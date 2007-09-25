package org.drools.analytics.result;

import org.drools.analytics.components.Field;

/**
 * 
 * @author Toni Rikkola
 */
public interface RangeCheckCause extends Cause {

	public Field getField();

	public String getValueAsString();

	public String getEvaluator();
}
