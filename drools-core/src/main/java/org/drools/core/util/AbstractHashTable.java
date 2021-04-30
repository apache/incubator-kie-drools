/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

import org.drools.core.rule.Declaration;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ReadAccessor;
import org.drools.core.spi.Tuple;
import org.drools.core.spi.TupleValueExtractor;
import org.drools.core.util.index.TupleList;

public abstract class AbstractHashTable
    implements
    Externalizable {
    static final int           MAX_CAPACITY = 1 << 30;

    public static final int                           PRIME            = 31;

    protected int              size;
    protected int              threshold;
    protected float            loadFactor;

    protected ObjectComparator comparator;

    protected Entry<TupleList>[] table;

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
        this( 0.75f,
              table );
    }

    public AbstractHashTable(final float loadFactor,
                             final Entry[] table) {
        this.loadFactor = loadFactor;
        this.threshold = (int) (table.length * loadFactor);
        this.table = table;
        this.comparator = EqualityEquals.getInstance();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        size = in.readInt();
        threshold = in.readInt();
        loadFactor = in.readFloat();
        comparator = (ObjectComparator) in.readObject();
        table = (Entry[]) in.readObject();
        iterator = (HashTableIterator) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt( size );
        out.writeInt( threshold );
        out.writeFloat( loadFactor );
        out.writeObject( comparator );
        out.writeObject( table );
        out.writeObject( iterator );
    }

    public Iterator iterator() {
        if ( this.iterator == null ) {
            this.iterator = new HashTableIterator( this );
        } else {
            this.iterator.reset();
        }
        return this.iterator;
    }

    public Iterator newIterator() {
        return new HashTableIterator( this );
    }

    public void setComparator(final ObjectComparator comparator) {
        this.comparator = comparator;
    }

    public void ensureCapacity(int itemsToBeAdded) {
        int newCapacity = this.size + itemsToBeAdded;
        if (newCapacity > this.threshold) {
            int newSize = this.table.length * 2;
            while (newSize < newCapacity) {
                newSize *= 2;
            }
            resize(newSize);
        }
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
            while ( entry != null ) {
                Entry next = entry.getNext();
                                
                // we must use getResizeHashcode as some sub classes cache the hashcode and some don't
                // otherwise we end up rehashing a cached hashcode that has already been rehashed.
                final int index = indexOf(  getResizeHashcode( entry ),
                                            newTable.length );
                
                entry.setNext( newTable[index] );
                newTable[index] = entry;

                entry = next;
            }
        }

        this.table = newTable;
        this.threshold = (int) (newCapacity * this.loadFactor);
    }
    
    public abstract int getResizeHashcode(Entry entry);

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

    public Entry<TupleList>[] getTable() {
        return this.table;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public static int rehash(int hash) {
        hash ^= (hash >>> 20) ^ (hash >>> 12);
        return hash ^ (hash >>> 7) ^ (hash >>> 4);
    }     

    protected static int indexOf(final int hashCode,
                          final int dataSize) {
        return hashCode & (dataSize - 1);
    }      

    public interface ObjectComparator extends Externalizable {
        int hashCodeOf(Object object);
        boolean areEqual(Object object1, Object object2);
    }
    
    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder();
        Iterator it = newIterator();
        boolean isFirst = true;
        for (Entry entry = ( Entry ) it.next(); entry != null; entry = ( Entry ) it.next() ) {
            sbuilder.append( entry.toString() );
            if ( !isFirst ) {
                sbuilder.append( ", " );
            }
            isFirst = false;
        }
        
        return sbuilder.toString();
    }

    public abstract static class AbstractObjectComparator implements ObjectComparator {
    }

    public static class InstanceEquals
        extends
        AbstractObjectComparator {

        private static final long            serialVersionUID = 510l;
        public static final ObjectComparator INSTANCE         = new InstanceEquals();

        @Override
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public static ObjectComparator getInstance() {
            return InstanceEquals.INSTANCE;
        }

        public InstanceEquals() {

        }
        
        @Override
        public int hashCodeOf(final Object obj) {
            return rehash( System.identityHashCode( obj ) );
        }        

        @Override
        public boolean areEqual(final Object object1,
                                final Object object2) {
            return object1 == object2;
        }
    }

    public static class EqualityEquals
        extends
        AbstractObjectComparator {

        private static final long            serialVersionUID = 510l;
        public static final ObjectComparator INSTANCE         = new EqualityEquals();

        @Override
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public static ObjectComparator getInstance() {
            return EqualityEquals.INSTANCE;
        }

        public EqualityEquals() {

        }
        
        @Override
        public int hashCodeOf(final Object key) {
            return rehash( key.hashCode() );
        }        

        @Override
        public boolean areEqual(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 == null;
            }
            return object1.equals( object2 );
        }
    }

    public static class FieldIndex implements Externalizable {

        private static final long serialVersionUID = 510l;

        private TupleValueExtractor leftExtractor;
        private InternalReadAccessor rightExtractor;
        private boolean requiresCoercion;

        public FieldIndex() { }

        public FieldIndex( InternalReadAccessor rightExtractor, TupleValueExtractor leftExtractor ) {
            this.rightExtractor = rightExtractor;
            this.leftExtractor = leftExtractor;
            this.requiresCoercion = isCoercionRequired( rightExtractor, leftExtractor );
        }

        private boolean isCoercionRequired( InternalReadAccessor extractor, TupleValueExtractor declaration ) {
            return extractor.getValueType() != declaration.getValueType();
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            rightExtractor = (InternalReadAccessor) in.readObject();
            leftExtractor = (Declaration) in.readObject();
            requiresCoercion = isCoercionRequired( rightExtractor, leftExtractor );
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( rightExtractor );
            out.writeObject( leftExtractor );
        }

        public TupleValueExtractor getLeftExtractor() {
            return this.leftExtractor;
        }

        public ReadAccessor getRightExtractor() {
            return this.rightExtractor;
        }

        public boolean requiresCoercion() {
            return requiresCoercion;
        }

        public Object indexedValueOf(Tuple tuple, boolean left) {
            return left ?
                    ( requiresCoercion ?
                            rightExtractor.getValueType().coerce( leftExtractor.getValue( tuple ) ) :
                            leftExtractor.getValue( tuple ) ) :
                   rightExtractor.getValue( null, tuple.getFactHandle().getObject() );
        }
    }

    public interface Index extends Externalizable {
        FieldIndex getFieldIndex(int index);
        HashEntry hashCodeOf(Tuple tuple, boolean left);
    }

    public static class SingleIndex implements Index {

        private static final long    serialVersionUID = 510l;

        private FieldIndex           index;

        private int                  startResult;

        public SingleIndex() {

        }

        public SingleIndex(final FieldIndex[] indexes,
                           final int startResult) {
            this.startResult = startResult;
            this.index = indexes[0];
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            index = (FieldIndex) in.readObject();
            startResult = in.readInt();
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( index );
            out.writeInt( startResult );
        }

        @Override
        public FieldIndex getFieldIndex(int index) {
            if ( index > 0 ) {
                throw new IllegalArgumentException( "IndexUtil position " + index + " does not exist" );
            }
            return this.index;
        }

        @Override
        public HashEntry hashCodeOf(Tuple tuple, boolean left) {
            return new SingleHashEntry( startResult, index.indexedValueOf( tuple, left ) );
        }
    }

    public static class IndexTupleList extends TupleList {
        private HashEntry hashEntry;
        private Index index;
        private int hashCode;

        public IndexTupleList( Index index, HashEntry hashEntry ) {
            this.index = index;
            this.hashEntry = hashEntry;
            this.hashCode = hashEntry.hashCode();
        }

        @Override
        public boolean equals(final Object object) {
            if (!(object instanceof IndexTupleList)) {
                return false;
            }
            final IndexTupleList other = (IndexTupleList) object;
            return this.hashCode == other.hashCode && this.index == other.index;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        protected void copyStateInto(TupleList other) {
            super.copyStateInto( other );
            ( (IndexTupleList) other ).hashEntry = hashEntry;
            ( (IndexTupleList) other ).index = index;
            ( (IndexTupleList) other ).hashCode = hashCode;
        }

        public HashEntry getHashEntry() {
            return hashEntry;
        }
    }

    public static class DoubleCompositeIndex implements Index {

        private static final long serialVersionUID = 510l;

        private FieldIndex index1;
        private FieldIndex index2;

        private int startResult;

        public DoubleCompositeIndex() {

        }

        public DoubleCompositeIndex(final FieldIndex[] indexes,
                                    final int startResult) {
            this.startResult = startResult;

            this.index1 = indexes[0];
            this.index2 = indexes[1];
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            index1 = (FieldIndex) in.readObject();
            index2 = (FieldIndex) in.readObject();
            startResult = in.readInt();
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( index1 );
            out.writeObject( index2 );
            out.writeInt( startResult );
        }

        @Override
        public FieldIndex getFieldIndex(int index) {
            switch ( index ) {
                case 0 :
                    return index1;
                case 1 :
                    return index2;
                default :
                    throw new IllegalArgumentException( "IndexUtil position " + index + " does not exist" );
            }
        }

        @Override
        public HashEntry hashCodeOf(Tuple tuple, boolean left) {
            return new DoubleHashEntry( startResult, index1.indexedValueOf( tuple, left ), index2.indexedValueOf( tuple, left ) );
        }
    }

    public static class TripleCompositeIndex implements Index {

        private static final long serialVersionUID = 510l;

        private FieldIndex index1;
        private FieldIndex index2;
        private FieldIndex index3;

        private int               startResult;

        public TripleCompositeIndex() {

        }

        public TripleCompositeIndex(final FieldIndex[] indexes,
                                    final int startResult) {
            this.startResult = startResult;

            this.index1 = indexes[0];
            this.index2 = indexes[1];
            this.index3 = indexes[2];
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            index1 = (FieldIndex) in.readObject();
            index2 = (FieldIndex) in.readObject();
            index3 = (FieldIndex) in.readObject();
            startResult = in.readInt();
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( index1 );
            out.writeObject( index2 );
            out.writeObject( index3 );
            out.writeInt( startResult );
        }

        @Override
        public FieldIndex getFieldIndex(int index) {
            switch ( index ) {
                case 0 :
                    return index1;
                case 1 :
                    return index2;
                case 2 :
                    return index3;
                default :
                    throw new IllegalArgumentException( "IndexUtil position " + index + " does not exist" );
            }
        }

        @Override
        public HashEntry hashCodeOf(Tuple tuple, boolean left) {
            return new TripleHashEntry( startResult, index1.indexedValueOf( tuple, left ), index2.indexedValueOf( tuple, left ), index3.indexedValueOf( tuple, left ) );
        }
    }

    public void clear() {
        this.table = new Entry[Math.min( this.table.length,
                                         16 )];
        this.threshold = (int) (this.table.length * this.loadFactor);
        this.size = 0;
        this.iterator = null;
    }

    public interface HashEntry { }

    public static class SingleHashEntry implements HashEntry {

        private final int hashCode;
        private final Object obj1;

        public SingleHashEntry(int hashSeed, Object obj1) {
            this.obj1 = obj1;
            this.hashCode = rehash( PRIME * hashSeed + Objects.hashCode( obj1 ) );
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            SingleHashEntry that = ( SingleHashEntry ) o;
            return hashCode == that.hashCode && Objects.equals( obj1, that.obj1 );
        }
    }

    public static class DoubleHashEntry implements HashEntry {

        private final int hashCode;
        private final Object obj1;
        private final Object obj2;

        public DoubleHashEntry(int hashSeed, Object obj1, Object obj2) {
            this.obj1 = obj1;
            this.obj2 = obj2;
            this.hashCode = hashCodeOf(hashSeed, obj1, obj2);
        }

        private int hashCodeOf(int hashSeed, Object obj1, Object obj2) {
            int hashCode = hashSeed;
            hashCode = PRIME * hashCode + Objects.hashCode( obj1 );
            hashCode = PRIME * hashCode + Objects.hashCode( obj2 );
            return rehash( hashCode );
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            DoubleHashEntry that = ( DoubleHashEntry ) o;
            return hashCode == that.hashCode && Objects.equals( obj1, that.obj1 ) && Objects.equals( obj2, that.obj2 );
        }
    }

    public static class TripleHashEntry implements HashEntry {

        private final int hashCode;
        private final Object obj1;
        private final Object obj2;
        private final Object obj3;

        public TripleHashEntry(int hashSeed, Object obj1, Object obj2, Object obj3) {
            this.obj1 = obj1;
            this.obj2 = obj2;
            this.obj3 = obj3;
            this.hashCode = hashCodeOf(hashSeed, obj1, obj2, obj3);
        }

        private int hashCodeOf(int hashSeed, Object obj1, Object obj2, Object obj3) {
            int hashCode = hashSeed;
            hashCode = PRIME * hashCode + Objects.hashCode( obj1 );
            hashCode = PRIME * hashCode + Objects.hashCode( obj2 );
            hashCode = PRIME * hashCode + Objects.hashCode( obj3 );
            return rehash( hashCode );
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            TripleHashEntry that = ( TripleHashEntry ) o;
            return hashCode == that.hashCode && Objects.equals( obj1, that.obj1 ) && Objects.equals( obj2, that.obj2 ) && Objects.equals( obj3, that.obj3 );
        }
    }
}
