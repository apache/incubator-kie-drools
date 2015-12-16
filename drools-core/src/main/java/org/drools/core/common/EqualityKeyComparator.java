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

package org.drools.core.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.AbstractHashTable.AbstractObjectComparator;

public class EqualityKeyComparator
    extends
    AbstractObjectComparator {
    private static final long            serialVersionUID = 510l;

    private static EqualityKeyComparator instance;

    public static EqualityKeyComparator getInstance() {
        if ( EqualityKeyComparator.instance == null ) {
            EqualityKeyComparator.instance = new EqualityKeyComparator();
        }

        return EqualityKeyComparator.instance;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }
    
    public int hashCodeOf(final Object key) {
        return AbstractHashTable.rehash( key.hashCode() );
    }    

    /**
     * Equality key  reverses the compare, so  that  the  key  controls the  comparison
     */
    public boolean equal(final Object o1,
                         final Object o2) {
        return (o1 == null) ? (o2 == null) : (o1 == o2) || o2.equals( o1 );
    }

    public int compare(final Object o1,
                       final Object o2) {
        return ((Comparable) o1).compareTo( o2 );
    }

    public String toString() {
        return "[EqualityKeyComparator]";
    }
}
