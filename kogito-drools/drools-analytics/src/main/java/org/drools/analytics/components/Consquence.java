package org.drools.analytics.components;

public interface Consquence {

	public static enum ConsequenceType {
		TEXT
	}

	public int getId();

	public ConsequenceType getConsequenceType();
	
	public int getRuleId();

	public String getRuleName();
	
}
