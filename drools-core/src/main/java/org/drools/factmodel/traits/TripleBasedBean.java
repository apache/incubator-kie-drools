package org.drools.factmodel.traits;


import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleImpl;
import org.drools.core.util.TripleStore;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class TripleBasedBean extends TripleBasedStruct {

    public static TripleFactory tripleFactory = TraitFactory.tripleFactory;

    protected Object object;

    public TripleBasedBean() { }

    public TripleBasedBean( Object o, TripleStore store ) {
        this.store = store;
        this.storeId = store.getId();
        this.object = o;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );

        int N = getTriplesForSubject( getObject() ).size();
        out.writeInt( N );
        for ( Triple t : getTriplesForSubject( getObject() ) ) {
            out.writeObject( tripleFactory.newTriple( null, t.getProperty(), t.getValue() ) );
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );

        int N = in.readInt( );
        for ( int j = 0; j < N; j++ ) {
            Triple t = (Triple) in.readObject();
            ((TripleImpl) t).setInstance( getObject() );
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
