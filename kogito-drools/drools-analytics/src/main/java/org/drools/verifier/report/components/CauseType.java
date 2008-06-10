package org.drools.verifier.report.components;

public class CauseType {

	public static final CauseType RULE = new CauseType(0);
	public static final CauseType FIELD = new CauseType(1);
	public static final CauseType GAP = new CauseType(2);
	public static final CauseType PATTERN = new CauseType(3);
	public static final CauseType RESTRICTION = new CauseType(4);
	public static final CauseType PATTERN_POSSIBILITY = new CauseType(5);
	public static final CauseType RULE_POSSIBILITY = new CauseType(6);
	public static final CauseType RANGE_CHECK_CAUSE = new CauseType(7);
	public static final CauseType REDUNDANCY = new CauseType(8);
	public static final CauseType EVAL = new CauseType(9);
	public static final CauseType PREDICATE = new CauseType(10);
	public static final CauseType CONSTRAINT = new CauseType(11);
	public static final CauseType CONSEQUENCE = new CauseType(12);
	public static final CauseType SUBSUMPTION = new CauseType(13);
	public static final CauseType OPPOSITE = new CauseType(14);
	public static final CauseType INCOMPATIBLE = new CauseType(15);

	public final int index;

	public CauseType(int i) {
		index = i;
	}
}
