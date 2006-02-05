package org.drools.leaps.conflict;

import java.util.Comparator;

import org.drools.leaps.Handle;

/**
 * <code>LoadOrderConflictResolver</code> that uses the load order of rules to
 * resolve conflict.
 * 
 * @author Alexander Bagerman
 * 
 * @see org.drools.leaps.conflict.AbstractConflictResolver
 * @see org.drools.leaps.ConflictResolver
 * @see org.drools.spi.ConflictResolver
 */
public class LoadOrderConflictResolver implements Comparator {

	/** Singleton instance. */
	private static final LoadOrderConflictResolver INSTANCE = new LoadOrderConflictResolver();

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
	private LoadOrderConflictResolver() {
		// intentionally left blank
	}

	/**
	 * @see LeapsRuleConflictResolver
	 */
	public int compare(Object o1, Object o2) {
		return (-1)
				* AbstractConflictResolver.compare(((Handle) o1).getRecency(),
						((Handle) o2).getRecency());
	};
}
