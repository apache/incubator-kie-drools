package org.drools.verifier.report.components;

/**
 * Partial redundancy between left and right. Redundancy stores the connection
 * between left and right.
 * 
 * @author Toni Rikkola
 */
public class PartialRedundancy {

	private final Cause left;
	private final Cause right;
	private final Redundancy redundancy;

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

	public Redundancy getRedundancy() {
		return redundancy;
	}

	public Cause getRight() {
		return right;
	}

}
