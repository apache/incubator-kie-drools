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

import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;

public interface LeftTupleMemory {
    Iterator iterator();

    LeftTuple getFirst(RightTuple rightTuple);
    
    void removeAdd(LeftTuple tuple);

    void add(LeftTuple tuple);

    void remove(LeftTuple leftTuple);

    boolean contains(LeftTuple leftTuple);

    boolean isIndexed();

    int size();

    Entry[] toArray();

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
    FastIterator fullFastIterator(LeftTuple leftTuple);

    void clear();
}
