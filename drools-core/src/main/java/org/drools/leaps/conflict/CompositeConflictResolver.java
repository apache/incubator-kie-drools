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

class CompositeConflictResolver extends AbstractConflictResolver {
    final Comparator[] factResolvers;

    final Comparator[] ruleResolvers;

    public CompositeConflictResolver(Comparator[] factResolvers,
                                     Comparator[] ruleResolvers) {
        this.factResolvers = factResolvers;
        this.ruleResolvers = ruleResolvers;
    }

    public final Comparator getFactConflictResolver() {
        return new Comparator() {
            public int compare(Object o1,
                               Object o2) {
                int ret = 0;
                if ( o1 != o2 ) {
                    for ( int i = 0, length = CompositeConflictResolver.this.factResolvers.length; ret == 0 && i < length; ++i ) {
                        ret = CompositeConflictResolver.this.factResolvers[i].compare( o1,
                                                                                       o2 );
                    }
                }
                return ret;
            }
        };
    }

    public final Comparator getRuleConflictResolver() {
        return new Comparator() {
            public int compare(Object o1,
                               Object o2) {
                int ret = 0;
                if ( o1 != o2 ) {
                    for ( int i = 0, length = CompositeConflictResolver.this.ruleResolvers.length; ret == 0 && i < length; ++i ) {
                        ret = CompositeConflictResolver.this.ruleResolvers[i].compare( o1,
                                                                                       o2 );
                    }
                }
                return ret;
            }
        };
    }
}
