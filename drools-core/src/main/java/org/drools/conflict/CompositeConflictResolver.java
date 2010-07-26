/**
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

package org.drools.conflict;

import org.drools.spi.Activation;
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
 * @see Activation
 * @see org.drools.spi.Tuple
 * @see org.drools.rule.Rule
 * 
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public class CompositeConflictResolver extends AbstractConflictResolver {
    /**
     * 
     */
    private static final long        serialVersionUID = 400L;
    private final ConflictResolver[] components;

    public CompositeConflictResolver(final ConflictResolver[] components) {
        this.components = components;
    }

    /**
     * @see AbstractConflictResolver
     */
    public final int compare(final Activation lhs,
                             final Activation rhs) {
        int result = 0;

        for ( int i = 0; result == 0 && i < this.components.length; ++i ) {
            result = this.components[i].compare( lhs,
                                                 rhs );
        }

        return result;
    }
}
