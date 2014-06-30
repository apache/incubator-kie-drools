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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleMemory;

public class RightTupleIndexHashTable extends AbstractHashTable
    implements
    RightTupleMemory {

    private static final long                         serialVersionUID = 510l;

    public static final int                           PRIME            = 31;

    private transient FieldIndexHashTableFullIterator tupleValueFullIterator;
    
    private transient FullFastIterator                fullFastIterator;

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
            this.startResult += RightTupleIndexHashTable.PRIME * this.startResult + index[i].getExtractor().getIndex();
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

    public RightTuple getFirst(LeftTuple leftTuple, InternalFactHandle factHandle, FastIterator rightTupleIterator) {
        RightTupleList bucket = get( leftTuple, factHandle );
        if ( bucket != null ) {
            return bucket.first;
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
        } else {
            this.tupleValueFullIterator.reset();
        }
        return this.tupleValueFullIterator;
    }
    
    @Override
    public int getResizeHashcode(Entry entry) {
        // Entry is always LeftTupleList which caches the hashcode, so just return it
        return  entry.hashCode();
    }    
    
    
    public FastIterator fastIterator() {
        return LinkedList.fastIterator;
    }

    public FastIterator fullFastIterator() {
        if ( fullFastIterator == null ) {
            fullFastIterator = new FullFastIterator( this.table );
            
        } else {
            fullFastIterator.reset(this.table );
        }
        return fullFastIterator;
    }
    
    public FastIterator fullFastIterator(RightTuple rightTuple) {
        fullFastIterator.resume(rightTuple.getMemory(), this.table);
        return fullFastIterator;
    }    

    public static class FullFastIterator implements FastIterator {
        private Entry[]           table;
        private int               row;
        
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
            RightTuple rightTuple = ( RightTuple ) object;
            RightTupleList list = null;
            if ( rightTuple != null ) {
                list = rightTuple.getMemory(); // assumes you do not pass in a null RightTuple
            }

            int length = table.length;

            while ( this.row <= length ) {
                // check if there is a current bucket
                while ( list == null ) {                    
                    if ( this.row < length ) {
                        // iterate while there is no current bucket, trying each array position
                        list = (RightTupleList) this.table[this.row];
                        this.row++;                   
                    } else {     
                        // we've scanned the whole table and nothing is left, so return null
                        return null;
                    }
                    
                    if ( list != null ) {
                        // we have a bucket so assign the frist LeftTuple and return
                        rightTuple = (RightTuple) list.getFirst( );
                        return rightTuple;
                    }                
                }

                rightTuple = (RightTuple) rightTuple.getNext();
                if ( rightTuple != null ) {
                    // we have a next tuple so return
                    return rightTuple;
                } else {
                    list = (RightTupleList) list.getNext();
                    // try the next bucket if we have a shared array position
                    if ( list != null ) {
                        // if we have another bucket, assign the first RightTuple and return
                        rightTuple = (RightTuple) list.getFirst( );
                        return rightTuple;
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
    
    public static class FieldIndexHashTableFullIterator
        implements
        Iterator {
        private AbstractHashTable hashTable;
        private Entry[]           table;
        private int               row;
        private int               length;
        private RightTupleList     list;
        private RightTuple         rightTuple;

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
                        this.list = (RightTupleList) this.table[this.row];
                        this.row++;
                    } else {
                        // we've scanned the whole table and nothing is left, so return null
                        return null;
                    }
                    
                    if ( this.list != null ) {
                        // we have a bucket so assign the frist LeftTuple and return
                        this.rightTuple = (RightTuple) this.list.getFirst();
                        return this.rightTuple;
                    }
                    
                }

                this.rightTuple = (RightTuple) this.rightTuple.getNext();
                if ( this.rightTuple != null ) {
                    // we have a next tuple so return
                    return this.rightTuple;
                } else {
                    this.list = (RightTupleList) this.list.getNext();
                    // try the next bucket if we have a shared array position
                    if ( this.list != null ) {
                        // if we have another bucket, assign the first LeftTuple and return
                        this.rightTuple = (RightTuple) this.list.getFirst( );
                        return this.rightTuple;
                    }
                }
            }
            return null;
        }

        /* (non-Javadoc)
         * @see org.kie.util.Iterator#reset()
         */
        public void reset() {
            this.table = this.hashTable.getTable();
            this.length = this.table.length;
            this.row = 0;
            this.list = null;
            this.rightTuple = null;
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
        entry.add( rightTuple );
        this.factSize++;
    }
    
    public void removeAdd(final RightTuple rightTuple) {
        RightTupleList memory = rightTuple.getMemory();
        memory.remove( rightTuple );
        
        final int newHashCode = this.index.hashCodeOf( rightTuple.getFactHandle().getObject() );
        if ( newHashCode == memory.hashCode() ) {
            // it's the same bucket, so re-use and return
            memory.add( rightTuple );
            return;
        }
           
        // bucket is empty so remove.
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

        add( rightTuple );
    }

    /**
     * We assume that this rightTuple is contained in this hash table
     */
    public void remove(final RightTuple rightTuple) {
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
        rightTuple.clear();
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

    public RightTupleList get(final LeftTuple tuple, InternalFactHandle factHandle) {
        //this.index.setCachedValue( tuple );

        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );
        
        RightTupleList entry = (RightTupleList) this.table[index];
        

        while ( entry != null ) {
            if ( entry.matches( tuple,
                                hashCode,
                                factHandle ) ) {
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

    private RightTupleList get(final Object object) {
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
                for ( RightTuple rightTuple = bucket.getFirst( ); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                    builder.append( rightTuple );
                }
                entry = entry.getNext();
            }
        }

        return builder.toString();
    }

    public IndexType getIndexType() {
        return IndexType.EQUAL;
    }

    public void clear() {
        super.clear();
        this.startResult = PRIME;
        this.factSize = 0;
        this.fullFastIterator = null;
        this.tupleValueFullIterator = null;
    }
}
