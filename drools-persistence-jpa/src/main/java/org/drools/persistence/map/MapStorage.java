package org.drools.persistence.map;

import java.util.HashMap;
import java.util.Map;

import org.drools.persistence.info.SessionInfo;

public class MapStorage
    implements
    AbstractStorage {

    private Map<Long, SessionInfo> store;
    
    public MapStorage() {
        store = new HashMap<Long, SessionInfo>();
    }
    
    public SessionInfo findSessionInfo(Long id) {
        return store.get( id );
    }

    public void saveOrUpdate(SessionInfo object) {
        store.put( object.getId(), object );
    }

}
