package org.drools.leaps;

import java.io.Serializable;
import java.util.Comparator;

import org.drools.leaps.util.Table;

/**
 * Implementation of a container to store data elements used throughout the
 * leaps. Stores rule handles
 * 
 * @author Alexander Bagerman
 *
 */
class RuleTable extends Table implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RuleTable(Comparator ruleConflictResolver) {
		super(ruleConflictResolver);
	}
}
