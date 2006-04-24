package org.drools.conflict;
/*
 * Copyright 2005 JBoss Inc
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





import org.drools.spi.ConflictResolver;

/**
 * Strategy for resolving conflicts amongst multiple rules.
 * 
 * <p>
 * Since a fact or set of facts may activate multiple rules, a
 * <code>ConflictResolutionStrategy</code> is used to provide priority
 * ordering of conflicting rules.
 * </p>
 * 
 * @see org.drools.spi.Activation
 * @see org.drools.spi.Tuple
 * @see org.drools.rule.Rule
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public class DefaultConflictResolver extends CompositeConflictResolver {
    // ----------------------------------------------------------------------
    // Class members
    // ----------------------------------------------------------------------

     private static final ConflictResolver[] CONFLICT_RESOLVERS = new ConflictResolver[]{ DepthConflictResolver.getInstance() };

    /** Singleton instance. */
    private static final DefaultConflictResolver INSTANCE           = new DefaultConflictResolver();

    // ----------------------------------------------------------------------
    // Class methods
    // ----------------------------------------------------------------------

    /**
     * Retrieve the singleton instance.
     * 
     * @return The singleton instance.
     */
    public static ConflictResolver getInstance() {
        return DefaultConflictResolver.INSTANCE;
    }

    /**
     * Setup a default ConflictResolver configuration
     */
    public DefaultConflictResolver() {
        super( DefaultConflictResolver.CONFLICT_RESOLVERS );
    }
}