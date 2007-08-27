package org.drools.analytics.result;

/**
 * 
 * @author Toni Rikkola
 */
public class Redundancy {

	private Cause left;
	private Cause right;

	public Redundancy(Cause left, Cause right) {
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

	@Override
	public String toString() {
		return "Redundacy between: (" + left + ") and (" + right + ").";
	}
}
