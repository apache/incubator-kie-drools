package org.drools.persistence.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.persistence.EntityInfo;
import org.drools.persistence.PersistenceContext;

public class MapBasedPersistenceContext
    implements
    PersistenceContext,
    NonTransactionalPersistentSession {
    
    private Map<Long, EntityInfo> entities;
    private boolean open;
    private AbstractStorage storage;
    
    public MapBasedPersistenceContext(AbstractStorage storage) {
        open = true;
        this.storage = storage;
        this.entities = new HashMap<Long, EntityInfo>();
    }
    
    public void persist(Object entity) {
        EntityInfo entityInfo = (EntityInfo) entity;
        entities.put( entityInfo.getId(), entityInfo );
    }

    public <T> T find(Class<T> entityClass,
                      Object primaryKey) {
        Long id = (Long) primaryKey;
        EntityInfo entityInfo = entities.get( id );
        if(entityInfo == null)
            entityInfo = storage.find( id );
        return (T) entityInfo;
    }

    public boolean isOpen() {
        return open;
    }

    public void joinTransaction() {
    }

    public void close() {
        open = false;
        entities.clear();
    }

    public void clear() {
    }

    public List<EntityInfo> getStoredObjects() {
        return new ArrayList<EntityInfo>(entities.values());
    }

}
