package org.drools.verifier.report.components;


public class RedundancyType {
	public static final RedundancyType WEAK = new RedundancyType(0);
	public static final RedundancyType STRONG = new RedundancyType(0);

	public final int index;

	public RedundancyType(int i) {
		index = i;
	}
}
