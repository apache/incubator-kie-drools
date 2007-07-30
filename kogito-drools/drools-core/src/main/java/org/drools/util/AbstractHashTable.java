/**
 * 
 */
package org.drools.util;

import java.io.Serializable;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;

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
    
    public AbstractHashTable(final Entry[] table) {
        this( 0.75f, table);
    }      
    
    public AbstractHashTable(final float loadFactor,
                             final Entry[] table) {
        this.loadFactor = loadFactor;
        this.threshold = (int) (table.length * loadFactor);
        this.table = table;
        this.comparator = EqualityEquals.getInstance();
    }    

    public Iterator iterator() {
        if ( this.iterator == null ) {
            this.iterator = new HashTableIterator( this );
        }

        this.iterator.reset();
        return this.iterator;
    }
    
    public Iterator newIterator() {
        HashTableIterator iterator = new HashTableIterator( this );
        iterator.reset();
        return iterator;
        
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
    
    public Entry[] toArray() {
        Entry[] result = new Entry[this.size];
        int index = 0;
        for ( int i = 0; i < this.table.length; i++ ) {
            Entry entry = this.table[i];
            while ( entry != null ) {
                result[index++] = entry;
                entry = entry.getNext();
            }
        }
        return result;
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

    protected Entry getBucket(final int hashCode) {
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

    public interface ObjectComparator
        extends
        Serializable {
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

        private static final long serialVersionUID = 400L;

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
        public Object next() {
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

    public static class InstanceEquals
        implements
        ObjectComparator {

        private static final long      serialVersionUID = 400L;
        public static ObjectComparator INSTANCE         = new InstanceEquals();

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

        private static final long      serialVersionUID = 400L;
        public static ObjectComparator INSTANCE         = new EqualityEquals();

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
            if ( object1 == null ) {
                return object2 == null;
            }
            return object1.equals( object2 );
        }
    }

    public static class FactEntryImpl
        implements
        FactEntry,
        Entry {

        private static final long serialVersionUID = 400L;

        public InternalFactHandle handle;

        public int                hashCode;

        public Entry              next;

        //        private LinkedList              list;

        public FactEntryImpl(final InternalFactHandle handle) {
            this.handle = handle;
            this.hashCode = handle.hashCode();
            //            this.list = new LinkedList();
        }

        public FactEntryImpl(final InternalFactHandle handle,
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
            return (object == this) || (this.handle == ((FactEntryImpl) object).handle);
        }

        public String toString() {
            return "FactEntry( handle=" + this.handle + " hashcode=" + this.hashCode + " next=" + this.next + " )";
        }
    }

    public static class FieldIndex {
        FieldExtractor   extractor;
        Declaration      declaration;
        public Evaluator evaluator;

        public FieldIndex(final FieldExtractor extractor,
                          final Declaration declaration,
                          final Evaluator evaluator) {
            super();
            this.extractor = extractor;
            this.declaration = declaration;
            this.evaluator = evaluator;
        }

        public Declaration getDeclaration() {
            return this.declaration;
        }

        public FieldExtractor getExtractor() {
            return this.extractor;
        }

        public Evaluator getEvaluator() {
            return this.evaluator;
        }
    }

    public static interface Index {
        public int hashCodeOf(ReteTuple tuple);

        public int hashCodeOf(Object object);

        public boolean equal(Object object,
                             ReteTuple tuple);

        public boolean equal(ReteTuple tuple1,
                             ReteTuple tuple2);

        public boolean equal(Object object1,
                             Object object2);
    }

    public static class SingleIndex
        implements
        Index {

        private FieldExtractor extractor;
        private Declaration    declaration;
        private Evaluator      evaluator;

        private int            startResult;

        public SingleIndex(final FieldIndex[] indexes,
                           final int startResult) {
            this.startResult = startResult;

            this.extractor = indexes[0].extractor;
            this.declaration = indexes[0].declaration;
            this.evaluator = indexes[0].evaluator;

        }

        public int hashCodeOf(final Object object) {
            int hashCode = this.startResult;
            hashCode = TupleIndexHashTable.PRIME * hashCode + this.extractor.getHashCode( null, object );
            return rehash( hashCode );
        }

        public int hashCodeOf(final ReteTuple tuple) {
            int hashCode = this.startResult;
            hashCode = TupleIndexHashTable.PRIME * hashCode + this.declaration.getHashCode( null, tuple.get( this.declaration ).getObject() );
            return rehash( hashCode );
        }

        public boolean equal(final Object right,
                             final ReteTuple tuple) {
            final Object left = tuple.get( this.declaration ).getObject();

            return this.evaluator.evaluate( null,
                                            this.declaration.getExtractor(),
                                            left,
                                            this.extractor, right );
        }

        public boolean equal(final Object object1,
                             final Object object2) {

            return this.evaluator.evaluate( null,
                                            this.extractor,
                                            object1,
                                            this.extractor, object2 );
        }

        public boolean equal(final ReteTuple tuple1,
                             final ReteTuple tuple2) {
            final Object object1 = tuple1.get( this.declaration ).getObject();
            final Object object2 = tuple2.get( this.declaration ).getObject();
            return this.evaluator.evaluate( null,
                                            this.declaration.getExtractor(),
                                            object1,
                                            this.declaration.getExtractor(), object2 );
        }

        public int rehash(int h) {
            h += ~(h << 9);
            h ^= (h >>> 14);
            h += (h << 4);
            h ^= (h >>> 10);
            return h;
        }

    }

    public static class DoubleCompositeIndex
        implements
        Index {
        private FieldIndex index0;
        private FieldIndex index1;

        private int        startResult;

        public DoubleCompositeIndex(final FieldIndex[] indexes,
                                    final int startResult) {
            this.startResult = startResult;

            this.index0 = indexes[0];
            this.index1 = indexes[1];

        }

        public int hashCodeOf(final Object object) {
            int hashCode = this.startResult;

            hashCode = TupleIndexHashTable.PRIME * hashCode + this.index0.extractor.getHashCode( null, object );
            hashCode = TupleIndexHashTable.PRIME * hashCode + this.index1.extractor.getHashCode( null, object );

            return rehash( hashCode );
        }

        public int hashCodeOf(final ReteTuple tuple) {
            int hashCode = this.startResult;

            hashCode = TupleIndexHashTable.PRIME * hashCode + this.index0.declaration.getHashCode( null, tuple.get( this.index0.declaration ).getObject() );
            hashCode = TupleIndexHashTable.PRIME * hashCode + this.index1.declaration.getHashCode( null, tuple.get( this.index1.declaration ).getObject() );

            return rehash( hashCode );
        }

        public boolean equal(final Object right,
                             final ReteTuple tuple) {
            final Object left1 = tuple.get( this.index0.declaration ).getObject();
            final Object left2 = tuple.get( this.index1.declaration ).getObject();

            return this.index0.evaluator.evaluate( null,
                                                   this.index0.declaration.getExtractor(),
                                                   left1,
                                                   this.index0.extractor, right ) && this.index1.evaluator.evaluate( null,
                                                                                              this.index1.declaration.getExtractor(),
                                                                                              left2,
                                                                                              this.index1.extractor, right );
        }

        public boolean equal(final ReteTuple tuple1,
                             final ReteTuple tuple2) {
            final Object object11 = tuple1.get( this.index0.declaration ).getObject();
            final Object object12 = tuple2.get( this.index0.declaration ).getObject();

            final Object object21 = tuple1.get( this.index1.declaration ).getObject();
            final Object object22 = tuple2.get( this.index1.declaration ).getObject();

            return this.index0.evaluator.evaluate( null,
                                                   this.index0.declaration.getExtractor(),
                                                   object11,
                                                   this.index0.declaration.getExtractor(), object12 ) && this.index1.evaluator.evaluate( null,
                                                                                                 this.index1.declaration.getExtractor(),
                                                                                                 object21,
                                                                                                 this.index1.declaration.getExtractor(), object22 );
        }

        public boolean equal(final Object object1,
                             final Object object2) {
            return this.index0.evaluator.evaluate( null,
                                                   this.index0.extractor,
                                                   object1,
                                                   this.index0.extractor, object2 ) && this.index1.evaluator.evaluate( null,
                                                                                                this.index1.extractor,
                                                                                                object1,
                                                                                                this.index1.extractor, object2 );
        }

        public int rehash(int h) {
            h += ~(h << 9);
            h ^= (h >>> 14);
            h += (h << 4);
            h ^= (h >>> 10);
            return h;
        }
    }

    public static class TripleCompositeIndex
        implements
        Index {
        private FieldIndex index0;
        private FieldIndex index1;
        private FieldIndex index2;

        private int        startResult;

        public TripleCompositeIndex(final FieldIndex[] indexes,
                                    final int startResult) {
            this.startResult = startResult;

            this.index0 = indexes[0];
            this.index1 = indexes[1];
            this.index2 = indexes[2];

        }

        public int hashCodeOf(final Object object) {
            int hashCode = this.startResult;

            hashCode = TupleIndexHashTable.PRIME * hashCode + this.index0.extractor.getHashCode( null, object );;
            hashCode = TupleIndexHashTable.PRIME * hashCode + this.index1.extractor.getHashCode( null, object );;
            hashCode = TupleIndexHashTable.PRIME * hashCode + this.index2.extractor.getHashCode( null, object );;

            return rehash( hashCode );
        }

        public int hashCodeOf(final ReteTuple tuple) {
            int hashCode = this.startResult;

            hashCode = TupleIndexHashTable.PRIME * hashCode + this.index0.declaration.getHashCode( null, tuple.get( this.index0.declaration ).getObject() );
            hashCode = TupleIndexHashTable.PRIME * hashCode + this.index1.declaration.getHashCode( null, tuple.get( this.index1.declaration ).getObject() );
            hashCode = TupleIndexHashTable.PRIME * hashCode + this.index2.declaration.getHashCode( null, tuple.get( this.index2.declaration ).getObject() );

            return rehash( hashCode );
        }

        public boolean equal(final Object right,
                             final ReteTuple tuple) {
            final Object left1 = tuple.get( this.index0.declaration ).getObject();
            final Object left2 = tuple.get( this.index1.declaration ).getObject();
            final Object left3 = tuple.get( this.index2.declaration ).getObject();

            return this.index0.evaluator.evaluate( null,
                                                   this.index0.declaration.getExtractor(),
                                                   left1,
                                                   this.index0.extractor, right ) && this.index1.evaluator.evaluate( null,
                                                                                              this.index1.declaration.getExtractor(),
                                                                                              left2,
                                                                                              this.index1.extractor, right ) && this.index2.evaluator.evaluate( null,
                                                                                                                                         this.index2.declaration.getExtractor(),
                                                                                                                                         left3,
                                                                                                                                         this.index2.extractor, right );
        }

        public boolean equal(final ReteTuple tuple1,
                             final ReteTuple tuple2) {
            final Object object11 = tuple1.get( this.index0.declaration ).getObject();
            final Object object12 = tuple2.get( this.index0.declaration ).getObject();
            final Object object21 = tuple1.get( this.index1.declaration ).getObject();
            final Object object22 = tuple2.get( this.index1.declaration ).getObject();
            final Object object31 = tuple1.get( this.index2.declaration ).getObject();
            final Object object32 = tuple2.get( this.index2.declaration ).getObject();

            return this.index0.evaluator.evaluate( null,
                                                   this.index0.declaration.getExtractor(),
                                                   object11,
                                                   this.index0.declaration.getExtractor(), object12 ) && this.index1.evaluator.evaluate( null,
                                                                                                 this.index1.declaration.getExtractor(),
                                                                                                 object21,
                                                                                                 this.index1.declaration.getExtractor(), object22 ) && this.index2.evaluator.evaluate( null,
                                                                                                                                               this.index2.declaration.getExtractor(),
                                                                                                                                               object31,
                                                                                                                                               this.index2.declaration.getExtractor(), object32 );
        }

        public boolean equal(final Object object1,
                             final Object object2) {
            return this.index0.evaluator.evaluate( null,
                                                   this.index0.extractor,
                                                   object1,
                                                   this.index0.extractor, object2 ) && this.index1.evaluator.evaluate( null,
                                                                                                this.index1.extractor,
                                                                                                object1,
                                                                                                this.index1.extractor, object2 ) && this.index2.evaluator.evaluate( null,
                                                                                                                                             this.index2.extractor,
                                                                                                                                             object1,
                                                                                                                                             this.index2.extractor, object2 );
        }

        public int rehash(int h) {
            h += ~(h << 9);
            h ^= (h >>> 14);
            h += (h << 4);
            h ^= (h >>> 10);
            return h;
        }

    }
}