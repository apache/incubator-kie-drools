package org.drools.factmodel.traits;


import org.drools.core.util.TripleStore;

import java.util.HashMap;
import java.util.Map;

public class TripleStoreRegistry {
    
    private static Map<String, TripleStore> registry = new HashMap<String, TripleStore>();
    
    public static TripleStore getRegistry( String key ) {
        TripleStore store = registry.get( key );
        if ( store == null ) {
            store = new TripleStore( 500, 0.6f );
            store.setId( key );
            registry.put( key, store );
        }
        return store;
    }

}
