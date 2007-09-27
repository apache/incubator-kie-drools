package org.drools.analytics.result;

/**
 * 
 * @author Toni Rikkola
 */
public class Subsumption {

	private Cause left;
	private Cause right;

	public Subsumption(Cause left, Cause right) {
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
}
