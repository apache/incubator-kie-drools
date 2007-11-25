package org.drools.analytics.components;

public interface Consequence {

	public static enum ConsequenceType {
		TEXT
	}

	public int getId();

	public ConsequenceType getConsequenceType();
	
	public int getRuleId();

	public String getRuleName();
	
}
