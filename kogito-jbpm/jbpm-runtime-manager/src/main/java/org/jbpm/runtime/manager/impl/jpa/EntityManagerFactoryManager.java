/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
		for (EntityManagerFactory emf : entityManagerFactories.values()) {
			if (emf.isOpen()) {
				emf.close();
			}
		}
		entityManagerFactories.clear();
	}
}
