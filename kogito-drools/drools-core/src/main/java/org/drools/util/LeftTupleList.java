/**
 * 
 */
package org.drools.util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.RightTuple;
import org.drools.util.AbstractHashTable.Index;

public class LeftTupleList
    implements
    LeftTupleMemory,
    Entry {

    public static final long       serialVersionUID = 400L;
    //      private Entry             previous;
    public Entry                   next;

    public LeftTuple               first;

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

    public void add(final LeftTuple leftTuple) {
        if ( this.first != null ) {
            leftTuple.setNext( this.first );
            this.first.setPrevious( leftTuple );
        }

        this.first = leftTuple;

        this.size++;
    }

    public void remove(final LeftTuple leftTuple) {
        LeftTuple previous = (LeftTuple) leftTuple.getPrevious();
        LeftTuple next = (LeftTuple) leftTuple.getNext();

        if ( previous != null && next != null ) {
            //remove  from middle
            previous.setNext( next );
            next.setPrevious( previous );
        } else if ( next != null ) {
            //remove from first
            this.first = next;
            next.setPrevious( null );
        } else if ( previous != null ) {
            //remove from end
            previous.setNext( null );
        } else {
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

    public Entry getPrevious() {
        return null;
    }

    public void setPrevious(Entry previous) {
        //      this.previous = previous;           
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator it = iterator();
        for ( LeftTuple leftTuple = (LeftTuple) it.next(); leftTuple != null; leftTuple = (LeftTuple) it.next() ) {
            builder.append( leftTuple + "\n" );
        }

        return builder.toString();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        next = (LeftTuple) in.readObject();
        first = (LeftTuple) in.readObject();
        hashCode = in.readInt();
        index = (Index) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( next );
        out.writeObject( first );
        out.writeInt( hashCode );
        out.writeObject( index );
    }
}