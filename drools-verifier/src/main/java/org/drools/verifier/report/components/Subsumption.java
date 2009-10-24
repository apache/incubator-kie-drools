package org.drools.verifier.report.components;

/**
 * 
 * @author Toni Rikkola
 */
public class Subsumption implements Cause {

	private static int index = 0;

	private final String guid = String.valueOf( index++ );

	private final Cause left;
	private final Cause right;

	public Subsumption(Cause left, Cause right) {
		this.left = left;
		this.right = right;
	}

	public String getGuid() {
		return guid;
	}

	public CauseType getCauseType() {
		return CauseType.SUBSUMPTION;
	}

	public Cause getLeft() {
		return left;
	}

	public Cause getRight() {
		return right;
	}

	@Override
	public String toString() {
		return "Subsumption between: (" + getLeft() + ") and (" + getRight()
				+ ").";
	}
}
