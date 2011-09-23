package org.drools.factmodel.traits;


import org.drools.core.util.Triple;
import org.drools.core.util.TripleImpl;
import org.drools.core.util.TripleStore;
import org.drools.runtime.rule.Variable;

import java.util.*;

public abstract class TripleBasedStruct implements Map<String, Object> {


    protected TripleStore store;


    protected TripleImpl key( Object property ) {
        return new TripleImpl( getObject(), property.toString(), Variable.v );
    }

    protected TripleImpl propertyKey( String property ) {
        return new TripleImpl( getObject(), property, Variable.v );
    }

    protected TripleImpl property( String property, Object value ) {
        return new TripleImpl( getObject(), property, value );
    }

    public int size() {
        return getTriplesForSubject( getObject() ).size();
    }


    public boolean isEmpty() {
        return size() == 0;
    }


    public boolean containsKey(Object key) {
        return store.get( key( key ) ) != null;
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
        Triple t = store.get( key( key ) );
        return t == null ? null : t.getValue();
    }

    public Object put(String key, Object value) {
        return store.put( property( key, value ) );
    }

    public Object remove(Object key) {
        return store.remove( store.get( key ( key ) ) );
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
        Collection<Triple> coll = store.getAll( new TripleImpl( subj, Variable.v, Variable.v ) );
        Iterator<Triple> iter = coll.iterator();
        while ( iter.hasNext() ) {
            if ( (iter.next().getProperty()).equals(TripleStore.TYPE) ) {
                iter.remove();
            }
        }
        return coll;
    }
    
    protected abstract Object getObject();
}
