/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import org.kie.api.runtime.rule.Variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class TripleStore extends AbstractHashTable implements Externalizable {


    public static final String TYPE = "rdfs:type";
    public static final String PROXY = "drools:proxy";
    public static final String VALUE = "drools:hasValue";

    private String id;

    public TripleStore( ) {
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

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        id = (String) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean put(final Triple triple) {
        boolean ret = put( triple, true );
        return ret;
    }

    public boolean add( final Triple triple ) {
        return put(triple, false);
    }

    public boolean put(final Triple triple,
                       final boolean checkExists) {
        final int hashCode = this.comparator.hashCodeOf( triple );
        final int index = indexOf( hashCode,
                this.table.length );


        // scan the linked entries to see if it exists
        if ( checkExists ) {
            Object val = triple.getValue();
            ((TripleImpl) triple).setValue( Variable.v );
            TripleImpl current = (TripleImpl) this.table[index];
            while ( current != null ) {
                if ( hashCode == this.comparator.hashCodeOf( current ) && this.comparator.equal( triple,
                        current ) ) {
                    current.setValue( val );
                    return true;
                }
                current = (TripleImpl) current.getNext();
            }
            ((TripleImpl) triple).setValue( val );
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

    public Collection<Triple> getAll(final Triple triple) {
        List<Triple> list = new ArrayList<Triple>();

        if ( triple.getInstance() != Variable.v && triple.getProperty() != Variable.v ) {
            Triple t =  get( triple );
            if ( t != null ) {

                Triple current = t;
                while ( current != null ) {
                    if ( this.comparator.equal( triple,current ) ) {
                        list.add( current );
                    }
                    current = (Triple) current.getNext();
                }

            }
            return list;
        }

        Iterator iter = this.iterator();
        Triple tx;
        while ( ( tx = ((Triple) iter.next()) ) != null ) {
//            while ( tx != null ) {
//                Triple current = tx;
//                while ( current != null ) {
                    if ( this.comparator.equal( triple, tx ) ) {
                        list.add( tx );
                    }
//                    current = (Triple) current.remove();
//                }
//                tx = (TripleImpl) tx.remove();
//            }
        }

        return list;
    }



    public int removeAll(final Triple triple) {
        int removed = 0;
        Collection<Triple> coll = getAll( triple );
        for ( Triple t : coll ) {
            if ( remove(t) ) {
                removed++;
            }
        }
        return removed;
    }


    public boolean remove(final Triple triple) {
        final int hashCode = this.comparator.hashCodeOf( triple );
        final int index = indexOf( hashCode,
                this.table.length );

        TripleImpl previous = (TripleImpl) this.table[index];
        TripleImpl current = previous;

        Triple key = new TripleImpl( triple.getInstance(), triple.getProperty(), Variable.v );

        while ( current != null ) {
            final TripleImpl next = (TripleImpl) current.getNext();
            if ( hashCode == this.comparator.hashCodeOf( current ) && this.comparator.equal( key, current ) ) {

                if ( ( current.getValue() == null && triple.getValue() == null )
                       || ( current.getValue() != null && current.getValue().equals( triple.getValue() ) ) ) {
                    if ( previous == current ) {
                        this.table[index] = next;
                    } else {
                        previous.setNext( next );
                    }
                    current.setNext( null );
                    this.size--;
                    return true;
                }
            }
            previous = current;
            current = next;
        }
        return false;
    }



    public boolean contains( final Triple triple ) {
        final int hashCode = this.comparator.hashCodeOf( triple );
        final int index = indexOf( hashCode,
                this.table.length );

        // scan the linked entries to see if it exists
        TripleImpl current = (TripleImpl) this.table[index];
        while ( current != null ) {
            if ( hashCode == this.comparator.hashCodeOf( current ) ) {
                if ( this.comparator.equal( triple, current ) ) {
                    return true;
                }
            }
            current = (TripleImpl) current.getNext();
        }
        return false;
    }

    @Override
    public int getResizeHashcode(Entry entry) {
        // TripleStore never caches the hashcode, so it must be recomputed, which is also rehashed.
        return this.comparator.hashCodeOf( entry );
    }     

    public static class TripleKeyComparator implements ObjectComparator {

        public void writeExternal(ObjectOutput out) throws IOException {
//            throw new UnsupportedOperationException();

        }

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
//            throw new UnsupportedOperationException();
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

            if ( t1.getInstance() != Variable.v ) {
                if ( t1.getInstance() == null ) {
                    return false;
                } else if ( t1.getInstance() instanceof String ) {
                    if ( ! t1.getInstance().equals( t2.getInstance() ) ) {
                        return false;
                    }
                } else if ( t1.getInstance() != t2.getInstance() ) {
                    return false;
                }
            }

            if ( t1.getProperty() != Variable.v && ! t1.getProperty().equals( t2.getProperty() ) ) {
                return false;
            }
            if ( t1.getValue() != Variable.v ) {
                if ( t1.getValue() == null ) {
                    return t2.getValue() == null;
                } else {
                    return t1.getValue().equals( t2.getValue() );
                }
            }
            return true;
//            // Assuming == for core instance check is fine.
//            return t1.getInstance() == t2.getInstance()  && t1.getProperty().equals( t2.getProperty() );
        }

    }

}
