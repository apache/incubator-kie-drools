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

package org.drools.common;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.FactHandle;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.AbstractHashTable.AbstractObjectComparator;

public class EqualityAssertMapComparator
    extends
    AbstractObjectComparator {
    private static final long serialVersionUID = 510l;

    public EqualityAssertMapComparator() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public int hashCodeOf(final Object obj) {
        if ( obj instanceof FactHandle ) {
            return AbstractHashTable.rehash( ((InternalFactHandle) obj).getObjectHashCode() );
        }
        return AbstractHashTable.rehash( obj.hashCode() );
    }

    /**
     * Special comparator  that  allows FactHandles to  be  keys, but always  checks
     * equals with the  identity of the  objects involved
     */
    public boolean equal(final Object o1,
                         Object o2) {
        if ( o1 instanceof FactHandle ) {
            return o1 == o2;
        }

        final InternalFactHandle handle = ((InternalFactHandle) o2);

        o2 = handle.getObject();

        return o1 == o2 || o1.equals( o2 );
    }

    public int compare(final Object o1,
                       final Object o2) {
        return ((Comparable) o1).compareTo( o2 );
    }

    public String toString() {
        return "equality";
    }
}
