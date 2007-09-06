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

	public enum Type {
		WEAK, STRONG
	}

	private Type type = Type.WEAK; // By default the redundancy is weak.
	private Cause left;
	private Cause right;

	public Redundancy(Cause left, Cause right) {
		this.left = left;
		this.right = right;
	}

	public Redundancy(Type type, Cause left, Cause right) {
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Redundacy between: (" + left + ") and (" + right + ").";
	}
}
