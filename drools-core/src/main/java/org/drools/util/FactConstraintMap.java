package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.spi.FieldExtractor;

/***********************************/
    public class FactConstraintMap {
        static final int       MAX_CAPACITY = 1 << 30;

        private int            size;
        private int            threshold;
        private float          loadFactor;

        private Entry[]        table;

        private int            fieldIndex;
        private FieldExtractor extractor;

        final int              PRIME        = 31;

        private final int      startResult;

        public FactConstraintMap(int fieldIndex,
                                 FieldExtractor extractor) {
            this( 16,
                  0.75f,
                  fieldIndex,
                  extractor );
        }

        public FactConstraintMap(int capacity,
                                 float loadFactor,
                                 int fieldIndex,
                                 FieldExtractor extractor) {
            this.loadFactor = loadFactor;
            this.threshold = (int) (capacity * loadFactor);
            this.table = new TupleEntry[capacity];
            this.fieldIndex = fieldIndex;
            this.extractor = extractor;

            this.startResult = PRIME + this.fieldIndex;
        }

        public void resize(int newCapacity) {
            Entry[] oldTable = this.table;
            int oldCapacity = oldTable.length;
            if ( oldCapacity == MAX_CAPACITY ) {
                this.threshold = Integer.MAX_VALUE;
                return;
            }

            Entry[] newTable = new Entry[newCapacity];

            for ( int i = 0; i < this.table.length; i++ ) {
                Entry entry = this.table[i];
                if ( entry == null ) {
                    continue;
                }
                this.table[i] = null;
                Entry next = null;
                while ( entry != null ) {
                    next = entry.getNext();

                    int index = entry.hashCode() % newTable.length;
                    entry.setNext( newTable[index] );
                    newTable[index] = entry;

                    entry = next;
                }
            }

            this.table = newTable;
            this.threshold = (int) (newCapacity * this.loadFactor);
        }

        public void add(InternalFactHandle handle) {
            Object value = this.extractor.getValue( handle.getObject() );
            int hashCode = PRIME * startResult + ((value == null) ? 0 : value.hashCode());

//            FactEntry factEntry = new FactEntry( handle );
//            getOrCreate( hashCode,
//                         this.fieldIndex,
//                         value ).add( factEntry );
        }

        public FieldIndexEntry getOrCreate(int hashCode,
                                          int fieldIndex,
                                          Object value) {
            int index = hashCode % this.table.length;
            FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

            while ( entry != null ) {
                if ( hashCode == entry.hashCode() && value.equals( entry.getValue() ) ) {
                    return entry;
                }
                entry = (FieldIndexEntry) entry.getNext();
            }

            if ( entry == null ) {
                entry = new FieldIndexEntry( this.extractor,
                                            fieldIndex,
                                            hashCode );
                entry.setNext( this.table[index] );
                this.table[index] = entry;

                if ( this.size++ >= this.threshold ) {
                    resize( 2 * this.table.length );
                }
            }
            return entry;
        }

        public Entry get(Entry entry) {
            int index = entry.hashCode() % this.table.length;
            Entry current = this.table[index];
            while ( current != null ) {
                if ( entry.hashCode() == current.hashCode() && entry.equals( current ) ) {
                    return current;
                }
                current = current.getNext();
            }
            return null;
        }

        public Entry remove(TupleEntry tuple) {
            int index = tuple.hashCode() % this.table.length;
            Entry current = this.table[index];
            Entry previous = current;
            if ( current != null ) {
                if ( tuple.hashCode() == current.hashCode() && tuple.equals( current ) ) {
                    previous.setNext( current.getNext() );
                    return current;
                }
                previous = current;
            }
            return null;
        }

        public Entry getBucket(int hashCode) {
            int index = hashCode % this.table.length;
            return this.table[index];
        }

        public int size() {
            return this.size;
        }
    }