/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.conflict;

import org.drools.core.spi.Activation;
import org.drools.core.spi.ConflictResolver;

/**
 * <code>ConflictResolver</code> that uses the loadOrder of rules to resolve
 * conflict.
 * 
 * @see #getInstance
 * @see org.kie.rule.Rule#getLoadOrder
 * 
 *
 * @version $Id: LoadOrderConflictResolver.java,v 1.1 2004/06/25 01:55:16
 *          mproctor Exp $
 */
public class SequentialConflictResolver extends AbstractConflictResolver {
    // ----------------------------------------------------------------------
    // Class members
    // ----------------------------------------------------------------------

    private static final long                       serialVersionUID = 510l;
    /** Singleton instance. */
    private static final SequentialConflictResolver INSTANCE         = new SequentialConflictResolver();

    // ----------------------------------------------------------------------
    // Class methods
    // ----------------------------------------------------------------------

    /**
     * Retrieve the singleton instance.
     *
     * @return The singleton instance.
     */
    public static ConflictResolver getInstance() {
        return SequentialConflictResolver.INSTANCE;
    }

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public SequentialConflictResolver() {
        // intentionally left blank
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public final int compare(final Activation existing,
                             final Activation adding) {
        final int s1 = existing.getSalience();
        final int s2 = adding.getSalience();

        if ( s1 > s2 ) {
            return 1;
        } else if ( s1 < s2 ) {
            return -1;
        }

        return (int) (existing.getRule().getLoadOrder() - adding.getRule().getLoadOrder());
    }
}
