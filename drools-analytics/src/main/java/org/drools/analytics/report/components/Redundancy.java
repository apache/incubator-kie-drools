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
public class Redundancy implements Cause {

	public enum RedundancyType {
		WEAK, STRONG
	}

	private static int index = 0;

	private int id = index++;
	// By default the redundancy is weak.
	private RedundancyType type = RedundancyType.WEAK;
	private Cause left;
	private Cause right;

	public Redundancy(Cause left, Cause right) {
		this.left = left;
		this.right = right;
	}

	public Redundancy(RedundancyType type, Cause left, Cause right) {
		this.type = type;
		this.left = left;
		this.right = right;
	}

	public CauseType getCauseType() {
		return CauseType.REDUNDANCY;
	}

	public int getId() {
		return id;
	}

	public Cause getLeft() {
		return left;
	}

	public void setLeft(Cause left) {
		this.left = left;
	}

	public Cause getRight() {
		return right;
	}

	public void setRight(Cause right) {
		this.right = right;
	}

	public RedundancyType getType() {
		return type;
	}

	public void setType(RedundancyType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Redundacy between: (" + left + ") and (" + right + ").";
	}
}
