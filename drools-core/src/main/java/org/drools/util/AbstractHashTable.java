/**
 * 
 */
package org.drools.util;

import java.io.Serializable;

import org.drools.common.InternalFactHandle;

public abstract class AbstractHashTable
    implements
    Serializable {
    static final int           MAX_CAPACITY = 1 << 30;

    protected int              size;
    protected int              threshold;
    protected float            loadFactor;

    protected ObjectComparator comparator;

    protected Entry[]          table;

    private HashTableIterator  iterator;

    public AbstractHashTable() {
        this( 16,
              0.75f );
    }

    public AbstractHashTable(final int capacity,
                             final float loadFactor) {
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.table = new Entry[capacity];
        this.comparator = EqualityEquals.getInstance();
    }

    public Iterator iterator() {
        if ( this.iterator == null ) {
            this.iterator = new HashTableIterator( this );
        }

        this.iterator.reset();
        return this.iterator;
    }

    public void setComparator(final ObjectComparator comparator) {
        this.comparator = comparator;
    }

    protected void resize(final int newCapacity) {
        final Entry[] oldTable = this.table;
        final int oldCapacity = oldTable.length;
        if ( oldCapacity == AbstractHashTable.MAX_CAPACITY ) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }

        final Entry[] newTable = new Entry[newCapacity];

        for ( int i = 0; i < this.table.length; i++ ) {
            Entry entry = this.table[i];
            if ( entry == null ) {
                continue;
            }
            this.table[i] = null;
            Entry next = null;
            while ( entry != null ) {
                next = entry.getNext();

                final int index = indexOf( entry.hashCode(),
                                     newTable.length );
                entry.setNext( newTable[index] );
                newTable[index] = entry;

                entry = next;
            }
        }

        this.table = newTable;
        this.threshold = (int) (newCapacity * this.loadFactor);
    }

    //    public void add(Entry entry) {
    //        int index = indexOf( entry.hashCode(), table.length  );
    //
    //        
    //        boolean exists = false;
    //        
    //        // scan the linked entries to see if it exists
    //        if ( !checkExists ) {
    //            Entry current = this.table[index];
    //            int hashCode = entry.hashCode();
    //            while ( current != null ) {                
    //                if  ( hashCode == current.hashCode() && entry.equals( current ) ) {
    //                    exists = true;
    //                }
    //            }                        
    //        }
    //        
    //        if( exists == false ) {
    //            entry.setNext( this.table[index] );
    //            this.table[index] = entry;
    //    
    //            if ( this.size++ >= this.threshold ) {
    //                resize( 2 * this.table.length );
    //            }
    //        }
    //
    //    }
    //
    //    public Entry get(Entry entry) {
    //        int index = indexOf( entry.hashCode(), table.length  );
    //        Entry current = this.table[index];
    //        while ( current != null ) {
    //            if ( entry.hashCode() == current.hashCode() && entry.equals( current ) ) {
    //                return current;
    //            }
    //            current = current.getNext();
    //        }
    //        return null;
    //    }
    //
    //    public Entry remove(Entry entry) {
    //        int index = indexOf( entry.hashCode(), table.length  );
    //        Entry previous = this.table[index];        
    //        Entry current = previous;
    //        int hashCode = entry.hashCode();
    //        while ( current != null ) {
    //            Entry next = current.getNext();
    //            if ( hashCode == current.hashCode() && entry.equals( current ) ) {
    //                if( previous  == current ) {
    //                    this.table[index] = next;
    //                    previous.setNext( next );
    //                }
    //                current.setNext( null );
    //                this.size--;
    //                return current;
    //            }
    //            previous = current;
    //            current = next;
    //        }
    //        return current;
    //    }

    public Entry getBucket(final int hashCode) {
        return this.table[indexOf( hashCode,
                                   this.table.length )];
    }

    public Entry[] getTable() {
        return this.table;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    //    protected int indexOf(int hashCode,
    //                          int dataSize) {
    //        int index = hashCode % dataSize;
    //        if ( index < 0 ) {
    //            index = index * -1;
    //        }
    //        return index;
    //    }

    protected int indexOf(final int hashCode,
                          final int dataSize) {
        return hashCode & (dataSize - 1);
    }

    public abstract Entry getBucket(Object object);

    public interface ObjectComparator {
        public int hashCodeOf(Object object);

        public int rehash(int hashCode);

        public boolean equal(Object object1,
                             Object object2);
    }

    /**
     * Fast re-usable iterator
     *
     */
    public static class HashTableIterator
        implements
        Iterator {
        private AbstractHashTable hashTable;
        private Entry[]           table;
        private int               row;
        private int               length;
        private Entry             entry;

        public HashTableIterator(final AbstractHashTable hashTable) {
            this.hashTable = hashTable;
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#next()
         */
        public Entry next() {
            if ( this.entry == null ) {
                // keep skipping rows until we come to the end, or find one that is populated
                while ( this.entry == null ) {
                    this.row++;
                    if ( this.row == this.length ) {
                        return null;
                    }
                    this.entry = this.table[this.row];
                }
            } else {
                this.entry = this.entry.getNext();
                if ( this.entry == null ) {
                    this.entry = next();
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

    public static class InstanceEquals
        implements
        ObjectComparator {
        public static ObjectComparator INSTANCE = new InstanceEquals();

        public static ObjectComparator getInstance() {
            return InstanceEquals.INSTANCE;
        }

        public int hashCodeOf(final Object key) {
            return rehash( key.hashCode() );
        }

        public int rehash(int h) {
            h += ~(h << 9);
            h ^= (h >>> 14);
            h += (h << 4);
            h ^= (h >>> 10);
            return h;
        }

        private InstanceEquals() {

        }

        public boolean equal(final Object object1,
                             final Object object2) {
            return object1 == object2;
        }
    }

    public static class EqualityEquals
        implements
        ObjectComparator {
        public static ObjectComparator INSTANCE = new EqualityEquals();

        public static ObjectComparator getInstance() {
            return EqualityEquals.INSTANCE;
        }

        public int hashCodeOf(final Object key) {
            return rehash( key.hashCode() );
        }

        public int rehash(int h) {
            h += ~(h << 9);
            h ^= (h >>> 14);
            h += (h << 4);
            h ^= (h >>> 10);
            return h;
        }

        private EqualityEquals() {

        }

        public boolean equal(final Object object1,
                             final Object object2) {
            return object1.equals( object2 );
        }
    }

    public static class FactEntry
        implements
        Entry {
        public InternalFactHandle handle;

        public int                hashCode;

        public Entry              next;

        //        private LinkedList              list;

        public FactEntry(final InternalFactHandle handle) {
            this.handle = handle;
            this.hashCode = handle.hashCode();
            //            this.list = new LinkedList();
        }

        public FactEntry(final InternalFactHandle handle,
                         final int hashCode) {
            this.handle = handle;
            this.hashCode = hashCode;
            //            this.list = new LinkedList();
        }

        public InternalFactHandle getFactHandle() {
            return this.handle;
        }

        public Entry getNext() {
            return this.next;
        }

        public void setNext(final Entry next) {
            this.next = next;
        }

        //        
        //        void add(final LinkedListEntry tupleMatchEntry) {
        //            this.list.add( tupleMatchEntry );
        //        }
        //        void remove(final LinkedListEntry tupleMatchEntry) {
        //            this.list.remove( tupleMatchEntry );
        //        }        

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(final Object object) {
            return (object == this) || (this.handle == ((FactEntry) object).handle);
        }
    }
}