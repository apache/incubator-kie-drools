package org.drools.persistence.map;

import java.util.HashMap;
import java.util.Map;

import org.drools.persistence.EntityInfo;

public class MapStorage
    implements
    AbstractStorage {

    private Map<Long, EntityInfo> store;
    
    public MapStorage() {
        store = new HashMap<Long, EntityInfo>();
    }
    
    public EntityInfo find(Long id) {
        return store.get( id );
    }

    public void saveOrUpdate(EntityInfo object) {
        store.put( object.getId(), object );
    }

}
