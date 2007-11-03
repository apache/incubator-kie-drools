package org.drools.analytics.report.components;

import org.drools.analytics.components.Field;
import org.drools.base.evaluators.Operator;

/**
 * 
 * @author Toni Rikkola
 */
public interface RangeCheckCause extends Cause {

	public Field getField();

	public Object getValueAsObject();

	public String getValueAsString();

	public Operator getOperator();
}
