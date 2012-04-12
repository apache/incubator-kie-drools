package org.drools.factmodel.traits;


import org.drools.core.util.Triple;
import org.drools.core.util.TripleImpl;
import org.drools.core.util.TripleStore;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class TripleBasedBean extends TripleBasedStruct {

    protected Object object;

    public TripleBasedBean() { }

    public TripleBasedBean( Object o, TripleStore store ) {
        this.store = store;
        this.storeId = store.getId();
        this.object = o;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        System.out.println(" written a " + this.getClass().getName() + " >>  " + object );

        int N = getTriplesForSubject( getObject() ).size();
        out.writeInt( N );
        for ( Triple t : getTriplesForSubject( getObject() ) ) {
            System.out.println("Exting " + t );
            out.writeObject( new TripleImpl( null, t.getProperty(), t.getValue() ) );
        }

    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );

        System.out.println(" ridden a " + this.getClass().getName() + " >> " + object + " ! " + store);

        int N = in.readInt( );
        for ( int j = 0; j < N; j++ ) {
            Triple t = (Triple) in.readObject();
            ((TripleImpl) t).setInstance( getObject() );
            System.out.println("Inned " + t );
            store.put( t, false );
        }


    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String toString() {
        return "TBB " + storeId;
    }
}
