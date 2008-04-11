package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;

public interface Consequence extends Cause {

	public static class ConsequenceType {

		public static final ConsequenceType TEXT = new ConsequenceType(0);

		private final int index;

		private ConsequenceType(int i) {
			index = i;
		}
	}

	public int getId();

	public ConsequenceType getConsequenceType();

	public int getRuleId();

	public String getRuleName();

}
