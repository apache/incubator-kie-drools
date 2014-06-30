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

package org.drools.core.util.index;

import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class LeftTupleIndexHashTable extends AbstractHashTable
    implements
    LeftTupleMemory {

    private static final long                         serialVersionUID = 510l;

    public static final int                           PRIME            = 31;

    private int                                       startResult;

    private transient FieldIndexHashTableFullIterator tupleValueFullIterator;
    
    private transient FullFastIterator                fullFastIterator;

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
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeInt( startResult );
        out.writeInt( factSize );
        out.writeObject( index );
    }   
    
    public void init(Entry[] table, int size, int factSize) {
        this.table = table;
        this.size = size;
        this.factSize = factSize;
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

    public FastIterator fullFastIterator() {
        if ( fullFastIterator == null ) {
            fullFastIterator = new FullFastIterator( this.table );
        } else {
            fullFastIterator.reset(this.table);
        }
        return fullFastIterator;
    }

    public FastIterator fullFastIterator(LeftTuple leftTuple) {
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
            LeftTuple leftTuple = ( LeftTuple ) object;
            LeftTupleList list = null;
            if ( leftTuple != null ) {
                list = leftTuple.getMemory(); // assumes you do not pass in a null RightTuple
            }

            int length = table.length;

            while ( this.row <= length ) {
                // check if there is a current bucket
                while ( list == null ) {
                    if ( this.row < length ) {
                        // iterate while there is no current bucket, trying each array position
                        list = (LeftTupleList) this.table[this.row];
                        this.row++;
                    } else {
                        // we've scanned the whole table and nothing is left, so return null
                        return null;                        
                    }
                    
                    if ( list != null ) {
                        // we have a bucket so assign the frist LeftTuple and return
                        leftTuple = list.getFirst( );
                        return leftTuple;
                    }                    
                }

                leftTuple = (LeftTuple) leftTuple.getNext();
                if ( leftTuple != null ) {
                    // we have a next tuple so return
                    return leftTuple;
                } else {
                    list = (LeftTupleList) list.getNext();
                    // try the next bucket if we have a shared array position
                    if ( list != null ) {
                        // if we have another bucket, assign the first LeftTuple and return
                        leftTuple = list.getFirst( );
                        return leftTuple;
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

    public LeftTuple getFirst(final RightTuple rightTuple) {
        LeftTupleList bucket = get( rightTuple );
        if ( bucket != null ) {
            return bucket.getFirst( );
        } else {
            return null;
        }
    }

    public LeftTuple getFirst(final LeftTuple leftTuple) {
        final LeftTupleList bucket = get( leftTuple );
        if ( bucket != null ) {
            return bucket.getFirst( );
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
    
    @Override
    public int getResizeHashcode(Entry entry) {
        // Entry is always LeftTupleList which caches the hashcode, so just return it
        return  entry.hashCode();
    }    

    public static class FieldIndexHashTableFullIterator
        implements
        Iterator {
        private final AbstractHashTable hashTable;
        private Entry[]                 table;
        private int                     row;
        private int                     length;
        private LeftTupleList           list;
        private LeftTuple               leftTuple;

        public FieldIndexHashTableFullIterator(final AbstractHashTable hashTable) {
            this.hashTable = hashTable;
            reset();
        }

        /* (non-Javadoc)
         * @see org.kie.util.Iterator#next()
         */
        public Object next() {
            while ( this.row <= this.length ) {
                // check if there is a current bucket
                while ( this.list == null ) {
                    if ( this.row < length ) {
                        // iterate while there is no current bucket, trying each array position
                        this.list = (LeftTupleList) this.table[this.row];
                        this.row++;
                    } else {
                        // we've scanned the whole table and nothing is left, so return null
                        return null;                        
                    }
                    
                    if ( this.list != null ) {
                        // we have a bucket so assign the first LeftTuple and return
                        this.leftTuple = this.list.getFirst( );
                        return this.leftTuple;
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
                        this.leftTuple = this.list.getFirst( );
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
         * @see org.kie.util.Iterator#reset()
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
        for (Entry aTable : this.table) {
            LeftTupleList bucket = (LeftTupleList) aTable;
            while (bucket != null) {
                LeftTuple entry = bucket.getFirst();
                while (entry != null) {
                    result[index++] = entry;
                    entry = (LeftTuple) entry.getNext();
                }
                bucket = (LeftTupleList) bucket.getNext();
            }
        }
        return result;
    }
    
    public void removeAdd(LeftTuple leftTuple) {
        LeftTupleList memory = leftTuple.getMemory();
        memory.remove( leftTuple );
        
        final int newHashCode = this.index.hashCodeOf( leftTuple );
        if ( newHashCode == memory.hashCode() ) {
            // it's the same bucket, so re-use and return
            memory.add( leftTuple );
            return;
        }
           
        // bucket is empty so remove.
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

        add( leftTuple );
    }    

    public void add(final LeftTuple tuple) {
        final LeftTupleList entry = getOrCreate( tuple );
        entry.add( tuple );
        this.factSize++;
    }

    public void remove(final LeftTuple leftTuple) {
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
        leftTuple.clear();
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
        entry = new LeftTupleList( this.index,
                                   hashCode );
        entry.next = this.table[index];
        this.table[index] = entry;

        if ( this.size++ >= this.threshold ) {
            resize( 2 * this.table.length );
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
}
