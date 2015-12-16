/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TupleIndexHashTable extends AbstractHashTable implements TupleMemory {

    private static final long                         serialVersionUID = 510l;

    public static final int                           PRIME            = 31;

    private int                                       startResult;

    private transient FieldIndexHashTableFullIterator tupleValueFullIterator;

    private transient FullFastIterator                fullFastIterator;

    private int                                       factSize;

    private Index                                     index;

    private boolean                                   left;

    public TupleIndexHashTable() {
        // constructor for serialisation
    }

    public TupleIndexHashTable( FieldIndex[] index, boolean left ) {
        this( 128, 0.75f, index, left );
    }

    public TupleIndexHashTable( int capacity,
                                float loadFactor,
                                FieldIndex[] index,
                                boolean left ) {
        super( capacity,
               loadFactor );

        this.left = left;

        this.startResult = PRIME;
        for ( FieldIndex i : index ) {
            this.startResult += PRIME * this.startResult + i.getExtractor().getIndex();
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
        left = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeInt( startResult );
        out.writeInt( factSize );
        out.writeObject( index );
        out.writeBoolean( left );
    }

    public void init(Entry[] table, int size, int factSize) {
        this.table = table;
        this.size = size;
        this.factSize = factSize;
    }

    public Iterator<Tuple> iterator() {
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

    public FastIterator fullFastIterator() {
        if ( fullFastIterator == null ) {
            fullFastIterator = new FullFastIterator( this.table );
        } else {
            fullFastIterator.reset(this.table);
        }
        return fullFastIterator;
    }

    public FastIterator fullFastIterator(Tuple leftTuple) {
        fullFastIterator.resume(leftTuple.getMemory(), this.table);
        return fullFastIterator;
    }

    public static class FullFastIterator implements FastIterator {
        private Entry[]     table;
        private int         row;

        public FullFastIterator(Entry[] table, int row) {
            this.table = table;
            this.row = row + 1;
        }

        public FullFastIterator(Entry[] table) {
            this.table = table;
            this.row = 0;
        }

        public void resume(Entry target, Entry[] table) {
            this.table = table;
            row = indexOf( target.hashCode(),
                           this.table.length );
            row++; // row always points to the row after the current list
        }

        public Entry next(Entry object) {
            Tuple tuple = ( Tuple ) object;
            TupleList list = null;
            if ( tuple != null ) {
                list = tuple.getMemory(); // assumes you do not pass in a null RightTuple
            }

            int length = table.length;

            while ( this.row <= length ) {
                // check if there is a current bucket
                while ( list == null ) {
                    if ( this.row < length ) {
                        // iterate while there is no current bucket, trying each array position
                        list = (TupleList) this.table[this.row];
                        this.row++;
                    } else {
                        // we've scanned the whole table and nothing is left, so return null
                        return null;
                    }

                    if ( list != null ) {
                        // we have a bucket so assign the frist LeftTuple and return
                        tuple = list.getFirst( );
                        return tuple;
                    }
                }

                tuple = (Tuple) tuple.getNext();
                if ( tuple != null ) {
                    // we have a next tuple so return
                    return tuple;
                } else {
                    list = list.getNext();
                    // try the next bucket if we have a shared array position
                    if ( list != null ) {
                        // if we have another bucket, assign the first LeftTuple and return
                        tuple = list.getFirst( );
                        return tuple;
                    }
                }
            }
            return null;
        }

        public boolean isFullIterator() {
            return true;
        }

        public void reset(Entry[] table) {
            this.table = table;
            this.row = 0;
        }

    }

    public Tuple getFirst(final Tuple rightTuple) {
        TupleList bucket = get( rightTuple, !left );
        return bucket != null ? bucket.getFirst() : null;
    }

    public boolean isIndexed() {
        return true;
    }

    public Index getIndex() {
        return this.index;
    }

    @Override
    public int getResizeHashcode(Entry entry) {
        // Entry is always LeftTupleList which caches the hashcode, so just return it
        return  entry.hashCode();
    }

    public static class FieldIndexHashTableFullIterator
        implements
        Iterator<Tuple> {
        private final AbstractHashTable hashTable;
        private Entry[]                 table;
        private int                     row;
        private int                     length;
        private TupleList               list;
        private Tuple                   tuple;

        public FieldIndexHashTableFullIterator(final AbstractHashTable hashTable) {
            this.hashTable = hashTable;
            reset();
        }

        public Tuple next() {
            while ( this.row <= this.length ) {
                // check if there is a current bucket
                while ( this.list == null ) {
                    if ( this.row < length ) {
                        // iterate while there is no current bucket, trying each array position
                        this.list = (TupleList) this.table[this.row];
                        this.row++;
                    } else {
                        // we've scanned the whole table and nothing is left, so return null
                        return null;
                    }

                    if ( this.list != null ) {
                        // we have a bucket so assign the first LeftTuple and return
                        this.tuple = this.list.getFirst( );
                        return this.tuple;
                    }
                }

                this.tuple = (Tuple) this.tuple.getNext();
                if ( this.tuple != null ) {
                    // we have a next tuple so return
                    return this.tuple;
                } else {
                    this.list = this.list.getNext();
                    // try the next bucket if we have a shared array position
                    if ( this.list != null ) {
                        // if we have another bucket, assign the first LeftTuple and return
                        this.tuple = this.list.getFirst( );
                        return this.tuple;
                    }
                }
            }
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException( "FieldIndexHashTableFullIterator does not support remove()." );
        }

        /* (non-Javadoc)
         * @see org.kie.util.Iterator#reset()
         */
        public void reset() {
            this.table = this.hashTable.getTable();
            this.length = this.table.length;
            this.row = 0;
            this.list = null;
            this.tuple = null;
        }
    }

    public Tuple[] toArray() {
        Tuple[] result = new Tuple[this.factSize];
        int index = 0;
        for (Entry aTable : this.table) {
            TupleList bucket = (TupleList) aTable;
            while (bucket != null) {
                Tuple entry = bucket.getFirst();
                while (entry != null) {
                    result[index++] = entry;
                    entry = (Tuple) entry.getNext();
                }
                bucket = bucket.getNext();
            }
        }
        return result;
    }

    public void removeAdd(Tuple tuple) {
        TupleList memory = tuple.getMemory();
        memory.remove( tuple );

        final int newHashCode = this.index.hashCodeOf( tuple, left );
        if ( newHashCode == memory.hashCode() ) {
            // it's the same bucket, so re-use and return
            memory.add( tuple );
            return;
        }

        // bucket is empty so remove.
        this.factSize--;
        if ( memory.getFirst() == null ) {
            final int index = indexOf( memory.hashCode(),
                                       this.table.length );
            TupleList previous = null;
            TupleList current = (TupleList) this.table[index];
            while ( current != memory ) {
                previous = current;
                current = current.getNext();
            }

            if ( previous != null ) {
                previous.setNext( current.getNext() );
            } else {
                this.table[index] = current.getNext();
            }
            this.size--;
        }

        add( tuple );
    }

    public void add(final Tuple tuple) {
        final TupleList entry = getOrCreate( tuple );
        entry.add( tuple );
        this.factSize++;
    }

    public void remove(final Tuple tuple) {
        TupleList memory = tuple.getMemory();
        memory.remove( tuple );
        this.factSize--;
        if ( memory.getFirst() == null ) {
            final int index = indexOf( memory.hashCode(),
                                       this.table.length );
            TupleList previous = null;
            TupleList current = (TupleList) this.table[index];
            while ( current != memory ) {
                previous = current;
                current = current.getNext();
            }

            if ( previous != null ) {
                previous.setNext( current.getNext() );
            } else {
                this.table[index] = current.getNext();
            }
            this.size--;
        }
        tuple.clear();
    }

    /**
     * We use this method to aviod to table lookups for the same hashcode; which is what we would have to do if we did
     * a get and then a create if the value is null.
     */
    private TupleList getOrCreate(final Tuple tuple) {
        final int hashCode = this.index.hashCodeOf( tuple, left );
        final int index = indexOf( hashCode, this.table.length );
        TupleList entry = (TupleList) this.table[index];

        // search to find an existing entry
        while ( entry != null ) {
            if ( matchesRight( entry, tuple, hashCode ) ) {
                return entry;
            }
            entry = entry.getNext();
        }

        // entry does not exist, so create
        entry = new TupleList( this.index, hashCode );
        entry.setNext( (TupleList) this.table[index] );
        this.table[index] = entry;

        if ( this.size++ >= this.threshold ) {
            resize( 2 * this.table.length );
        }
        return entry;
    }

    public boolean contains(final Tuple tuple) {
        return get(tuple, left) != null;
    }

    private TupleList get(final Tuple tuple, boolean isLeftTuple) {
        final int hashCode = this.index.hashCodeOf( tuple, isLeftTuple );

        final int index = indexOf( hashCode, this.table.length );
        TupleList entry = (TupleList) this.table[index];

        while ( entry != null ) {
            if ( matches(entry, tuple, hashCode ) ) {
                return entry;
            }
            entry = entry.getNext();
        }

        return null;
    }

    private boolean matches(TupleList list, Tuple tuple, int tupleHashCode) {
        if ( list.hashCode() != tupleHashCode ) {
            return false;
        }

        return left ?
               this.index.equal( tuple.getFactHandle().getObject(), list.getFirst() ) :
               this.index.equal( list.getFirst().getFactHandle().getObject(), tuple );
    }

    private boolean matchesRight( TupleList list, Tuple tuple, int tupleHashCode ) {
        if ( list.hashCode() != tupleHashCode ) {
            return false;
        }

        return left ?
               this.index.equal( list.getFirst(), tuple ) :
               this.index.equal( list.getFirst().getFactHandle().getObject(), tuple.getFactHandle().getObject() );
    }

    public int size() {
        return this.factSize;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator it = iterator();
        for ( Tuple leftTuple = (Tuple) it.next(); leftTuple != null; leftTuple = (Tuple) it.next() ) {
            builder.append(leftTuple).append("\n");
        }

        return builder.toString();
    }

    public void clear() {
        super.clear();
        this.startResult = PRIME;
        this.factSize = 0;
        this.fullFastIterator = null;
        this.tupleValueFullIterator = null;
    }

    public IndexType getIndexType() {
        return IndexType.EQUAL;
    }

    public Tuple getFirst(Tuple leftTuple, InternalFactHandle factHandle) {
        TupleList bucket = get( leftTuple, factHandle );
        return bucket != null ? bucket.getFirst() : null;
    }

    private TupleList get(final Tuple tuple, InternalFactHandle factHandle) {
        int hashCode = this.index.hashCodeOf( tuple, !left );
        int index = indexOf( hashCode, this.table.length );

        TupleList entry = (TupleList) this.table[index];

        while ( entry != null ) {
            if ( matches( entry, tuple, hashCode, factHandle ) ) {
                return entry;
            }
            entry = entry.getNext();
        }

        return entry;
    }

    private boolean matches(TupleList tupleList, Tuple tuple, int tupleHashCode, InternalFactHandle factHandle) {
        if ( tupleList.hashCode() != tupleHashCode ) {
            return false;
        }

        if ( tupleList.getFirst().getFactHandle() == factHandle ) {
            Tuple rightTuple = ( Tuple ) tupleList.getFirst().getNext();
            if ( rightTuple != null ) {
                return this.index.equal( rightTuple.getFactHandle().getObject(),
                                         tuple );
            }
        }

        return this.index.equal( tupleList.getFirst().getFactHandle().getObject(), tuple );
    }
}
