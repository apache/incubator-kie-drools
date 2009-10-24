package org.drools.verifier.report.components;

/**
 * 
 * Two causes are incompatible.
 * <p>
 * For example: Restrictions (a > b) and (a == b)
 * 
 * @author Toni Rikkola
 */
public class Incompatibility implements Cause {

	private static int index = 0;

	private final String guid = String.valueOf( index++ );

	private final Cause left;
	private final Cause right;

	public Incompatibility(Cause left, Cause right) {
		this.left = left;
		this.right = right;
	}

	public String getGuid() {
		return guid;
	}

	public CauseType getCauseType() {
		return CauseType.INCOMPATIBLE;
	}

	public Cause getLeft() {
		return left;
	}

	public Cause getRight() {
		return right;
	}

	@Override
	public String toString() {
		return "(" + getLeft() + ") and (" + getRight() + ") are incompatible.";
	}
}
