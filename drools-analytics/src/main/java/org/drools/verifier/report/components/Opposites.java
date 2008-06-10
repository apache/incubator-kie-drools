package org.drools.verifier.report.components;

/**
 * 
 * Two causes are opposites.
 * <p>
 * For example: Restrictions (a > b) and (a <= b)
 * 
 * @author Toni Rikkola
 */
public class Opposites extends Incompatibility implements Cause {

	public Opposites(Cause left, Cause right) {
		super(left, right);
	}

	public CauseType getCauseType() {
		return CauseType.OPPOSITE;
	}

	@Override
	public String toString() {
		return "Opposites: (" + getLeft() + ") and (" + getRight() + ").";
	}
}
