/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.persistence.jpa;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.drools.persistence.api.PersistenceContext;
import org.drools.persistence.api.PersistentSession;
import org.drools.persistence.api.PersistentWorkItem;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerHelper;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.kie.api.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaPersistenceContext implements PersistenceContext {

    private static Logger logger = LoggerFactory.getLogger(JpaPersistenceContext.class);
    
    private EntityManager em;
    protected final boolean isJTA;
    protected final boolean pessimisticLocking;
    protected final TransactionManager txm;
    
    public JpaPersistenceContext(EntityManager em, TransactionManager txm) {
        this(em, true, false, txm);
    }
    
    public JpaPersistenceContext(EntityManager em, boolean isJTA, TransactionManager txm) {
       this(em, isJTA, false, txm);
    }
    
    public JpaPersistenceContext(EntityManager em, boolean isJTA, boolean locking, TransactionManager txm) {
        this.em = em;
        this.isJTA = isJTA;
        this.pessimisticLocking = locking;
        this.txm = txm;
    }

    public PersistentSession persist(PersistentSession entity) {
        this.em.persist( entity );
        TransactionManagerHelper.addToUpdatableSet(txm, entity);
        if( this.pessimisticLocking ) {
            this.em.flush();
            return this.em.find(SessionInfo.class, entity.getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT );
        }
        return entity;
    }

    public PersistentSession findSession(Long id) {

        SessionInfo sessionInfo = null;
        if( this.pessimisticLocking ) {
            sessionInfo = this.em.find( SessionInfo.class, id, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
            TransactionManagerHelper.addToUpdatableSet(txm, sessionInfo);
            return sessionInfo;
        }
        sessionInfo = this.em.find( SessionInfo.class, id );

        TransactionManagerHelper.addToUpdatableSet(txm, sessionInfo);

        return sessionInfo;
    }

    public void remove(PersistentSession session) {
        if (!em.contains(session)) {
            SessionInfo s = em.getReference(SessionInfo.class, session.getId());
            em.remove( s );
        } else {
            em.remove(session);
        }
        TransactionManagerHelper.removeFromUpdatableSet(txm, session);
        em.flush();
    }
    
    public void lock(PersistentSession session) {
    	this.em.lock( session, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
    }
    
    public boolean isOpen() {
        return this.em.isOpen();
    }

    public void joinTransaction() {
    	if (isJTA) {
    	    this.em.joinTransaction();
    	}
    }

    public void close() {
        this.em.close();
    }

    public PersistentWorkItem persist(PersistentWorkItem workItem) {
        em.persist( workItem );
        TransactionManagerHelper.addToUpdatableSet(txm, workItem);
        if( this.pessimisticLocking ) {
            this.em.flush();
            return em.find(WorkItemInfo.class, workItem.getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        }

        return workItem;
    }

    public PersistentWorkItem findWorkItem(Long id) {
        WorkItemInfo workItemInfo = null;
        if( this.pessimisticLocking ) {
            workItemInfo = this.em.find( WorkItemInfo.class, id, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
            TransactionManagerHelper.addToUpdatableSet(txm, workItemInfo);

            return workItemInfo;
        }
        workItemInfo = em.find( WorkItemInfo.class, id );

        TransactionManagerHelper.addToUpdatableSet(txm, workItemInfo);

        return workItemInfo;
    }

    public void remove(PersistentWorkItem workItem) {
        em.remove( workItem );
        TransactionManagerHelper.removeFromUpdatableSet(txm, workItem);
    }

    public PersistentWorkItem merge(PersistentWorkItem workItem) {
        if( this.pessimisticLocking ) { 
            if( em.contains(workItem) ) { 
                em.lock(workItem, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            } else { 
                // Yes, this is a hack, but for detached entities, it's the only way to lock before merging
                WorkItemInfo dbWorkItemInfo = em.find(WorkItemInfo.class, workItem.getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT);
                for( Field field : WorkItemInfo.class.getDeclaredFields() ) { 
                    boolean access = field.isAccessible();
                    field.setAccessible(true);
                    try {
                        field.set(dbWorkItemInfo, field.get(workItem));
                    } catch (Exception e) {
                        logger.error("Unable to set field " + field.getName() + " of unmerged WorkItemInfo instance!", e);
                    } 
                    field.setAccessible(access);
                }
            }
        }
        TransactionManagerHelper.addToUpdatableSet(txm, workItem);
        return em.merge( workItem );
    }
    
    public void lock(PersistentWorkItem workItem) {
    	this.em.lock( workItem, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
    }
    
    protected EntityManager getEntityManager() {
        return this.em;
    }
 }  
