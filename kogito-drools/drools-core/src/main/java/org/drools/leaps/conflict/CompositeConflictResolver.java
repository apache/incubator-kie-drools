package org.drools.leaps.conflict;

import java.util.Comparator;

/**
 * Strategy for resolving conflicts amongst multiple rules.
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

public class CompositeConflictResolver extends AbstractConflictResolver {
	private final Comparator[] factResolvers;

	private final Comparator[] ruleResolvers;

	public CompositeConflictResolver(Comparator[] factResolvers,
			Comparator[] ruleResolvers) {
		this.factResolvers = factResolvers;
		this.ruleResolvers = ruleResolvers;
	}

	public final Comparator getFactConflictResolver() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				int ret = 0;
				if (o1 != o2) {
					for (int i = 0; ret == 0 && i < factResolvers.length; ++i) {
						ret = factResolvers[i].compare(o1, o2);
					}
				}
				return ret;
			}
		};
	}

	public final Comparator getRuleConflictResolver() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				int ret = 0;
				if (o1 != o2) {
					for (int i = 0; ret == 0 && i < ruleResolvers.length; ++i) {
						ret = ruleResolvers[i].compare(o1, o2);
					}
				}
				return ret;
			}
		};
	}
}
