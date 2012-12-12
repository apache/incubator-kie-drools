/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

public class EntityManagerFactoryAndTracker implements EntityManagerFactory {

    private EntityManagerFactory emf;
    private HashSet<EntityManager> entityManagerMap = new HashSet<EntityManager>();

    public EntityManagerFactoryAndTracker(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager createEntityManager() {
        EntityManager em = emf.createEntityManager();
        entityManagerMap.add(em);
        return em;
    }

    public EntityManager createEntityManager(Map map) {
        EntityManager em = emf.createEntityManager(map);
        entityManagerMap.add(em);
        return em;
    }

    public void close() {
        // clean up all entity managers found.
        Iterator<EntityManager> iter = entityManagerMap.iterator();
        while (iter.hasNext()) {
            EntityManager em = iter.next();
            try {
                EntityTransaction etx = em.getTransaction();
                if (etx.isActive()) {
                    etx.rollback();
                }
            } catch (Throwable t) {
                // don't worry, it doesn't matter.
            } finally {
                try {
                    if (em.isOpen()) {
                        em.clear();
                        em.close();
                    }
                } catch (Throwable t) {
                    // do nothing..
                }
            }
        }
        emf.close();
    }

    public boolean isOpen() {
        return emf.isOpen();
    }

    /**
     * JPA 2 method.
     * {@inheritDoc}
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return this.emf.getCriteriaBuilder();
    }

    /**
     * JPA 2 method.
     * {@inheritDoc}
     */
    public Metamodel getMetamodel() {
        return this.emf.getMetamodel();
    }

    /**
     * JPA 2 method.
     * {@inheritDoc}
     */
    public Map<String, Object> getProperties() {
        return this.emf.getProperties();
    }

    /**
     * JPA 2 method.
     * {@inheritDoc}
     */
    public Cache getCache() {
        return this.emf.getCache();
    }

    /**
     * JPA 2 method.
     * {@inheritDoc}
     */
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return this.emf.getPersistenceUnitUtil();
    }

}
