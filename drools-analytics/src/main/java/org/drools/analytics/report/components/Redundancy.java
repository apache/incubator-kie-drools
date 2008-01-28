package org.drools.analytics.report.components;

/**
 * Presents a redundancy between two Causes. The link between them can be WEAK
 * or STRONG.
 * 
 * WEAK redundancy is for example two AnalyticsRules, but not theyr's rule
 * possibilities. STRONG redundancy includes possibilities.
 * 
 * @author Toni Rikkola
 */
public class Redundancy extends Subsumption implements Cause {
	// By default the redundancy is weak.
	private RedundancyType type = RedundancyType.WEAK;

	public Redundancy(Cause left, Cause right) {
		super(left, right);
	}

	public Redundancy(RedundancyType type, Cause left, Cause right) {
		super(left, right);
		this.type = type;
	}

	public CauseType getCauseType() {
		return CauseType.REDUNDANCY;
	}

	public RedundancyType getType() {
		return type;
	}

	public void setType(RedundancyType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Redundancy between: (" + getLeft() + ") and (" + getRight()
				+ ").";
	}
}
