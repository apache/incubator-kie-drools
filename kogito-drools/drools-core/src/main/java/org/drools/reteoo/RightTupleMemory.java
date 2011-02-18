/**
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

package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;

public interface RightTupleMemory {
    public RightTuple getFirst(LeftTuple leftTuple, InternalFactHandle factHandle);
    
    public RightTuple getFirst(RightTuple rightTuple);

    public void add(RightTuple rightTuple);

    public void remove(RightTuple rightTuple);

    public boolean contains(RightTuple rightTuple);
    
    public Iterator iterator();
    
    public FastIterator fastIterator();
    
    public FastIterator fullFastIterator();

    public boolean isIndexed();

    public Entry[] toArray();

    public int size();
}
