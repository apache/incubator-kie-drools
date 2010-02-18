/**
 *
 */
package org.drools.core.util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleMemory;

public class RightTupleIndexHashTable extends AbstractHashTable
    implements
    RightTupleMemory {

    private static final long                         serialVersionUID = 400L;

    public static final int                           PRIME            = 31;

    private transient FieldIndexHashTableFullIterator tupleValueFullIterator;

    private int                                       startResult;

    private int                                       factSize;

    private Index                                     index;

    public RightTupleIndexHashTable() {

    }

    public RightTupleIndexHashTable(final FieldIndex[] index) {
        this( 128,
              0.75f,
              index );
    }

    public RightTupleIndexHashTable(final int capacity,
                                    final float loadFactor,
                                    final FieldIndex[] index) {
        super( capacity,
               loadFactor );

        this.startResult = RightTupleIndexHashTable.PRIME;
        for ( int i = 0, length = index.length; i < length; i++ ) {
            this.startResult = RightTupleIndexHashTable.PRIME * this.startResult + index[i].getExtractor().getIndex();
        }

        switch ( index.length ) {
            case 0 :
                throw new IllegalArgumentException( "FieldIndexHashTable cannot use an index[] of length  0" );
            case 1 :
                this.index = new SingleIndex( index,
                                              this.startResult );
                break;
            case 2 :
                this.index = new DoubleCompositeIndex( index,
                                                       this.startResult );
                break;
            case 3 :
                this.index = new TripleCompositeIndex( index,
                                                       this.startResult );
                break;
            default :
                throw new IllegalArgumentException( "FieldIndexHashTable cannot use an index[] of length  great than 3" );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        startResult = in.readInt();
        factSize = in.readInt();
        index = (Index) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeInt( startResult );
        out.writeInt( factSize );
        out.writeObject( index );
    }

    public RightTuple getFirst(LeftTuple leftTuple) {
        RightTupleList bucket = get( leftTuple );
        if ( bucket != null ) {
            return bucket.first;
        } else {
            return null;
        }
    }
    
    public RightTuple getFirst(final RightTuple rightTuple) {
        final RightTupleList bucket = getOrCreate( rightTuple.getFactHandle().getObject() );
        if ( bucket != null ) {
            return bucket.getFirst( ( RightTuple ) null );
        } else {
            return null;
        }        
    }      

    public RightTuple getLast(LeftTuple leftTuple) {
        RightTupleList bucket = get( leftTuple );
        if ( bucket != null ) {
            return bucket.last;
        } else {
            return null;
        }
    }

    public boolean isIndexed() {
        return true;
    }

    public Index getIndex() {
        return this.index;
    }

    public Entry getBucket(final Object object) {
        final int hashCode = this.index.hashCodeOf( object );
        final int index = indexOf( hashCode,
                                   this.table.length );

        return this.table[index];
    }

    public Iterator iterator() {
        if ( this.tupleValueFullIterator == null ) {
            this.tupleValueFullIterator = new FieldIndexHashTableFullIterator( this );
        }
        this.tupleValueFullIterator.reset();
        return this.tupleValueFullIterator;
    }

    public static class FieldIndexHashTableFullIterator
        implements
        Iterator {
        private AbstractHashTable hashTable;
        private Entry[]           table;
        private int               row;
        private int               length;
        private Entry             entry;

        public FieldIndexHashTableFullIterator(final AbstractHashTable hashTable) {
            this.hashTable = hashTable;
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#next()
         */
        public Object next() {
            if ( this.entry == null ) {
                // keep skipping rows until we come to the end, or find one that is populated
                while ( this.entry == null ) {
                    this.row++;
                    if ( this.row == this.length ) {
                        return null;
                    }
                    this.entry = (this.table[this.row] != null) ? ((RightTupleList) this.table[this.row]).first : null;
                }
            } else {
                this.entry = this.entry.getNext();
                if ( this.entry == null ) {
                    this.entry = (Entry) next();
                }
            }

            return this.entry;
        }

        public void remove() {
            throw new UnsupportedOperationException( "FieldIndexHashTableFullIterator does not support remove()." );
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#reset()
         */
        public void reset() {
            this.table = this.hashTable.getTable();
            this.length = this.table.length;
            this.row = -1;
            this.entry = null;
        }
    }

    public Entry[] toArray() {
        Entry[] result = new Entry[this.factSize];
        int index = 0;
        for ( int i = 0; i < this.table.length; i++ ) {
            RightTupleList bucket = (RightTupleList) this.table[i];
            while ( bucket != null ) {
                Entry entry = bucket.first;
                while ( entry != null ) {
                    result[index++] = entry;
                    entry = entry.getNext();
                }
                bucket = (RightTupleList) bucket.next;
            }
        }
        return result;
    }

    public void add(final RightTuple rightTuple) {
        final RightTupleList entry = getOrCreate( rightTuple.getFactHandle().getObject() );
        rightTuple.setMemory( entry );
        entry.add( rightTuple );
        this.factSize++;
    }

    /**
     * We assume that this rightTuple is contained in this hash table
     */
    public void remove(final RightTuple rightTuple) {
        if ( rightTuple.getMemory() != null ) {
            RightTupleList memory = rightTuple.getMemory();
            memory.remove( rightTuple );
            this.factSize--;
            if ( memory.first == null ) {
                final int index = indexOf( memory.hashCode(),
                                           this.table.length );
                RightTupleList previous = null;
                RightTupleList current = (RightTupleList) this.table[index];
                while ( current != memory ) {
                    previous = current;
                    current = (RightTupleList) current.getNext();
                }

                if ( previous != null ) {
                    previous.next = current.next;
                } else {
                    this.table[index] = current.next;
                }
                this.size--;
            }
            return;
        }

        final Object object = rightTuple.getFactHandle().getObject();
        final int hashCode = this.index.hashCodeOf( object );
        final int index = indexOf( hashCode,
                                   this.table.length );

        // search the table for  the Entry, we need to track previous, so if the Entry
        // is empty we can remove it.
        RightTupleList previous = null;
        RightTupleList current = (RightTupleList) this.table[index];
        while ( current != null ) {
            if ( current.matches( object,
                                  hashCode ) ) {
                current.remove( rightTuple );
                this.factSize--;

                if ( current.first == null ) {
                    if ( previous != null ) {
                        previous.next = current.next;
                    } else {
                        this.table[index] = current.next;
                    }
                    this.size--;
                }
                break;
            }
            previous = current;
            current = (RightTupleList) current.next;
        }
        rightTuple.setNext( null );
        rightTuple.setPrevious( null );
    }

    public boolean contains(final RightTuple rightTuple) {
        final Object object = rightTuple.getFactHandle().getObject();

        final int hashCode = this.index.hashCodeOf( object );

        final int index = indexOf( hashCode,
                                   this.table.length );

        RightTupleList current = (RightTupleList) this.table[index];
        while ( current != null ) {
            if ( current.matches( object,
                                  hashCode ) ) {
                return true;
            }
            current = (RightTupleList) current.next;
        }
        return false;
    }

    public RightTupleList get(final LeftTuple tuple) {
        //this.index.setCachedValue( tuple );

        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );
        RightTupleList entry = (RightTupleList) this.table[index];

        while ( entry != null ) {
            if ( entry.matches( tuple,
                                hashCode ) ) {
                return entry;
            }
            entry = (RightTupleList) entry.getNext();
        }

        return entry;
    }

    /**
     * We use this method to aviod to table lookups for the same hashcode; which is what we would have to do if we did
     * a get and then a create if the value is null.
     * 
     * @param value
     * @return
     */
    private RightTupleList getOrCreate(final Object object) {
        //this.index.setCachedValue( object );

        final int hashCode = this.index.hashCodeOf( object );

        final int index = indexOf( hashCode,
                                   this.table.length );
        RightTupleList entry = (RightTupleList) this.table[index];

        while ( entry != null ) {
            if ( entry.matches( object,
                                hashCode ) ) {
                return entry;
            }
            entry = (RightTupleList) entry.next;
        }

        if ( entry == null ) {
            entry = new RightTupleList( this.index,
                                        hashCode );
            entry.next = this.table[index];
            this.table[index] = entry;

            if ( this.size++ >= this.threshold ) {
                resize( 2 * this.table.length );
            }
        }
        return entry;
    }

    public int size() {
        return this.factSize;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for ( Entry entry : this.table ) {
            while ( entry != null ) {
                RightTupleList bucket = (RightTupleList) entry;
                for ( RightTuple rightTuple = bucket.getFirst( ( RightTuple ) null ); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                    builder.append( rightTuple );
                }
                entry = entry.getNext();
            }
        }

        return builder.toString();
    }
}