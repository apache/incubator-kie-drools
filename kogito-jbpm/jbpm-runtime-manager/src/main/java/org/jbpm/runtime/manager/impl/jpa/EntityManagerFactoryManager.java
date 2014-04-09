package org.jbpm.runtime.manager.impl.jpa;

import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerFactoryManager {

	private static EntityManagerFactoryManager INSTANCE = new EntityManagerFactoryManager();
	
    private ConcurrentHashMap<String, EntityManagerFactory> entityManagerFactories = new ConcurrentHashMap<String, EntityManagerFactory>();
    
    private EntityManagerFactoryManager() {
    	
    }
	public static EntityManagerFactoryManager get() {
		return INSTANCE;
	}
	
	public synchronized void addEntityManagerFactory(String pu, EntityManagerFactory emf) {
		EntityManagerFactory eManagerFactory = entityManagerFactories.get(pu);
		
		if (eManagerFactory == null || !eManagerFactory.isOpen()) {
			entityManagerFactories.put(pu, emf);
		}
	}
	
	public synchronized EntityManagerFactory getOrCreate(String pu) {
		EntityManagerFactory eManagerFactory = entityManagerFactories.get(pu);
		
		if (eManagerFactory == null || !eManagerFactory.isOpen()) {
			eManagerFactory = Persistence.createEntityManagerFactory(pu);
			entityManagerFactories.put(pu, eManagerFactory);
		}
		
		return eManagerFactory;
	}
	
	public synchronized EntityManagerFactory remove(String pu) {
		return entityManagerFactories.remove(pu);
	}
	
	public synchronized void clear() {
		entityManagerFactories.clear();
	}
}
