package org.drools.core.util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.util.ObjectHashMap.ObjectEntry;


public class TripleStore extends AbstractHashTable {
    
    public TripleStore() {
        super();
        this.comparator = new TripleKeyComparator();
    }

    public TripleStore(final int capacity,
                             final float loadFactor) {
        super(capacity, loadFactor);
        this.comparator = new TripleKeyComparator();
    }

    public TripleStore(final Entry[] table) {
        super( table );
        this.comparator = new TripleKeyComparator();
    }

    public TripleStore(final float loadFactor,
                             final Entry[] table) {
       super( loadFactor, table );
       this.comparator = new TripleKeyComparator();
    }
    
    public boolean put(final Triple triple) {
        return put( triple, true );
    }
    
    public boolean put(final Triple triple,
                       final boolean checkExists) {
        final int hashCode = this.comparator.hashCodeOf( triple );
        final int index = indexOf( hashCode,
                                   this.table.length );

        // scan the linked entries to see if it exists
        if ( checkExists ) {
            TripleImpl current = (TripleImpl) this.table[index];
            while ( current != null ) {
                if ( hashCode == this.comparator.hashCodeOf( current ) && this.comparator.equal( triple,
                                                                                                 current ) ) {
                    current.setValue(triple.getValue());;
                    return true;
                }
                current = (TripleImpl) current.getNext();
            }
        }

        // We aren't checking the key exists, or it didn't find the key
        TripleImpl timpl = ( TripleImpl ) triple;
        timpl.setNext( this.table[index] );
        this.table[index] = timpl;

        if ( this.size++ >= this.threshold ) {
            resize( 2 * this.table.length );
        }
        return false;
    }    
    
    public Triple get(final Triple triple) {
        final int hashCode = this.comparator.hashCodeOf( triple );
        final int index = indexOf( hashCode,
                                   this.table.length );

        // scan the linked entries to see if it exists
        TripleImpl current = (TripleImpl) this.table[index];
        while ( current != null ) {
            if ( hashCode == this.comparator.hashCodeOf( current ) && this.comparator.equal( triple,
                                                                                             current ) ) {
                return current;
            }
            current = (TripleImpl) current.getNext();
        }
        
        return null;
    }
    
    public boolean remove(final Triple triple) {
        final int hashCode = this.comparator.hashCodeOf( triple );
        final int index = indexOf( hashCode,
                                   this.table.length );

        TripleImpl previous = (TripleImpl) this.table[index];
        TripleImpl current = previous;
        while ( current != null ) {
            final TripleImpl next = (TripleImpl) current.getNext();
            if ( hashCode == this.comparator.hashCodeOf( current ) && this.comparator.equal( triple,
                                                                                             current ) ) {
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

    @Override
    public Entry getBucket(Object object) {
        final int hashCode = this.comparator.hashCodeOf( object );
        final int index = indexOf( hashCode,
                                   this.table.length );

        return this.table[index];
    }
    
    public static class TripleKeyComparator implements ObjectComparator {

        public void writeExternal(ObjectOutput out) throws IOException {
            throw new UnsupportedOperationException();
  
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {  
            throw new UnsupportedOperationException();
        }

        public int hashCodeOf(Object object) {
            Triple t = ( Triple ) object;
            final int prime = 31;
            int result = 1;
            result = prime * result + t.getInstance().hashCode();
            result = prime * result + t.getProperty().hashCode();
            return result;
        }

        public int rehash(int h) {
            throw new UnsupportedOperationException();
        }

        public boolean equal(Object object1,
                             Object object2) {
            Triple t1 = ( Triple ) object1;
            Triple t2 = ( Triple ) object2;
            // Assuming == for core instance check is fine.
            return t1.getInstance() == t2.getInstance() && t1.getProperty().equals( t2.getProperty() );
        }
        
    }

}
