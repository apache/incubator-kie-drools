package org.drools.verifier.report.components;

/**
 * Presents a redundancy between two Causes. The link between them can be WEAK
 * or STRONG.
 * 
 * WEAK redundancy is for example two VerifierRules, but not theyr's rule
 * possibilities. STRONG redundancy includes possibilities.
 * 
 * @author Toni Rikkola
 */
public class Redundancy extends Subsumption implements Cause {
	// By default the redundancy is weak.
	private final RedundancyType type;

	public Redundancy(Cause left, Cause right) {
		super(left, right);
		type = RedundancyType.WEAK;
	}

	public Redundancy(RedundancyType type, Cause left, Cause right) {
		super(left, right);
		this.type = type;
	}

	public CauseType getCauseType() {
		return CauseType.REDUNDANCY;
	}

	public RedundancyType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Redundancy between: (" + getLeft() + ") and (" + getRight()
				+ ").";
	}
}
