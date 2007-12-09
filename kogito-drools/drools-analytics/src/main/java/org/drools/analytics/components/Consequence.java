package org.drools.analytics.components;

import org.drools.analytics.report.components.Cause;


public interface Consequence extends Cause{

	public static enum ConsequenceType {
		TEXT
	}

	public int getId();

	public ConsequenceType getConsequenceType();

	public int getRuleId();

	public String getRuleName();

}
