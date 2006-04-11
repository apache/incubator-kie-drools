package org.drools.leaps.conflict;

/*
 * Copyright 2006 Alexander Bagerman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
            LoadOrderConflictResolver.getInstance() };

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
