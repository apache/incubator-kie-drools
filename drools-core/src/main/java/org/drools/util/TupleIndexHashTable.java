/**
 *
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;

import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.Externalizable;

public class TupleIndexHashTable extends AbstractHashTable
    implements
    LeftTupleMemory {

    private static final long               serialVersionUID = 400L;

    public static final int                 PRIME            = 31;

    private int                             startResult;

    private FieldIndexHashTableIterator     tupleValueIterator;
    private FieldIndexHashTableFullIterator tupleValueFullIterator;

    private int                             factSize;

    private Index                           index;

    public TupleIndexHashTable() {
    }

    public TupleIndexHashTable(final FieldIndex[] index) {
        this( 16,
              0.75f,
              index );
    }

    public TupleIndexHashTable(final int capacity,
                               final float loadFactor,
                               final FieldIndex[] index) {
        super( capacity,
               loadFactor );

        this.startResult = TupleIndexHashTable.PRIME;
        for ( int i = 0, length = index.length; i < length; i++ ) {
            this.startResult += TupleIndexHashTable.PRIME * this.startResult + index[i].getExtractor().getIndex();
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

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        startResult = in.readInt();
        tupleValueIterator  = (FieldIndexHashTableIterator)in.readObject();
        tupleValueFullIterator  = (FieldIndexHashTableFullIterator)in.readObject();
        factSize    = in.readInt();
        index       = (Index)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(startResult);
        out.writeObject(tupleValueIterator);
        out.writeObject(tupleValueFullIterator);
        out.writeInt(factSize);
        out.writeObject(index);
    }

    public Iterator iterator() {
        if ( this.tupleValueFullIterator == null ) {
            this.tupleValueFullIterator = new FieldIndexHashTableFullIterator( this );
        }
        this.tupleValueFullIterator.reset();
        return this.tupleValueFullIterator;
    }

    public Iterator iterator(final InternalFactHandle handle) {
        if ( this.tupleValueIterator == null ) {
            this.tupleValueIterator = new FieldIndexHashTableIterator();
        }
        final FieldIndexEntry entry = get( handle );
        this.tupleValueIterator.reset( (entry != null) ? entry.first : null );
        return this.tupleValueIterator;
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

    /**
     * Fast re-usable iterator
     *
     */
    public static class FieldIndexHashTableIterator
        implements
        Iterator, Externalizable {
        private Entry entry;

        public FieldIndexHashTableIterator() {

        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            entry   = (Entry)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(entry);
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#next()
         */
        public Object next() {
            final Entry current = this.entry;
            this.entry = (this.entry != null) ? this.entry.getNext() : null;
            return current;
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#reset()
         */
        public void reset(final Entry entry) {
            this.entry = entry;
        }
    }

    public static class FieldIndexHashTableFullIterator
        implements
        Iterator, Externalizable {
        private AbstractHashTable hashTable;
        private Entry[]           table;
        private int               row;
        private int               length;
        private Entry             entry;

        public FieldIndexHashTableFullIterator() {

        }
        public FieldIndexHashTableFullIterator(final AbstractHashTable hashTable) {
            this.hashTable = hashTable;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            hashTable   = (AbstractHashTable)in.readObject();
            table   = (Entry[])in.readObject();
            row     = in.readInt();
            length  = in.readInt();
            entry   = (Entry)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(hashTable);
            out.writeObject(table);
            out.writeInt(row);
            out.writeInt(length);
            out.writeObject(entry);
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
                    this.entry = (this.table[this.row] != null) ? ((FieldIndexEntry) this.table[this.row]).first : null;
                }
            } else {
                this.entry = this.entry.getNext();
                if ( this.entry == null ) {
                    this.entry = (Entry) next();
                }
            }

            return this.entry;
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
            FieldIndexEntry fieldIndexEntry = (FieldIndexEntry)this.table[i];
            while ( fieldIndexEntry != null ) {
                Entry entry = fieldIndexEntry.getFirst();
                while ( entry != null ) {
                    result[index++] = entry;
                    entry = entry.getNext();
                }
                fieldIndexEntry  = ( FieldIndexEntry ) fieldIndexEntry.getNext();
            }
        }
        return result;
    }

    public void add(final LeftTuple tuple) {
        final FieldIndexEntry entry = getOrCreate( tuple );
        entry.add( tuple );
        this.factSize++;
    }

    public boolean add(final LeftTuple tuple,
                       final boolean checkExists) {
        throw new UnsupportedOperationException( "FieldIndexHashTable does not support add(ReteTuple tuple, boolean checkExists)" );
    }

    public LeftTuple remove(final LeftTuple tuple) {
        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );

        // search the table for  the Entry, we need to track previous  and next, so if the
        // Entry is empty after  its had the FactEntry removed, we must remove  it from the table
        FieldIndexEntry previous = (FieldIndexEntry) this.table[index];
        FieldIndexEntry current = previous;
        while ( current != null ) {
            final FieldIndexEntry next = (FieldIndexEntry) current.next;
            if ( current.matches( tuple,
                                  hashCode ) ) {
                final LeftTuple old = current.remove( tuple );
                this.factSize--;
                // If the FactEntryIndex is empty, then remove it from the hash table
                if ( current.first == null ) {
                    if ( previous == current ) {
                        this.table[index] = next;
                    } else {
                        previous.next = next;
                    }
                    current.next = null;
                    this.size--;
                }
                return old;
            }
            previous = current;
            current = next;
        }
        return null;
    }

    public boolean contains(final LeftTuple tuple) {
        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );

        FieldIndexEntry current = (FieldIndexEntry) this.table[index];
        while ( current != null ) {
            if ( current.matches( tuple,
                                  hashCode ) ) {
                return true;
            }
            current = (FieldIndexEntry) current.next;
        }
        return false;
    }

    public FieldIndexEntry get(final InternalFactHandle handle) {
        final Object object = handle.getObject();
        final int hashCode = this.index.hashCodeOf( handle.getObject() );

        final int index = indexOf( hashCode,
                                   this.table.length );
        FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

        while ( entry != null ) {
            if ( entry.matches( object,
                                hashCode ) ) {
                return entry;
            }
            entry = (FieldIndexEntry) entry.getNext();
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
    private FieldIndexEntry getOrCreate(final LeftTuple tuple) {
        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );
        FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

        // search to find an existing entry
        while ( entry != null ) {
            if ( entry.matches( tuple,
                                hashCode ) ) {
                return entry;
            }
            entry = (FieldIndexEntry) entry.next;
        }

        // entry does not exist, so create
        if ( entry == null ) {
            entry = new FieldIndexEntry( this.index,
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

    public static class FieldIndexEntry
        implements
        Entry {

        private static final long serialVersionUID = 400L;
        private Entry             next;
        private LeftTuple         first;
        private int         hashCode;
        private Index             index;

        public FieldIndexEntry() {

        }
        public FieldIndexEntry(final Index index,
                               final int hashCode) {
            this.index = index;
            this.hashCode = hashCode;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            next    = (Entry)in.readObject();
            first   = (LeftTuple)in.readObject();
            hashCode    = in.readInt();
            index   = (Index)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(next);
            out.writeObject(first);
            out.writeInt(hashCode);
            out.writeObject(index);
        }

        public Entry getNext() {
            return this.next;
        }

        public void setNext(final Entry next) {
            this.next = next;
        }

        public LeftTuple getFirst() {
            return this.first;
        }

        public void add(final LeftTuple tuple) {
            tuple.setNext( this.first );
            this.first = tuple;
        }

        public LeftTuple get(final LeftTuple tuple) {
            LeftTuple current = this.first;
            while ( current != null ) {
                if ( tuple.equals( current ) ) {
                    return current;
                }
                current = (LeftTuple) current.getNext();
            }
            return null;
        }

        public LeftTuple remove(final LeftTuple tuple) {
            LeftTuple previous = this.first;
            LeftTuple current = previous;
            while ( current != null ) {
                final LeftTuple next = (LeftTuple) current.getNext();
                if ( tuple.equals( current ) ) {
                    if ( this.first == current ) {
                        this.first = next;
                    } else {
                        previous.setNext( next );
                    }
                    current.setNext( null );
                    return current;
                }
                previous = current;
                current = next;
            }
            return current;
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
            final FieldIndexEntry other = (FieldIndexEntry) object;
            return this.hashCode == other.hashCode && this.index == other.index;
        }
    }

}