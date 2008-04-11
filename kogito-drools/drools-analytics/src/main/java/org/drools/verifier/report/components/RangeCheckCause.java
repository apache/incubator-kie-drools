package org.drools.verifier.report.components;

import org.drools.base.evaluators.Operator;
import org.drools.verifier.components.Field;

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
