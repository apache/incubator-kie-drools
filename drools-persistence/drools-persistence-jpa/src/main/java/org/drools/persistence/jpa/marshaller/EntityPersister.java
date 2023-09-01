package org.drools.persistence.jpa.marshaller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

public class EntityPersister {

    private EntityManager entityManager;
    private List<Object> entities;
    
    
    public EntityPersister(EntityManager em) {
        this.entityManager = em;
        this.entities = new ArrayList<>();
    }
    
    public EntityManager getEntityManager() {
        return this.entityManager;
    }
    
    public boolean isPersited(Object entity) {
        return entities.contains(entity);
    }
    
    public void processed(Object entity) {
        this.entities.add(entity);
    }
    
    public void close() {
        this.entities.clear();
        this.entityManager.close();
    }
}
