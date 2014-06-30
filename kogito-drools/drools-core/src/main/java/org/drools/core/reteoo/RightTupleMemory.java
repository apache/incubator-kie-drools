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

package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;

public interface RightTupleMemory {

    enum IndexType {
        NONE, EQUAL, COMPARISON, RANGE;

        public boolean isComparison() {
            return this == COMPARISON || this == RANGE;
        }
    }

    /**
     * The FactHandle is always the context fact and is necessary when the object being modified is in the both left and right
     * node memories. This is because the memory on the opposite side would not have yet memory.removeAdd the fact, so it
     * could potentially be in the wrong bucket. So the bucket matches check always checks to ignore the first facthandle if it's
     * the same as the context fact.
     * 
     * @param leftTuple
     * @param factHandle
     * @return
     */
    RightTuple getFirst(LeftTuple leftTuple, InternalFactHandle factHandle, FastIterator rightTupleIterator);
    
    void removeAdd(RightTuple rightTuple);

    void add(RightTuple rightTuple);

    void remove(RightTuple rightTuple);

    boolean contains(RightTuple rightTuple);
    
    Iterator iterator();
    
    FastIterator fastIterator();
    
    /**
     * Iterates the entire data structure, regardless of whether TupleMemory is hashed or not.
     * @return
     */
    FastIterator fullFastIterator();
    
    /**
     * Iterator that resumes from the current RightTuple, regardless of whether the TupleMemory is hashed or not 
     * @param rightTuple
     * @return
     */
    FastIterator fullFastIterator(RightTuple rightTuple);

    boolean isIndexed();

    Entry[] toArray();

    int size();

    IndexType getIndexType();

    void clear();
}
