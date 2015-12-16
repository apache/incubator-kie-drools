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

import org.drools.core.rule.Declaration;
import org.drools.core.rule.IndexEvaluator;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ReadAccessor;
import org.drools.core.spi.Tuple;
import org.drools.core.util.index.TupleList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

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

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        size = in.readInt();
        threshold = in.readInt();
        loadFactor = in.readFloat();
        comparator = (ObjectComparator) in.readObject();
        table = (Entry[]) in.readObject();
        iterator = (HashTableIterator) in.readObject();
    }

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
        HashTableIterator iterator = new HashTableIterator( this );
        iterator.reset();
        return iterator;

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
            Entry next = null;
            while ( entry != null ) {
                next = entry.getNext();
                                
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
        boolean equal(Object object1, Object object2);
    }
    
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

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public static ObjectComparator getInstance() {
            return InstanceEquals.INSTANCE;
        }

        public InstanceEquals() {

        }
        
        public int hashCodeOf(final Object obj) {
            return rehash( System.identityHashCode( obj ) );
        }        

        public boolean equal(final Object object1,
                             final Object object2) {
            return object1 == object2;
        }
    }

    public static class EqualityEquals
        extends
        AbstractObjectComparator {

        private static final long            serialVersionUID = 510l;
        public static final ObjectComparator INSTANCE         = new EqualityEquals();

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public static ObjectComparator getInstance() {
            return EqualityEquals.INSTANCE;
        }

        public EqualityEquals() {

        }
        
        public int hashCodeOf(final Object key) {
            return rehash( key.hashCode() );
        }        

        public boolean equal(final Object object1,
                             final Object object2) {
            if ( object1 == null ) {
                return object2 == null;
            }
            return object1.equals( object2 );
        }
    }

    public static class FieldIndex
        implements
        Externalizable {

        private static final long serialVersionUID = 510l;

        private InternalReadAccessor    extractor;
        private Declaration             declaration;
        private IndexEvaluator          evaluator;

        public FieldIndex() {

        }

        public FieldIndex(final InternalReadAccessor extractor,
                          final Declaration declaration,
                          final IndexEvaluator evaluator) {
            super();
            this.extractor = extractor;
            this.declaration = declaration;
            this.evaluator = evaluator;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            extractor = (InternalReadAccessor) in.readObject();
            declaration = (Declaration) in.readObject();
            evaluator = (IndexEvaluator) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( extractor );
            out.writeObject( declaration );
            out.writeObject( evaluator );
        }

        public Declaration getDeclaration() {
            return this.declaration;
        }

        public ReadAccessor getExtractor() {
            return this.extractor;
        }

        public IndexEvaluator getEvaluator() {
            return this.evaluator;
        }

        public int hashCodeOf(Tuple tuple, boolean left) {
            return left ?
                   declaration.getHashCode( null, tuple.getObject( declaration ) ) :
                   extractor.getHashCode( null, tuple.getFactHandle().getObject() );
        }
    }

    public interface Index extends Externalizable {
        FieldIndex getFieldIndex(int index);

        int hashCodeOf(Tuple tuple, boolean left);

        boolean equal(Object object, Tuple tuple);

        boolean equal(Tuple tuple1, Tuple tuple2);

        boolean equal(Object object1, Object object2);
    }

    public static class SingleIndex
        implements
        Index {

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

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            index = (FieldIndex) in.readObject();
            startResult = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( index );
            out.writeInt( startResult );
        }

        public FieldIndex getFieldIndex(int index) {
            if ( index > 0 ) {
                throw new IllegalArgumentException( "IndexUtil position " + index + " does not exist" );
            }
            return this.index;
        }

        public int hashCodeOf(final Tuple tuple, boolean left) {
            return rehash( PRIME * startResult + index.hashCodeOf( tuple, left ) );
        }

        public boolean equal(final Object right,
                             final Tuple tuple) {
            return this.index.evaluator.evaluate( null,
                                                  this.index.declaration.getExtractor(),
                                                  tuple.getObject( this.index.declaration ),
                                                  this.index.extractor,
                                                  right );
        }

        public boolean equal(final Object object1,
                             final Object object2) {
            return this.index.evaluator.evaluate( null,
                                                  this.index.extractor,
                                                  object1,
                                                  this.index.extractor,
                                                  object2 );
        }

        public boolean equal(final Tuple tuple1,
                             final Tuple tuple2) {
            return this.index.evaluator.evaluate( null,
                                                  this.index.declaration.getExtractor(),
                                                  tuple1.getObject( this.index.declaration ),
                                                  this.index.declaration.getExtractor(),
                                                  tuple2.getObject( this.index.declaration ) );
        }
    }

    public static class DoubleCompositeIndex
        implements
        Index {

        private static final long serialVersionUID = 510l;

        private FieldIndex        index0;
        private FieldIndex        index1;

        private int               startResult;

        public DoubleCompositeIndex() {

        }

        public DoubleCompositeIndex(final FieldIndex[] indexes,
                                    final int startResult) {
            this.startResult = startResult;

            this.index0 = indexes[0];
            this.index1 = indexes[1];
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            index0 = (FieldIndex) in.readObject();
            index1 = (FieldIndex) in.readObject();
            startResult = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( index0 );
            out.writeObject( index1 );
            out.writeInt( startResult );
        }

        public FieldIndex getFieldIndex(int index) {
            switch ( index ) {
                case 0 :
                    return index0;
                case 1 :
                    return index1;
                default :
                    throw new IllegalArgumentException( "IndexUtil position " + index + " does not exist" );
            }
        }

        public int hashCodeOf(Tuple tuple, boolean left) {
            int hashCode = this.startResult;
            hashCode = PRIME * hashCode + this.index0.hashCodeOf( tuple, left );
            hashCode = PRIME * hashCode + this.index1.hashCodeOf( tuple, left );
            return rehash( hashCode );
        }

        public boolean equal(final Object right,
                             final Tuple tuple) {
            return this.index0.evaluator.evaluate( null,
                                                   this.index0.declaration.getExtractor(),
                                                   tuple.getObject( this.index0.declaration ),
                                                   this.index0.extractor,
                                                   right )
                   &&
                   this.index1.evaluator.evaluate( null,
                                                   this.index1.declaration.getExtractor(),
                                                   tuple.getObject( this.index1.declaration ),
                                                   this.index1.extractor,
                                                   right );
        }

        public boolean equal(final Tuple tuple1,
                             final Tuple tuple2) {
            return this.index0.evaluator.evaluate( null,
                                                   this.index0.declaration.getExtractor(),
                                                   tuple1.getObject( this.index0.declaration ),
                                                   this.index0.declaration.getExtractor(),
                                                   tuple2.getObject( this.index0.declaration ) )
                   &&
                   this.index1.evaluator.evaluate( null,
                                                   this.index1.declaration.getExtractor(),
                                                   tuple1.getObject( this.index1.declaration ),
                                                   this.index1.declaration.getExtractor(),
                                                   tuple2.getObject( this.index1.declaration ) );
        }

        public boolean equal(final Object object1,
                             final Object object2) {
            return this.index0.evaluator.evaluate( null,
                                                   this.index0.extractor,
                                                   object1,
                                                   this.index0.extractor,
                                                   object2 )
                   &&
                   this.index1.evaluator.evaluate( null,
                                                   this.index1.extractor,
                                                   object1,
                                                   this.index1.extractor,
                                                   object2 );
        }
    }

    public static class TripleCompositeIndex
        implements
        Index {

        private static final long serialVersionUID = 510l;

        private FieldIndex        index0;
        private FieldIndex        index1;
        private FieldIndex        index2;

        private int               startResult;

        public TripleCompositeIndex() {

        }

        public TripleCompositeIndex(final FieldIndex[] indexes,
                                    final int startResult) {
            this.startResult = startResult;

            this.index0 = indexes[0];
            this.index1 = indexes[1];
            this.index2 = indexes[2];
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            index0 = (FieldIndex) in.readObject();
            index1 = (FieldIndex) in.readObject();
            index2 = (FieldIndex) in.readObject();
            startResult = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( index0 );
            out.writeObject( index1 );
            out.writeObject( index2 );
            out.writeInt( startResult );
        }

        public FieldIndex getFieldIndex(int index) {
            switch ( index ) {
                case 0 :
                    return index0;
                case 1 :
                    return index1;
                case 2 :
                    return index2;
                default :
                    throw new IllegalArgumentException( "IndexUtil position " + index + " does not exist" );
            }
        }

        public int hashCodeOf(Tuple tuple, boolean left) {
            int hashCode = this.startResult;
            hashCode = PRIME * hashCode + this.index0.hashCodeOf( tuple, left );
            hashCode = PRIME * hashCode + this.index1.hashCodeOf( tuple, left );
            hashCode = PRIME * hashCode + this.index2.hashCodeOf( tuple, left );
            return rehash( hashCode );
        }

        public boolean equal(final Object right,
                             final Tuple tuple) {
            return this.index0.evaluator.evaluate( null,
                                                   this.index0.declaration.getExtractor(),
                                                   tuple.getObject( this.index0.declaration ),
                                                   this.index0.extractor,
                                                   right )
                   &&
                   this.index1.evaluator.evaluate( null,
                                                   this.index1.declaration.getExtractor(),
                                                   tuple.getObject( this.index1.declaration ),
                                                   this.index1.extractor,
                                                   right )
                   && this.index2.evaluator.evaluate( null,
                                                      this.index2.declaration.getExtractor(),
                                                      tuple.getObject( this.index2.declaration ),
                                                      this.index2.extractor,
                                                      right );
        }

        public boolean equal(final Tuple tuple1,
                             final Tuple tuple2) {
            return this.index0.evaluator.evaluate( null,
                                                   this.index0.declaration.getExtractor(),
                                                   tuple1.getObject( this.index0.declaration ),
                                                   this.index0.declaration.getExtractor(),
                                                   tuple2.getObject( this.index0.declaration ) )
                   &&
                   this.index1.evaluator.evaluate( null,
                                                   this.index1.declaration.getExtractor(),
                                                   tuple1.getObject( this.index1.declaration ),
                                                   this.index1.declaration.getExtractor(),
                                                   tuple2.getObject( this.index1.declaration ) )
                   &&
                   this.index2.evaluator.evaluate( null,
                                                   this.index2.declaration.getExtractor(),
                                                   tuple1.getObject( this.index2.declaration ),
                                                   this.index2.declaration.getExtractor(),
                                                   tuple2.getObject( this.index2.declaration ) );
        }

        public boolean equal(final Object object1,
                             final Object object2) {
            return this.index0.evaluator.evaluate( null,
                                                   this.index0.extractor,
                                                   object1,
                                                   this.index0.extractor,
                                                   object2 )
                   &&
                   this.index1.evaluator.evaluate( null,
                                                   this.index1.extractor,
                                                   object1,
                                                   this.index1.extractor,
                                                   object2 )
                   &&
                   this.index2.evaluator.evaluate( null,
                                                   this.index2.extractor,
                                                   object1,
                                                   this.index2.extractor,
                                                   object2 );
        }

    }

    public void clear() {
        this.table = new Entry[Math.min( this.table.length,
                                         16 )];
        this.threshold = (int) (this.table.length * this.loadFactor);
        this.size = 0;
        this.iterator = null;
    }
}
