package org.drools.leaps;

import org.drools.spi.ClassObjectType;

/**
 * class container for rules used in the system. Handle is created for each
 * leaps rule, dominant position (column/ce position), dominant position type
 * (class at the column/ce position) or indicator if handle is for asserted or
 * retracted tuple combination
 * 
 * @author Alexander Bagerman
 */
public class RuleHandle extends Handle {
	// ce position for which handle is created
	private final int dominantPosition;

	private final ClassObjectType dominantPositionType;

	private final boolean isDominantFactAsserted;

	public RuleHandle(long id, LeapsRule rule, int dominantPosition,
			ClassObjectType dominantPositionType, boolean isDominantFactAsserted) {
		super(id, rule);
		this.dominantPosition = dominantPosition;
		this.dominantPositionType = dominantPositionType;
		this.isDominantFactAsserted = isDominantFactAsserted;
	}

	/**
	 * @return leaps wrapped rule
	 */
	public LeapsRule getLeapsRule() {
		return (LeapsRule) this.getObject();
	}

	/**
	 * @return base column / ce position
	 */
	public int getDominantPosition() {
		return dominantPosition;
	}

	/**
	 * @return base column / ce position type
	 */
	public ClassObjectType getDominantPositionType() {
		return dominantPositionType;
	}

	/**
	 * @return type of fact to be at position (asserted vs. retracted)
	 */
	public boolean isDominantFactAsserted() {
		return isDominantFactAsserted;
	}

	/**
	 * @see org.drools.rule.Rule
	 */
	public int getRuleComplexity() {
		return this.getLeapsRule().getRule().getDeclarations().size();
	}

	/**
	 * @see org.drools.rule.Rule
	 */
	public int getSalience() {
		return this.getLeapsRule().getRule().getSalience();
	}

	/**
	 * @see java.lang.Object
	 */
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof RuleHandle))
			return false;
		return (this.getId() == ((RuleHandle) that).getId()
				&& this.getLeapsRule().equals(
						((RuleHandle) that).getLeapsRule())
				&& (this.getDominantPosition() == ((RuleHandle) that)
						.getDominantPosition()) && (this
				.getDominantPositionType() == ((RuleHandle) that)
				.getDominantPositionType()));
	}

	/**
	 * @see java.lang.Object
	 */
	public String toString() {
		return "R-" + this.getId() + " \"" + this.getLeapsRule().toString()
				+ "\" [pos - " + this.dominantPosition + "]";
	}
}
