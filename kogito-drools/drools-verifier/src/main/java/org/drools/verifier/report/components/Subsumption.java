package org.drools.verifier.report.components;

/**
 * 
 * @author Toni Rikkola
 */
public class Subsumption implements Cause {

	private static int index = 0;

	private int id = index++;

	private Cause left;
	private Cause right;

	public Subsumption(Cause left, Cause right) {
		this.left = left;
		this.right = right;
	}

	public int getId() {
		return id;
	}

	public CauseType getCauseType() {
		return CauseType.SUBSUMPTION;
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
		return "Subsumption between: (" + getLeft() + ") and (" + getRight()
				+ ").";
	}
}
