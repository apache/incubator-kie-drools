package org.drools.factmodel.traits;

import org.drools.core.util.Triple;
import org.drools.core.util.TripleImpl;
import org.drools.core.util.TripleStore;
import org.drools.runtime.rule.Variable;

import java.util.*;


public class TripleBasedTypes extends TripleBasedStruct {

    protected Object object;



    public TripleBasedTypes(Object obj, TripleStore store) {
        this.store = store;
        this.object = obj;
    }

    public Object getObject() {
        return object;
    }




    public int size() {
        return getSchemaTriplesForSubject(getObject()).size();
    }


    public boolean containsKey(Object key) {
        return store.contains( property( TripleStore.TYPE, key ) );
    }

    public boolean containsValue(Object value) {
        for ( Triple t : getSchemaTriplesForSubject(getObject()) ) {
            if (t.getProperty().equals( TripleStore.TYPE ) ) {
                return store.contains( new TripleImpl( t.getValue(), TripleStore.PROXY, value ) );
            }
        }
        return false;
    }


    public Object get(Object key) {
        Triple t = store.get( new TripleImpl( key, TripleStore.PROXY, Variable.v ) );
        return t == null ? null : t.getValue();
    }


    public Object put(String key, Object value) {
        store.put( new TripleImpl( key, TripleStore.PROXY, value ) );
        Object ret =  store.add(property(TripleStore.TYPE, key));
        return ret;
    }


    public Object remove(Object key) {
        Triple t = store.get( new TripleImpl( key, TripleStore.PROXY, Variable.v ) );
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
            values.add( store.get( new TripleImpl( t.getValue(), TripleStore.PROXY, null ) ).getValue() );
        }
        return values;
    }

    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> set = new HashSet<Entry<String, Object>>();
        for ( Triple t : getSchemaTriplesForSubject( getObject() ) ) {
            Triple proxy = store.get( new TripleImpl(t.getValue(), TripleStore.PROXY, Variable.v ) );
            set.add( TraitProxy.buildEntry( (String) t.getValue(), proxy.getValue() ) );
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
        return store.getAll( new TripleImpl( subj, TripleStore.TYPE, Variable.v ) );
    }
}
