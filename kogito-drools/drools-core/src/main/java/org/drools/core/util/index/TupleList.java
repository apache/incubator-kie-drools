/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.core.util.index;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractHashTable.Index;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;

public class TupleList implements TupleMemory, Entry {

    public static final long       serialVersionUID = 510l;

    public Entry                   next;

    public Tuple                   first;
    public Tuple                   last;

    private int                    hashCode;
    private Index                  index;

    private TupleHashTableIterator iterator;

    private int                    size;

    public TupleList() {
        // this is not an index bucket
        this.hashCode = 0;
        this.index = null;
    }

    public TupleList( Tuple first, Tuple last, int size ) {
        // this is not an index bucket
        this.hashCode = 0;
        this.index = null;
        this.first = first;
        this.last = last;
        this.size = size;
    }

    public TupleList( final Index index,
                      final int hashCode ) {
        this.index = index;
        this.hashCode = hashCode;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Tuple getFirst(Tuple rightTuple) {
        return this.first;
    }
    
    public Tuple getFirst() {
        return this.first;
    }
    
    public Tuple getLast() {
        return this.last;
    }
    
    public void clear() {
        this.first = null;
        this.last = null;
        size = 0;
    }    
    
    public void removeAdd(Tuple tuple) {
        remove(tuple);
        add(tuple);
    }

    public void add(final Tuple tuple) {
        if ( this.last != null ) {
            this.last.setNext( tuple );
            tuple.setPrevious( this.last );
            this.last = tuple;
        } else {
            this.first = tuple;
            this.last = tuple;
        }
        tuple.setMemory( this );
        this.size++;

    }

    public void remove(final Tuple tuple) {
        Tuple previous = (Tuple) tuple.getPrevious();
        Tuple next = (Tuple) tuple.getNext();

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
        tuple.clear();
        this.size--;
    }

    public Tuple removeFirst() {
        Tuple tuple = this.first;
        if ( this.last == tuple ) {
            this.last = null;
            this.first = null;
        }  else {
            this.first = (Tuple) tuple.getNext();
            if ( this.first != null ) {
                this.first.setPrevious(null);
            }
        }
        tuple.clear();
        this.size--;
        return tuple;
    }

    public boolean contains(final Tuple tuple) {
        return get( tuple ) != null;
    }

    public Tuple get(final Tuple tuple) {
        Tuple current = this.first;
        while ( current != null ) {
            if ( tuple.equals( current ) ) {
                return current;
            }
            current = (Tuple) current.getNext();
        }
        return null;
    }

    public Tuple get(final InternalFactHandle handle) {
        Tuple current = this.first;
        while ( current != null ) {
            if ( handle == current.getFactHandle() ) {
                return current;
            }
            current = (Tuple) current.getNext();
        }
        return null;
    }

    public int size() {
        return this.size;
    }

    public Tuple[] toArray() {
        Tuple[] tuples = new Tuple[this.size];

        Tuple current = first;
        for ( int i = 0; i < this.size; i++ ) {
            tuples[i] = current;
            current = (Tuple) current.getNext();
        }

        return tuples;
    }

    @Override
    public IndexType getIndexType() {
        return TupleMemory.IndexType.NONE;
    }

    public FastIterator fastIterator() {
        return LinkedList.fastIterator; // contains no state, so ok to be static
    }
    
    public FastIterator fullFastIterator() {
        return LinkedList.fastIterator; // contains no state, so ok to be static
    }
    

    public FastIterator fullFastIterator(Tuple tuple) {
        return LinkedList.fastIterator; // contains no state, so ok to be static
    }    

    public Iterator<Tuple> iterator() {
        if ( this.iterator == null ) {
            this.iterator = new TupleHashTableIterator();
        }
        this.iterator.reset( this.first );
        return this.iterator;
    }

    public static class TupleHashTableIterator
        implements
        Iterator<Tuple> {
        private Tuple current;

        public void reset(Tuple first) {
            this.current = first;
        }

        public Tuple next() {
            if ( this.current != null ) {
                Tuple returnValue = this.current;
                this.current = (Tuple) current.getNext();
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

    boolean matches(Object object, int objectHashCode, boolean left) {
        if ( this.hashCode != objectHashCode ) {
            return false;
        }

        return left ?
               this.index.equal( object, this.first ) :
               this.index.equal( this.first.getFactHandle().getObject(), object );
    }

    boolean matches(Tuple tuple, int tupleHashCode, boolean left) {
        if ( this.hashCode != tupleHashCode ) {
            return false;
        }

        return left ?
               this.index.equal( this.first.getFactHandle().getObject(), tuple ) :
               this.index.equal( this.first, tuple );
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(final Object object) {
        final TupleList other = (TupleList) object;
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
        for ( Tuple tuple = (Tuple) it.next(); tuple != null; tuple = (Tuple) it.next() ) {
            builder.append(tuple).append("\n");
        }

        return builder.toString();
    }

    protected void copyStateInto(TupleList other) {
        other.next = next;
        other.first = first;
        other.last = last;
        other.hashCode = hashCode;
        other.index = index;
        other.iterator = iterator;
        other.size = size;

        for ( Tuple current = first; current != null; current = (Tuple)current.getNext()) {
            current.setMemory(other);
        }
    }
}
