/*
 * Copyright 2015 JBoss Inc
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TripleBasedTypes extends TripleBasedStruct {

    protected Object object;

    public TripleBasedTypes( ) { }

    public TripleBasedTypes( Object obj, TripleStore store, TripleFactory factory ) {
        super();
        this.store = store;
        this.storeId = store.getId();
        this.object = obj;
        this.tripleFactory = factory;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );
    }

    public Object getObject() {
        return object;
    }

    public void setObject( Object o ) {
        object = o;
    }

    public int size() {
        return getSchemaTriplesForSubject( getObject() ).size();
    }


    public boolean containsKey(Object key) {
        return store.contains( property( TripleStore.TYPE, key ) );
    }

    public boolean containsValue( Object value ) {
        for ( Triple t : getSchemaTriplesForSubject(getObject()) ) {
            if (t.getProperty().equals( TripleStore.TYPE ) ) {
                return store.contains( tripleFactory.newTriple( t.getValue(), TripleStore.PROXY, value ) );
            }
        }
        return false;
    }


    public Object get( Object key ) {
        Triple t = store.get( tripleFactory.newTriple( key, TripleStore.PROXY, Variable.v ) );
        while ( t != null ) {
            Object o = t.getValue();
            if ( o instanceof TraitProxy && (( TraitProxy ) o ).getObject() == this.object ) {
                return o;
            } else {
                t = (Triple) t.getNext();
            }
        }
        return null;
    }


    public Object put( String key, Object value ) {
        store.put( tripleFactory.newTriple( key, TripleStore.PROXY, value ), false );
        Object ret =  store.add( property( TripleStore.TYPE, key ) );
        return ret;
    }


    public Object remove( Object key ) {
        Triple t = getProxyTripleByTraitType( key );
        if ( t == null ) {
            return false;
        }

        store.remove( t );
        store.remove( property( TripleStore.TYPE, key ) );
        return t.getValue();
    }

    public void clear() {
        for ( Triple t : getSchemaTriplesForSubject(getObject()) ) {
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
        for ( Triple t : getSchemaTriplesForSubject( getObject() ) ) {
            if ( t.getProperty().equals( TripleStore.TYPE ) ) {
                set.add( t.getValue().toString() );
            }
        }
        return set;
    }

    public Collection<Object> values() {
        List<Object> values = new ArrayList<Object>();
        for ( Triple t : getSchemaTriplesForSubject( getObject() ) ) {
            Triple x = getProxyTripleByTraitType( t.getValue() );
            if ( x != null ) {
//                values.add( store.get( tripleFactory.newTriple( t.getValue(), TripleStore.PROXY, null ) ).getValue() );
                values.add( x.getValue() );
            }
        }
        return values;
    }

    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> set = new HashSet<Entry<String, Object>>();
        for ( Triple t : getSchemaTriplesForSubject( getObject() ) ) {
//            Triple proxy = store.get( tripleFactory.newTriple( t.getValue(), TripleStore.PROXY, Variable.v ) );
//            set.add( TraitProxy.buildEntry( (String) t.getValue(), proxy.getValue() ) );
            Triple x = getProxyTripleByTraitType( t.getValue() );
            if ( x != null ) {
                set.add( TraitProxy.buildEntry( (String) t.getValue(), x.getValue() ) );
            }
        }
        return set;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if ( ! this.getClass().equals( o.getClass() ) ) return false;

        Object obj = getObject();
        Object other = ((TripleBasedStruct) o).getObject();
        if ( obj == other ) return true;

        Collection<Triple> l1 = getSchemaTriplesForSubject( obj );
        Collection<Triple> l2 = getSchemaTriplesForSubject( other );
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



    private Collection<Triple> getSchemaTriplesForSubject( Object subj ) {
        return store.getAll( tripleFactory.newTriple( subj, TripleStore.TYPE, Variable.v ) );
    }


    @Override
    public String toString() {
        return "TripleBasedTypes{" +
                "object=" + object +
                "schema=" + getSchemaTriplesForSubject( object ) +
                "} " + super.toString();
    }

    public Triple getProxyTripleByTraitType( Object key ) {
        Collection<Triple> candidates = store.getAll( tripleFactory.newTriple( key, TripleStore.PROXY, Variable.v ) );
        for ( Triple t : candidates ) {
            if ( ((TraitProxy) t.getValue() ).getObject() == object ) {
                return t;
            }
        }
        return null;
    }
}
