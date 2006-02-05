package org.drools.leaps;

import java.util.Comparator;

/**
 * Leaps specific conflict resolver provides for separate fact and rule based
 * conflict resolution
 * 
 * @author Alexander Bagerman
 * 
 */
public interface ConflictResolver {
	public Comparator getFactConflictResolver();

	public Comparator getRuleConflictResolver();
}
