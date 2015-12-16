/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.factmodel.traits;


import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleStore;
import org.kie.api.runtime.rule.Variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;



public abstract class TripleBasedStruct implements Map<String, Object>, Externalizable {

    protected TripleStore store;

    protected String storeId;

    protected TripleFactory tripleFactory;

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( storeId );

        out.writeObject( store );

        out.writeObject( tripleFactory );

        out.writeObject( getObject() );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        storeId = (String) in.readObject();
        store = (TripleStore) in.readObject();
        tripleFactory = (TripleFactory) in.readObject();

        setObject( in.readObject() );
    }

    protected Triple propertyKey( Object property ) {
        return tripleFactory.newTriple( getObject(), property.toString(), Variable.v );
    }

    protected Triple propertyKey( String property ) {
        return tripleFactory.newTriple( getObject(), property, Variable.v );
    }

    protected Triple property( String property, Object value ) {
        return tripleFactory.newTriple( getObject(), property, value );
    }

    public int size() {
        return getTriplesForSubject( getObject() ).size();
    }


    public boolean isEmpty() {
        return size() == 0;
    }


    public boolean containsKey(Object key) {
        return store.get( propertyKey( key ) ) != null;
    }

    public boolean containsValue(Object value) {
        for ( Triple t : getTriplesForSubject( getObject() ) ) {
            if ( t.getValue() == null ) {
                if ( value == null ) {
                    return true;
                }
            } else {
                if ( t.getValue().equals( value ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public Object get(Object key) {
        Triple t = store.get( propertyKey( key ) );
        return t == null ? null : t.getValue();
    }

    public Object put(String key, Object value) {
        return store.put( property( key, value ) );
    }

    public Object remove(Object key) {
        return store.remove( store.get( propertyKey ( key ) ) );
    }

    public void clear() {
        for ( Triple t : getTriplesForSubject( getObject() ) ) {
            store.remove( t );
        }
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
        for ( String k : m.keySet() ) {
            put( k, m.get( k ) );
        }
    }

    public Set<String> keySet() {
        Set<String> set = new HashSet<String>();
        for ( Triple t : getTriplesForSubject( getObject() ) ) {
            set.add( (String) t.getProperty() );
        }
        return set;
    }

    public Collection<Object> values() {
        List<Object> values = new ArrayList<Object>();
        for ( Triple t : getTriplesForSubject( getObject() ) ) {
            values.add( t.getValue() );
        }
        return values;
    }

    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> set = new HashSet<Entry<String, Object>>();
        for ( Triple t : getTriplesForSubject( getObject() ) ) {
            set.add( TraitProxy.buildEntry( (String) t.getProperty(), t.getValue() ) );
        }
        return set;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if ( ! this.getClass().equals( o.getClass() ) ) return false;

        Object obj = getObject();
        Object other = ((TripleBasedStruct) o).getObject();
        if ( obj == other ) return true;

        Collection<Triple> l1 = getTriplesForSubject( obj );
        Collection<Triple> l2 = getTriplesForSubject( other );
        if ( l1.size() != l2.size() ) {
            return false;
        }
        for ( Triple t : l1 ) {
            if (! l2.contains(t) ) {
                return false;
            }
        }
        return true;
    }


    protected Collection<Triple> getTriplesForSubject( Object subj ) {
        Collection<Triple> coll = store.getAll( tripleFactory.newTriple( subj, Variable.v, Variable.v ) );
        Iterator<Triple> iter = coll.iterator();
        while ( iter.hasNext() ) {
            if ( (iter.next().getProperty()).equals(TripleStore.TYPE) ) {
                iter.remove();
            }
        }
        return coll;
    }

    public abstract Object getObject();
    public abstract void setObject( Object o );

    @Override
    public String toString() {
        return "TripleBasedStruct{" +
                "store=" + store +
                ", storeId='" + storeId + '\'' +
                '}';
    }

    public TripleFactory getTripleFactory() {
        return tripleFactory;
    }

    public void setTripleFactory( TripleFactory factory ) {
        this.tripleFactory = factory;
    }
}
