package org.drools.factmodel.traits;


import org.drools.core.util.TripleStore;

import java.util.Map;

public class TripleBasedBean extends TripleBasedStruct {

    protected Object object;

    public Object getObject() {
        return object;
    }

    public TripleBasedBean( Object o, TripleStore store ) {
        this.store = store;
        this.object = o;
    }

}
