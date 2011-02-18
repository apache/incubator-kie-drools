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

import org.drools.common.InternalFactHandle;
import org.drools.spi.Activation;
import org.drools.spi.ConflictResolver;

/**
 * <code>ConflictResolver</code> that uses the mostRecentFactTimeStamp of
 * rules to resolve conflict.
 * 
 * @see #getInstance
 * @see org.drools.spi.Tuple#getMostRecentFactTimeStamp
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public class RecencyConflictResolver extends AbstractConflictResolver {
    // ----------------------------------------------------------------------
    // Class members
    // ----------------------------------------------------------------------

    /**
     * 
     */
    private static final long                    serialVersionUID = 510l;
    /** Singleton instance. */
    private static final RecencyConflictResolver INSTANCE         = new RecencyConflictResolver();

    // ----------------------------------------------------------------------
    // Class methods
    // ----------------------------------------------------------------------

    /**
     * Retrieve the singleton instance.
     * 
     * @return The singleton instance.
     */
    public static ConflictResolver getInstance() {
        return RecencyConflictResolver.INSTANCE;
    }

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public RecencyConflictResolver() {
        // intentionally left blank
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @see ConflictResolver
     */
    public int compare(final Activation lhs,
                       final Activation rhs) {
        final InternalFactHandle[] lFacts = lhs.getTuple().getFactHandles();
        final InternalFactHandle[] rFacts = rhs.getTuple().getFactHandles();

        InternalFactHandle leftMostRecent = getMostRecentFact( lFacts );
        InternalFactHandle rightMostRecent = getMostRecentFact( rFacts );

        final int lastIndex = (lFacts.length < rFacts.length) ? lFacts.length : rFacts.length;

        if ( leftMostRecent.getRecency() == rightMostRecent.getRecency() && lastIndex > 1 ) {

            for ( int i = 0; i < lastIndex; i++ ) {
                leftMostRecent = getMostRecentFact( lFacts,
                                                    leftMostRecent );
                rightMostRecent = getMostRecentFact( rFacts,
                                                     rightMostRecent );
                if ( leftMostRecent == null || rightMostRecent == null ) {
                    if ( leftMostRecent == null && rightMostRecent != null ) {
                        return (int) rightMostRecent.getRecency();
                    }
                } else if ( leftMostRecent.getRecency() != rightMostRecent.getRecency() ) {
                    return (int) (rightMostRecent.getRecency() - leftMostRecent.getRecency());
                }
            }
        } else {
            return (int) (rightMostRecent.getRecency() - leftMostRecent.getRecency());
        }

        return rFacts.length - lFacts.length;
    }

    private InternalFactHandle getMostRecentFact(final InternalFactHandle[] handles) {
        InternalFactHandle mostRecent = handles[0];
        for ( int i = 1; i < handles.length; i++ ) {
            final InternalFactHandle eachHandle = handles[i];

            if ( eachHandle.getRecency() > mostRecent.getRecency() ) {
                mostRecent = eachHandle;
            }
        }
        return mostRecent;
    }

    private InternalFactHandle getMostRecentFact(final InternalFactHandle[] handles,
                                                 final InternalFactHandle handle) {
        InternalFactHandle mostRecent = null;

        for ( int i = 0; i < handles.length; i++ ) {
            final InternalFactHandle eachHandle = handles[i];

            if ( mostRecent == null && eachHandle.getRecency() < handle.getRecency() ) {
                mostRecent = eachHandle;
            }

            if ( mostRecent != null && eachHandle.getRecency() > mostRecent.getRecency() && eachHandle.getRecency() < handle.getRecency() ) {
                mostRecent = eachHandle;
            }
        }
        return mostRecent;
    }

}
