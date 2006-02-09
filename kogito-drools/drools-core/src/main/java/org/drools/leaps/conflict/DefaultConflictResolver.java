package org.drools.leaps.conflict;

import java.util.Comparator;

import org.drools.leaps.ConflictResolver;

/**
 * Default strategy for resolving conflicts amongst multiple rules.
 *  
 * <p>
 * Since a fact or set of facts may activate multiple rules, a
 * <code>ConflictResolutionStrategy</code> is used to provide priority
 * ordering of conflicting rules.
 * </p>
 * 
 * @author Alexander Bagerman
 * 
 * @see org.drools.leaps.conflict.AbstractConflictResolver
 * @see org.drools.leaps.ConflictResolver
 * @see org.drools.spi.ConflictResolver
 */
public class DefaultConflictResolver extends CompositeConflictResolver {

	private static final Comparator[] FACT_CONFLICT_RESOLVERS = new Comparator[] { LoadOrderConflictResolver
			.getInstance() };

	private static final Comparator[] RULE_CONFLICT_RESOLVERS = new Comparator[] {
			RuleSalienceConflictResolver.getInstance(),
			RuleComplexityConflictResolver.getInstance(),
			RuleLoadOrderConflictResolver.getInstance() };

	/** Singleton instance. */
	private static final DefaultConflictResolver INSTANCE = new DefaultConflictResolver();

	/**
	 * Retrieve the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static ConflictResolver getInstance() {
		return INSTANCE;
	}

	/**
	 * Setup a default ConflictResolver configuration
	 */
	public DefaultConflictResolver() {
		super(FACT_CONFLICT_RESOLVERS, RULE_CONFLICT_RESOLVERS);
	}
}
