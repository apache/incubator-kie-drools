package org.drools.analytics.result;

/**
 * Partial redundancy between left and right. Eedundancy stores the connection
 * between left and right.
 * 
 * @author Toni Rikkola
 */
public class PartialRedundancy {

	private Cause left;
	private Cause right;
	private Redundancy redundancy;

	/**
	 * 
	 * @param left
	 *            Left side parent.
	 * @param right
	 *            Right side parent.
	 * @param redundancy
	 *            Connection between left and right.
	 */
	public PartialRedundancy(Cause left, Cause right, Redundancy redundancy) {
		this.left = left;
		this.right = right;
		this.redundancy = redundancy;
	}

	public Cause getLeft() {
		return left;
	}

	public void setLeft(Cause left) {
		this.left = left;
	}

	public Redundancy getRedundancy() {
		return redundancy;
	}

	public void setRedundancy(Redundancy redundancy) {
		this.redundancy = redundancy;
	}

	public Cause getRight() {
		return right;
	}

	public void setRight(Cause right) {
		this.right = right;
	}
}
