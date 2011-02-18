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

import org.drools.core.util.AbstractHashTable.Index;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.RightTuple;

public class LeftTupleList
    implements
    LeftTupleMemory,
    Entry {

    public static final long       serialVersionUID = 510l;
    //      private Entry             previous;
    public Entry                   next;

    public LeftTuple               first;
    public LeftTuple               last;

    private int                    hashCode;
    private Index                  index;

    private TupleHashTableIterator iterator;

    private int                    size;

    public LeftTupleList() {
        // this is not an index bucket        
        this.hashCode = 0;
        this.index = null;
    }

    public LeftTupleList(final Index index,
                         final int hashCode) {
        this.index = index;
        this.hashCode = hashCode;
    }

    public LeftTuple getFirst(RightTuple rightTuple) {
        return this.first;
    }
    
    public LeftTuple getFirst(LeftTuple leftTuple) {
        return this.first;
    }     
    
    public LeftTuple getLast(RightTuple lastTuple) {
        return this.first;
    }    

    public void add(final LeftTuple leftTuple) {
        if ( this.last != null ) {
            this.last.setNext( leftTuple );
            leftTuple.setPrevious( this.last );
            this.last = leftTuple;
        } else {
            this.first = leftTuple;
            this.last = leftTuple;;
        }

        this.size++;
    }

    public void remove(final LeftTuple leftTuple) {
        LeftTuple previous = (LeftTuple) leftTuple.getPrevious();
        LeftTuple next = (LeftTuple) leftTuple.getNext();

        if ( previous != null && next != null ) {
            // remove from middle
            previous.setNext( next );
            next.setPrevious( previous );
        } else if ( next != null ) {
            // remove from first
            this.first = next;
            next.setPrevious( null );
        } else if ( previous != null ) {
            // remove from end
            this.last = previous;
            previous.setNext( null );
        } else {
            // remove everything
            this.last = null;
            this.first = null;
        }
        
        leftTuple.setPrevious( null );
        leftTuple.setNext( null );

        this.size--;
    }

    public boolean contains(final LeftTuple leftTuple) {
        return get( leftTuple ) != null;
    }

    public Object get(final LeftTuple leftTtuple) {
        LeftTuple current = this.first;
        while ( current != null ) {
            if ( leftTtuple.equals( current ) ) {
                return current;
            }
            current = (LeftTuple) current.getNext();
        }
        return null;
    }

    public int size() {
        return this.size;
    }

    public LeftTuple[] toArray() {
        LeftTuple[] tuples = new LeftTuple[this.size];

        LeftTuple current = first;
        for ( int i = 0; i < this.size; i++ ) {
            tuples[i] = current;
            current = (LeftTuple) current.getNext();
        }

        return tuples;
    }

    public Entry getBucket(final Object object) {
        return this.first;
    }
    
    public FastIterator fastIterator() {
    	return LinkedList.fastIterator; // contains no state, so ok to be static   
    }    

    public Iterator iterator() {
        if ( this.iterator == null ) {
            this.iterator = new TupleHashTableIterator();
        }
        this.iterator.reset( this.first );
        return this.iterator;
    }

    public static class TupleHashTableIterator
        implements
        Iterator {
        private LeftTuple current;

        public void reset(LeftTuple first) {
            this.current = first;
        }

        public Object next() {
            if ( this.current != null ) {
                LeftTuple returnValue = this.current;
                this.current = (LeftTuple) current.getNext();
                return returnValue;
            } else {
                return null;
            }
        }

        public void remove() {
            // do nothing
        }
    }

    public boolean isIndexed() {
        return false;
    }

    public boolean matches(final Object object,
                           final int objectHashCode) {
        return this.hashCode == objectHashCode && this.index.equal( object,
                                                                    this.first );
    }

    public boolean matches(final LeftTuple tuple,
                           final int tupleHashCode) {
        return this.hashCode == tupleHashCode && this.index.equal( this.first,
                                                                   tuple );
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(final Object object) {
        final LeftTupleList other = (LeftTupleList) object;
        return this.hashCode == other.hashCode && this.index == other.index;
    }

    public Entry getNext() {
        return this.next;
    }

    public void setNext(final Entry next) {
        this.next = next;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator it = iterator();
        for ( LeftTuple leftTuple = (LeftTuple) it.next(); leftTuple != null; leftTuple = (LeftTuple) it.next() ) {
            builder.append( leftTuple + "\n" );
        }

        return builder.toString();
    }
}
