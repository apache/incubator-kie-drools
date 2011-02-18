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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ObjectHashSet extends AbstractHashTable {

    private static final long serialVersionUID = 510l;

    public ObjectHashSet() {
        this( 16,
              0.75f );
    }

    public ObjectHashSet(final int capacity,
                         final float loadFactor) {
        super( capacity,
               loadFactor );
    }

    public ObjectHashSet(final Entry[] table) {
        super( 0.75f,
               table );
    }

    public ObjectHashSet(final float loadFactor,
                         final Entry[] table) {
        super( loadFactor,
               table );
    }

    public boolean add(final Object value) {
        return add( value,
                    true );
    }

    public void clear() {
        this.table = new Entry[Math.min( this.table.length,
                                         16 )];
        this.threshold = (int) (this.table.length * this.loadFactor);
    }

    public boolean add(final Object value,
                       final boolean checkExists) {
        final int hashCode = this.comparator.hashCodeOf( value );
        final int index = indexOf( hashCode,
                                   this.table.length );

        // scan the linked entries to see if it exists
        if ( checkExists ) {
            ObjectEntry current = (ObjectEntry) this.table[index];
            while ( current != null ) {
                if ( hashCode == current.hashCode && this.comparator.equal( value,
                                                                            current.value ) ) {
                    final Object oldValue = current.value;
                    current.value = value;
                    return true;
                }
                current = (ObjectEntry) current.getNext();
            }
        }

        // We aren't checking the key exists, or it didn't find the key
        final ObjectEntry entry = new ObjectEntry( value,
                                                   hashCode );
        entry.next = this.table[index];
        this.table[index] = entry;

        if ( this.size++ >= this.threshold ) {
            resize( 2 * this.table.length );
        }
        return false;
    }

    public boolean contains(final Object value) {
        final int hashCode = this.comparator.hashCodeOf( value );
        final int index = indexOf( hashCode,
                                   this.table.length );

        ObjectEntry current = (ObjectEntry) this.table[index];
        while ( current != null ) {
            if ( hashCode == current.hashCode && this.comparator.equal( value,
                                                                        current.value ) ) {
                return true;
            }
            current = (ObjectEntry) current.getNext();
        }
        return false;
    }

    public boolean remove(final Object value) {
        final int hashCode = this.comparator.hashCodeOf( value );
        final int index = indexOf( hashCode,
                                   this.table.length );

        ObjectEntry previous = (ObjectEntry) this.table[index];
        ObjectEntry current = previous;
        while ( current != null ) {
            final ObjectEntry next = (ObjectEntry) current.getNext();
            if ( hashCode == current.hashCode && this.comparator.equal( value,
                                                                        current.value ) ) {
                if ( previous == current ) {
                    this.table[index] = next;
                } else {
                    previous.setNext( next );
                }
                current.setNext( null );
                this.size--;
                return true;
            }
            previous = current;
            current = next;
        }
        return false;
    }

    public Entry getBucket(final Object object) {
        final int hashCode = this.comparator.hashCodeOf( object );
        final int index = indexOf( hashCode,
                                   this.table.length );

        return this.table[index];
    }

    public Object[] toArray(Object[] objects) {
        Iterator it = iterator();
        int i = 0;
        for ( ObjectEntry entry = ( ObjectEntry) it.next(); entry != null; entry = ( ObjectEntry ) it.next() ) {
            objects[i++] = entry.getValue();
        }
        return objects;
    }

    public static class ObjectEntry
        implements
        Entry,
        Externalizable  {

        private static final long serialVersionUID = 510l;

        private Object            value;

        private int               hashCode;

        private Entry             next;

        public ObjectEntry() {

        }

        public ObjectEntry(final Object value,
                           final int hashCode) {
            this.value = value;
            this.hashCode = hashCode;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            value   = in.readObject();
            hashCode    = in.readInt();
            next    = (Entry)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(value);
            out.writeInt(hashCode);
            out.writeObject(next);
        }
        public Object getValue() {
            return this.value;
        }

        public Entry getNext() {
            return this.next;
        }

        public void setNext(final Entry next) {
            this.next = next;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(final Object object) {
            if ( object == this ) {
                return true;
            }

            // assumes we never have null or wrong class

            final ObjectEntry other = (ObjectEntry) object;
            return this.value.equals( other.value );
        }
    }
}
