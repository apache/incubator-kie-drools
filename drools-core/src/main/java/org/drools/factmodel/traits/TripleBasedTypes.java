package org.drools.factmodel.traits;

import org.drools.core.util.Triple;
import org.drools.core.util.TripleImpl;
import org.drools.core.util.TripleStore;
import org.drools.runtime.rule.Variable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;


public class TripleBasedTypes extends TripleBasedStruct {

    protected Object object;


    public TripleBasedTypes( ) { }


    public TripleBasedTypes(Object obj, TripleStore store) {
        this.store = store;
        this.storeId = store.getId();
        this.object = obj;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        System.out.println(" written a " + this.getClass().getName());
        
//        int N = getSchemaTriplesForSubject( getObject() ).size();
//        out.writeInt( N );
//        for ( Triple t : getSchemaTriplesForSubject( getObject() ) ) {
//            System.out.println("Exting " + t );
//            out.writeObject( new TripleImpl( null, t.getProperty(), t.getValue() ) );
//        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );

        


//
//        int N = in.readInt();
//        for ( int j = 0; j < N; j++ ) {
//
//            Collection x = this.getSchemaTriplesForSubject( getObject() );
//
//            Triple t = (Triple) in.readObject();
//            ((TripleImpl) t).setInstance( getObject() );
//            System.out.println("Inned " + t + " , " + this.getSchemaTriplesForSubject( getObject() ).size() );
//            store.put( t, false );
//        }
        
        
        System.out.println(" ridden a " + this.getClass().getName() + " " + store);

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
                return store.contains( new TripleImpl( t.getValue(), TripleStore.PROXY, value ) );
            }
        }
        return false;
    }


    public Object get( Object key ) {
        Triple t = store.get( new TripleImpl( key, TripleStore.PROXY, Variable.v ) );
        return t == null ? null : t.getValue();
    }


    public Object put( String key, Object value ) {
        store.put( new TripleImpl( key, TripleStore.PROXY, value ), false );
        Object ret =  store.add(property(TripleStore.TYPE, key));
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
//                values.add( store.get( new TripleImpl( t.getValue(), TripleStore.PROXY, null ) ).getValue() );
                values.add( t.getValue() );
            }
        }
        return values;
    }

    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> set = new HashSet<Entry<String, Object>>();
        for ( Triple t : getSchemaTriplesForSubject( getObject() ) ) {
//            Triple proxy = store.get( new TripleImpl( t.getValue(), TripleStore.PROXY, Variable.v ) );
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
        return store.getAll( new TripleImpl( subj, TripleStore.TYPE, Variable.v ) );
    }


    @Override
    public String toString() {
        return "TripleBasedTypes{" +
                "object=" + object +
                "schema=" + getSchemaTriplesForSubject( object ) +
                "} " + super.toString();
    }

    public Triple getProxyTripleByTraitType( Object key ) {
        Collection<Triple> candidates = store.getAll( new TripleImpl( key, TripleStore.PROXY, Variable.v ) );
        for ( Triple t : candidates ) {
            if ( ((TraitProxy) t.getValue() ).getObject() == object ) {
                return t;
            }
        }
        return null;
    }
}
