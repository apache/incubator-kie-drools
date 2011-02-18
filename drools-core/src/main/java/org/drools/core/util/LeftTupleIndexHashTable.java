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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.RightTuple;

public class LeftTupleIndexHashTable extends AbstractHashTable
    implements
    LeftTupleMemory {

    private static final long                         serialVersionUID = 510l;

    public static final int                           PRIME            = 31;

    private int                                       startResult;

    private transient FieldIndexHashTableFullIterator tupleValueFullIterator;

    private int                                       factSize;

    private Index                                     index;

    public LeftTupleIndexHashTable() {
        // constructor for serialisation
    }

    public LeftTupleIndexHashTable(final FieldIndex[] index) {
        this( 128,
              0.75f,
              index );
    }

    public LeftTupleIndexHashTable(final int capacity,
                                   final float loadFactor,
                                   final FieldIndex[] index) {
        super( capacity,
               loadFactor );

        this.startResult = LeftTupleIndexHashTable.PRIME;
        for ( int i = 0, length = index.length; i < length; i++ ) {
            this.startResult += LeftTupleIndexHashTable.PRIME * this.startResult + index[i].getExtractor().getIndex();
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

    public Iterator iterator() {
        if ( this.tupleValueFullIterator == null ) {
            this.tupleValueFullIterator = new FieldIndexHashTableFullIterator( this );
        } else {
            this.tupleValueFullIterator.reset();
        }
        return this.tupleValueFullIterator;
    }
    
	public FastIterator fastIterator() {
		return LinkedList.fastIterator;
	}

    public LeftTuple getFirst(final RightTuple rightTuple) {
        LeftTupleList bucket = get( rightTuple );
        if ( bucket != null ) {
            return bucket.getFirst( (LeftTuple) null );
        } else {
            return null;
        }
    }

    public LeftTuple getFirst(final LeftTuple leftTuple) {
        final LeftTupleList bucket = get( leftTuple );
        if ( bucket != null ) {
            return bucket.getFirst( (LeftTuple) null );
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

    public static class FieldIndexHashTableFullIterator
        implements
        Iterator {
        private AbstractHashTable hashTable;
        private Entry[]           table;
        private int               row;
        private int               length;
        private LeftTupleList     list;
        private LeftTuple         leftTuple;

        public FieldIndexHashTableFullIterator(final AbstractHashTable hashTable) {
            this.hashTable = hashTable;
            reset();
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#next()
         */
        public Object next() {
            while ( this.row < this.length ) {
                // check if there is a current bucket
                while ( this.list == null ) {
                    // iterate while there is no current bucket, trying each array position
                    this.list = (LeftTupleList) this.table[this.row];
                    this.row++;
                    
                    if ( this.list != null ) {
                        // we have a bucket so assign the first LeftTuple and return
                        this.leftTuple = (LeftTuple) this.list.getFirst( (LeftTuple) null );
                        return this.leftTuple;
                    } else if ( this.row >= this.length ) {
                        // we've scanned the whole table and nothing is left, so return null
                        return null;
                    }
                    
                }

                this.leftTuple = (LeftTuple) this.leftTuple.getNext();
                if ( this.leftTuple != null ) {
                    // we have a next tuple so return
                    return this.leftTuple;
                } else {
                    this.list = (LeftTupleList) this.list.getNext();
                    // try the next bucket if we have a shared array position
                    if ( this.list != null ) {
                        // if we have another bucket, assign the first LeftTuple and return
                        this.leftTuple = (LeftTuple) this.list.getFirst( (LeftTuple) null );
                        return this.leftTuple;
                    }
                }
            }
            return null;
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
            this.row = 0;
            this.list = null;
            this.leftTuple = null;
        }
    }

    public LeftTuple[] toArray() {
        LeftTuple[] result = new LeftTuple[this.factSize];
        int index = 0;
        for ( int i = 0; i < this.table.length; i++ ) {
            LeftTupleList bucket = (LeftTupleList) this.table[i];
            while ( bucket != null ) {
                LeftTuple entry = (LeftTuple) bucket.getFirst( (LeftTuple) null );
                while ( entry != null ) {
                    result[index++] = entry;
                    entry = (LeftTuple) entry.getNext();
                }
                bucket = (LeftTupleList) bucket.getNext();
            }
        }
        return result;
    }

    public void add(final LeftTuple tuple) {
        final LeftTupleList entry = getOrCreate( tuple );
        tuple.setMemory( entry );
        entry.add( tuple );
        this.factSize++;
    }

    public void remove(final LeftTuple leftTuple) {
        if ( leftTuple.getMemory() != null ) {
            LeftTupleList memory = leftTuple.getMemory();
            memory.remove( leftTuple );
            this.factSize--;
            if ( memory.first == null ) {
                final int index = indexOf( memory.hashCode(),
                                           this.table.length );
                LeftTupleList previous = null;
                LeftTupleList current = (LeftTupleList) this.table[index];
                while ( current != memory ) {
                    previous = current;
                    current = (LeftTupleList) current.getNext();
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

        final int hashCode = this.index.hashCodeOf( leftTuple );
        final int index = indexOf( hashCode,
                                   this.table.length );

        // search the table for  the Entry, we need to track previous, so if the Entry
        // is empty we can remove it.
        LeftTupleList previous = null;
        LeftTupleList current = (LeftTupleList) this.table[index];
        while ( current != null ) {
            if ( current.matches( leftTuple,
                                  hashCode ) ) {
                current.remove( leftTuple );
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
            current = (LeftTupleList) current.next;
        }
        leftTuple.setNext( null );
        leftTuple.setPrevious( null );
    }

    public boolean contains(final LeftTuple tuple) {
        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );

        LeftTupleList current = (LeftTupleList) this.table[index];
        while ( current != null ) {
            if ( current.matches( tuple,
                                  hashCode ) ) {
                return true;
            }
            current = (LeftTupleList) current.next;
        }
        return false;
    }

    public LeftTupleList get(final RightTuple rightTuple) {
        final Object object = rightTuple.getFactHandle().getObject();
        final int hashCode = this.index.hashCodeOf( object );

        final int index = indexOf( hashCode,
                                   this.table.length );
        LeftTupleList entry = (LeftTupleList) this.table[index];

        while ( entry != null ) {
            if ( entry.matches( object,
                                hashCode ) ) {
                return entry;
            }
            entry = (LeftTupleList) entry.getNext();
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
    private LeftTupleList getOrCreate(final LeftTuple tuple) {
        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );
        LeftTupleList entry = (LeftTupleList) this.table[index];

        // search to find an existing entry
        while ( entry != null ) {
            if ( entry.matches( tuple,
                                hashCode ) ) {
                return entry;
            }
            entry = (LeftTupleList) entry.next;
        }

        // entry does not exist, so create
        if ( entry == null ) {
            entry = new LeftTupleList( this.index,
                                       hashCode );
            entry.next = this.table[index];
            this.table[index] = entry;

            if ( this.size++ >= this.threshold ) {
                resize( 2 * this.table.length );
            }
        }
        return entry;
    }

    private LeftTupleList get(final LeftTuple tuple) {
        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );
        LeftTupleList entry = (LeftTupleList) this.table[index];

        // search to find an existing entry
        while ( entry != null ) {
            if ( entry.matches( tuple,
                                hashCode ) ) {
                return entry;
            }
            entry = (LeftTupleList) entry.next;
        }
        return entry;
    }

    public int size() {
        return this.factSize;
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
