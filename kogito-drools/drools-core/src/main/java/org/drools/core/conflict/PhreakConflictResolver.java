/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

    public final int compare(final Activation existing,
                             final Activation adding) {
        return doCompare( existing, adding );
    }

    public final static int doCompare(final Activation existing,
                                      final Activation adding) {
        if (existing == adding) {
            return 0;
        }

        final int s1 = existing.getSalience();
        final int s2 = adding.getSalience();

        return s1 != s2 ?
               ( s1 > s2 ? 1 : -1 ) : // highest salience goes first (cannot do s1-s2 due to overflow)
               adding.getRule().getLoadOrder() - existing.getRule().getLoadOrder(); // lowest order goes first
    }
}
