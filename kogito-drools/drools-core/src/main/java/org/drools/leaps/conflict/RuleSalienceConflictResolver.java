package org.drools.leaps.conflict;

import java.util.Comparator;

import org.drools.leaps.RuleHandle;

/**
 * <code>RuleConflictResolver</code> that uses the salience of rules to resolve
 * conflict.
 *
 * @author Alexander Bagerman
 * 
 * @see org.drools.leaps.conflict.AbstractConflictResolver
 * @see org.drools.leaps.ConflictResolver
 * @see org.drools.spi.ConflictResolver
 */
public class RuleSalienceConflictResolver implements Comparator {
	/** Singleton instance. */
	private static final RuleSalienceConflictResolver INSTANCE = new RuleSalienceConflictResolver();

	/**
	 * Retrieve the singleton instance.
	 *
	 * @return The singleton instance.
	 */
	public static Comparator getInstance() {
		return INSTANCE;
	}

	/**
	 * Construct.
	 */
	private RuleSalienceConflictResolver() {
		// intentionally left blank
	}

	/**
	 * @see LeapsRuleConflictResolver
	 */
	public int compare(Object o1, Object o2) {
		return (-1)
				* AbstractConflictResolver.compare(((RuleHandle) o1)
						.getSalience(), ((RuleHandle) o2).getSalience());
	};
}
