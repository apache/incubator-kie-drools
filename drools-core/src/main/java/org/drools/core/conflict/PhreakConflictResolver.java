/*
 * Copyright 2010 JBoss Inc
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class PhreakConflictResolver
        implements
        ConflictResolver, Externalizable {
    private static final long                   serialVersionUID = 510l;
    public static final  PhreakConflictResolver INSTANCE         = new PhreakConflictResolver();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public static ConflictResolver getInstance() {
        return PhreakConflictResolver.INSTANCE;
    }

    /**                                                                                   11
     * @see org.drools.core.spi.ConflictResolver
     */
    public final int compare(final Object existing,
                             final Object adding) {
        return compare((Activation) existing,
                       (Activation) adding);
    }

    public final int compare(final Activation existing,
                             final Activation adding) {
        return doCompare( existing, adding );
    }

    public final static int doCompare(final Activation existing,
                             final Activation adding) {
        final int s1 = existing.getSalience();
        final int s2 = adding.getSalience();

        // highest goes first
        if (s1 > s2) {
            return 1;
        } else if (s1 < s2) {
            return -1;
        }

        final int l1 = existing.getRule().getLoadOrder();
        final int l2 = adding.getRule().getLoadOrder();

        // lowest goes first
        if (l1 < l2) {
            return 1;
        } else if (l1 > l2) {
            return -1;
        } else {
            return 0;
        }
    }

}
