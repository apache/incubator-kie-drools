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

package org.drools.reteoo;

import java.util.concurrent.atomic.AtomicReference;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.index.RightTupleList;

public class ConcurrentRightTuple extends RightTuple {
    private RightTupleList     memory;

    private AtomicReference<Entry>              previous;
    private AtomicReference<Entry>              next;

    public ConcurrentRightTuple() {

    }

    public ConcurrentRightTuple(InternalFactHandle handle,
                                RightTupleSink sink) {
        this.handle = handle;
        this.sink = sink;
        
        this.previous = new AtomicReference<Entry>();
        this.next = new AtomicReference<Entry>();
        
        handle.addFirstRightTuple( this );
    }

    public RightTupleList getMemory() {
        return memory;
    }

    public void setMemory(RightTupleList memory) {
        this.memory = memory;
    }

    public Entry getPrevious() {
        return previous.get();
    }

    public void setPrevious(Entry previous) {
        this.previous.set( previous );
    }
    
    public Entry getNext() {
        return next.get();
    }

    public void setNext(Entry next) {
        this.next.set( next );
    }



    public int hashCode() {
        return this.handle.hashCode();
    }

    public String toString() {
        return this.handle.toString() + "\n";
    }

    public boolean equals(ConcurrentRightTuple other) {
        // we know the object is never null and always of the  type ReteTuple
        if ( other == this ) {
            return true;
        }

        // A ReteTuple is  only the same if it has the same hashCode, factId and parent
        if ( (other == null) || (hashCode() != other.hashCode()) ) {
            return false;
        }

        return this.handle == other.handle;
    }

    public boolean equals(Object object) {
        return equals( (ConcurrentRightTuple) object );
    }
}
