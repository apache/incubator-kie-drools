package org.drools.analytics.report.components;

import org.drools.analytics.components.Field;

/**
 * 
 * @author Toni Rikkola
 */
public interface RangeCheckCause extends Cause {

	public Field getField();

	public Object getValueAsObject();

	public String getValueAsString();

	public String getEvaluator();
}
