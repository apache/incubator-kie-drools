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

package org.drools.conflict;

import org.drools.WorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.Activation;
import org.drools.spi.ConflictResolver;

/**
 * A conflict resolver that compares the total recency of a tuple when 
 * determining firing order.
 * 
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 * 
 * @version $Id$
 */
public class TotalRecencyConflictResolver extends AbstractConflictResolver {
    // ----------------------------------------------------------------------
    // Class members
    // ----------------------------------------------------------------------

    /** Singleton instance. */
    private static final TotalRecencyConflictResolver INSTANCE = new TotalRecencyConflictResolver();

    // ----------------------------------------------------------------------
    // Class methods
    // ----------------------------------------------------------------------

    /**
     * Retrieve the singleton instance.
     * 
     * @return The singleton instance.
     */
    public static ConflictResolver getInstance() {
        return TotalRecencyConflictResolver.INSTANCE;
    }

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public TotalRecencyConflictResolver() {
        // intentionally left blank
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @see ConflictResolver
     */
    public int compare(Activation lhs,
                       Activation rhs) {
        long leftRecency = 0;
        long rightRecency = 0;
        if(lhs.getTuple() instanceof ReteTuple) {
            leftRecency = ((ReteTuple) lhs.getTuple()).getRecency();
        }
        if(rhs.getTuple() instanceof ReteTuple) {
            rightRecency = ((ReteTuple) rhs.getTuple()).getRecency();
        }
        return (rightRecency > leftRecency) ? 1  :
               (rightRecency < leftRecency) ? -1 :
               0;
    }

}
