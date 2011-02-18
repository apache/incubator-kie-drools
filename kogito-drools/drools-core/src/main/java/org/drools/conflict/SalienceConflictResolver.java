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

import org.drools.spi.Activation;
import org.drools.spi.ConflictResolver;

/**
 * <code>ConflictResolver</code> that uses the salience of rules to resolve
 * conflict.
 * 
 * @see #getInstance
 * @see org.drools.rule.Rule#getSalience
 * 
 *
 * @version $Id: SalienceConflictResolver.java,v 1.3 2004/06/25 02:46:39
 *          mproctor Exp $
 */
public class SalienceConflictResolver extends AbstractConflictResolver {
    // ----------------------------------------------------------------------
    // Class members
    // ----------------------------------------------------------------------

    private static final long                     serialVersionUID = 510l;
    /** Singleton instance. */
    private static final SalienceConflictResolver INSTANCE         = new SalienceConflictResolver();

    // ----------------------------------------------------------------------
    // Class methods
    // ----------------------------------------------------------------------

    /**
     * Retrieve the singleton instance.
     * 
     * @return The singleton instance.
     */
    public static ConflictResolver getInstance() {
        return SalienceConflictResolver.INSTANCE;
    }

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public SalienceConflictResolver() {
        // intentionally left blank
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @see ConflictResolver
     */
    public int compare(final Activation lhs,
                       final Activation rhs) {
        final int s1 = lhs.getSalience();
        final int s2 = rhs.getSalience();

        if ( s1 > s2 ) {
            return -1;
        } else if ( s1 < s2 ) {
            return 1;
        } else {
            return 0;
        }
    }
}
