package org.drools.verifier.report.components;

/**
 * 
 * Two causes are opposites.
 * 
 * @author Toni Rikkola
 */
public class Incompatibility implements Cause {

	private static int index = 0;

	private int id = index++;

	private Cause left;
	private Cause right;

	public Incompatibility(Cause left, Cause right) {
		this.left = left;
		this.right = right;
	}

	public int getId() {
		return id;
	}

	public CauseType getCauseType() {
		return CauseType.OPPOSITES;
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

	@Override
	public String toString() {
		return "(" + getLeft() + ") and (" + getRight()
				+ ") are opposites.";
	}
}
