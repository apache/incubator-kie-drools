package org.drools.leaps.conflict;

import java.util.Comparator;

import org.drools.leaps.RuleHandle;

/**
 * <code>RuleConflictResolver</code> that uses the load order of rules to
 * resolve conflict.
 * 
 * @author Alexander Bagerman
 * 
 * @see org.drools.leaps.conflict.AbstractConflictResolver
 * @see org.drools.leaps.ConflictResolver
 * @see org.drools.spi.ConflictResolver
 */
public class RuleLoadOrderConflictResolver implements Comparator {

	/** Singleton instance. */
	private static final RuleLoadOrderConflictResolver INSTANCE = new RuleLoadOrderConflictResolver();

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
	private RuleLoadOrderConflictResolver() {
		// intentionally left blank
	}

	/**
	 * @see LeapsRuleLoadOrderResolver
	 */
	public int compare(Object o1, Object o2) {
		int ret = LoadOrderConflictResolver.getInstance().compare(o1, o2);
		if (ret == 0) {
			ret = (-1)
					* AbstractConflictResolver.compare(((RuleHandle) o1)
							.getDominantPosition(), ((RuleHandle) o2)
							.getDominantPosition());
		}
		return ret;
	};
}
