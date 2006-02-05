package org.drools.leaps.conflict;

import java.util.Comparator;

import org.drools.leaps.ConflictResolver;
/**
 * A blueprint for conflict resolers
 * 
 * @author Alexander Bagerman
 * 
 */

public abstract class AbstractConflictResolver implements ConflictResolver {
	// need for comparator
	public static int compare(int i1, int i2) {
		return i1 - i2;
	}

	public static int compare(long l1, long l2) {
		return (int) (l1 - l2);
	}

	public abstract Comparator getFactConflictResolver();

	public abstract Comparator getRuleConflictResolver();
}
