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

package org.drools.conflict;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.spi.Activation;
import org.drools.spi.ConflictResolver;

public class DepthConflictResolver
    implements
    ConflictResolver, Externalizable {
    private static final long                 serialVersionUID = 510l;
    public static final DepthConflictResolver INSTANCE         = new DepthConflictResolver();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public static ConflictResolver getInstance() {
        return DepthConflictResolver.INSTANCE;
    }
    
    /**
     * @see ConflictResolver
     */
    public final int compare(final Object existing,
                             final Object adding) {
        return compare( (Activation) existing,
                        (Activation) adding );
    }

    public final int compare(final Activation existing,
                             final Activation adding) {
        final int s1 = existing.getSalience();
        final int s2 = adding.getSalience();
        
        if ( s1 != s2 ) {
            return s1 - s2;
        }


        // we know that no two activations will have the same number
        return (int) ( existing.getActivationNumber() - adding.getActivationNumber() );
    }

}
