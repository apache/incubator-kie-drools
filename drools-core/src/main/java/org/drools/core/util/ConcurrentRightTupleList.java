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

/**
 * 
 */
package org.drools.core.util;

import java.util.concurrent.atomic.AtomicReference;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.AbstractHashTable.Index;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleMemory;

public class ConcurrentRightTupleList
    implements
    RightTupleMemory,
    Entry {
    private static final long          serialVersionUID = 510l;

    public AtomicReference<Entry>      previous;
    public AtomicReference<Entry>      next;

    public AtomicReference<RightTuple> first;
    public AtomicReference<RightTuple> last;

    private final int                  hashCode;
    private final Index                index;

    public ConcurrentRightTupleList() {
        // this is not an index bucket
        this.hashCode = 0;
        this.index = null;

        this.previous = new AtomicReference<Entry>();
        this.next = new AtomicReference<Entry>();

        this.first = new AtomicReference<RightTuple>();
        this.last = new AtomicReference<RightTuple>();
    }

    public ConcurrentRightTupleList(final Index index,
                                    final int hashCode) {
        this.index = index;
        this.hashCode = hashCode;
    }

    public RightTuple getFirst(LeftTuple leftTuple, InternalFactHandle factHandle ) {
        return this.first.get();
    }
    
    public RightTuple getFirst(RightTuple rightTuple) {
        return this.first.get();
    }     

    public RightTuple getLast(LeftTuple leftTuple) {
        return this.last.get();
    }

    public void add(final RightTuple rightTuple) {
        if ( this.last != null ) {
            this.last.get().setNext( rightTuple );
            rightTuple.setPrevious( this.last.get() );
            this.last.set( rightTuple );
        } else {
            this.first.set( rightTuple );
            this.last.set( rightTuple );
        }
    }

    /**
     * We assume that this rightTuple is contained in this hash table
     */
    public void remove(final RightTuple rightTuple) {
        RightTuple previous = (RightTuple) rightTuple.getPrevious();
        RightTuple next = (RightTuple) rightTuple.getNext();

        if ( previous != null && next != null ) {
            // remove from middle
            previous.setNext( next );
            next.setPrevious( previous );
        } else if ( next != null ) {
            // remove from first
            this.first.set( next );
            next.setPrevious( null );
        } else if ( previous != null ) {
            // remove from end
            this.last.set( previous );
            previous.setNext( null );
        } else {
            // remove everything
            this.last = null;
            this.first = null;
        }

        rightTuple.setPrevious( null );
        rightTuple.setNext( null );
    }

    public RightTuple get(final InternalFactHandle handle) {
        RightTuple current = this.first.get();
        while ( current != null ) {
            if ( handle == current.getFactHandle() ) {
                return current;
            }
            current = (RightTuple) current.getNext();
        }
        return null;
    }

    public boolean contains(final InternalFactHandle handle) {
        return get( handle ) != null;
    }

    public RightTuple get(final RightTuple rightTuple) {
        InternalFactHandle handle = rightTuple.getFactHandle();
        RightTuple current = this.first.get();
        while ( current != null ) {
            if ( handle == current.getFactHandle() ) {
                return current;
            }
            current = (RightTuple) current.getNext();
        }
        return null;
    }

    public boolean contains(final RightTuple rightTuple) {
        return get( rightTuple ) != null;
    }

    public int size() {
        int i = 0;
        RightTuple current = this.first.get();
        while ( current != null ) {
            current = (RightTuple) current.getNext();
            i++;
        }
        return i;
    }

    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }
    
	public FastIterator fastIterator() {
		return LinkedList.fastIterator;
	}      
	
	public FastIterator fullFastIterator() {
		// TODO Auto-generated method stub
		return null;
	}    	

    public boolean matches(final Object object,
                           final int objectHashCode) {
        return this.hashCode == objectHashCode && this.index.equal( this.first.get().getFactHandle().getObject(),
                                                                    object );
    }

    public boolean matches(final LeftTuple tuple,
                           final int tupleHashCode) {
        return this.hashCode == tupleHashCode && this.index.equal( this.first.get().getFactHandle().getObject(),
                                                                   tuple );
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(final Object object) {
        final ConcurrentRightTupleList other = (ConcurrentRightTupleList) object;
        return this.hashCode == other.hashCode && this.index == other.index;
    }

    public Entry getNext() {
        return this.next.get();
    }

    public void setNext(final Entry next) {
        this.next.set( next );
    }

    public boolean isIndexed() {
        return (this.index != null);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for ( RightTuple rightTuple = (RightTuple) this.first.get(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
            builder.append( rightTuple );
        }

        return builder.toString();
    }
    
    public Entry[] toArray() {
        throw new UnsupportedOperationException( "method is not implemented yet" );
    }
}
