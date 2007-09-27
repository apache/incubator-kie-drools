package org.drools.analytics.result;

/**
 * Presents a redundancy between two Causes. The link between them can be WEAK
 * or STRONG.
 * 
 * WEAK redundancy is for example two AnalyticsRules, but not theys rule
 * possibilities. STRONG redundancy includes possibilities.
 * 
 * @author Toni Rikkola
 */
public class Redundancy {

	public enum RedundancyType {
		WEAK, STRONG
	}

	private RedundancyType type = RedundancyType.WEAK; // By default the redundancy is weak.
	private Cause.CauseType causeType; // left and right Cause are of the same type.
	private Cause left;
	private Cause right;

	public Redundancy(Cause left, Cause right) {
		this.causeType=left.getCauseType();
		this.left = left;
		this.right = right;
	}

	public Redundancy(RedundancyType type, Cause left, Cause right) {
		this.causeType=left.getCauseType();
		this.type = type;
		this.left = left;
		this.right = right;
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

	public Cause.CauseType getCauseType() {
		return causeType;
	}
}
