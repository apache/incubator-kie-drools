package org.drools.marshalling.util;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


public class MarshallingEntityManagerFactory implements EntityManagerFactory {

    private EntityManagerFactory emf;
    
    public MarshallingEntityManagerFactory(EntityManagerFactory realEmf) { 
        this.emf = realEmf;
    }
    
    public EntityManager createEntityManager() {
        EntityManager realEm = emf.createEntityManager();
        return new MarshallingEntityManager(realEm);
    }

    public EntityManager createEntityManager(Map map) {
        EntityManager realEm = emf.createEntityManager(map);
        return new MarshallingEntityManager(realEm);
    }

    public void close() {
        emf.close();
    }

    public boolean isOpen() {
        return emf.isOpen();
    }

}
